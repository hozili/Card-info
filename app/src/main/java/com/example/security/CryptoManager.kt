package com.example.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.json.JSONObject

object CryptoManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val MASTER_KEY_ALIAS = "BankCardVaultMasterKey"
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val PBKDF2_ITERATIONS = 12000
    private const val KEY_LENGTH = 256

    private val secureRandom = SecureRandom()

    init {
        initMasterKey()
    }

    private fun initMasterKey() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            if (!keyStore.containsAlias(MASTER_KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
                val spec = KeyGenParameterSpec.Builder(
                    MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_LENGTH)
                    .build()
                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val entry = keyStore.getEntry(MASTER_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: run {
            initMasterKey()
            (keyStore.getEntry(MASTER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
    }

    /**
     * Encrypt text using Android Keystore Master Key
     */
    fun encryptWithMasterKey(plaintext: String): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, getMasterKey())
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
            val json = JSONObject().apply {
                put("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
                put("data", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            }
            Base64.encodeToString(json.toString().toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
        } catch (e: Exception) {
            plaintext
        }
    }

    /**
     * Decrypt text using Android Keystore Master Key
     */
    fun decryptWithMasterKey(encoded: String): String {
        return try {
            val decodedJsonStr = String(Base64.decode(encoded, Base64.NO_WRAP), StandardCharsets.UTF_8)
            val json = JSONObject(decodedJsonStr)
            val iv = Base64.decode(json.getString("iv"), Base64.NO_WRAP)
            val ciphertext = Base64.decode(json.getString("data"), Base64.NO_WRAP)

            val cipher = Cipher.getInstance(AES_MODE)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), spec)
            String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            encoded
        }
    }

    /**
     * Hash PIN with salt using PBKDF2
     */
    fun hashPin(pin: String, salt: ByteArray = generateRandomBytes(16)): Pair<String, String> {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        val saltStr = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashStr = Base64.encodeToString(hash, Base64.NO_WRAP)
        return Pair(saltStr, hashStr)
    }

    fun verifyPin(pin: String, saltStr: String, storedHashStr: String): Boolean {
        return try {
            val salt = Base64.decode(saltStr, Base64.NO_WRAP)
            val (_, calculatedHash) = hashPin(pin, salt)
            calculatedHash == storedHashStr
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Encrypts payload for local backup export with a user-supplied passphrase
     */
    fun encryptBackupPayload(plainJson: String, passphrase: CharArray): String {
        val salt = generateRandomBytes(16)
        val iv = generateRandomBytes(12)

        val keySpec = PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secretKeyBytes = factory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(secretKeyBytes, "AES")

        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val cipherBytes = cipher.doFinal(plainJson.toByteArray(StandardCharsets.UTF_8))

        val exportContainer = JSONObject().apply {
            put("app", "BankCardVault")
            put("version", 1)
            put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            put("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            put("ciphertext", Base64.encodeToString(cipherBytes, Base64.NO_WRAP))
            put("timestamp", System.currentTimeMillis())
        }
        return exportContainer.toString(2)
    }

    /**
     * Decrypts local backup payload using the user-supplied passphrase
     */
    fun decryptBackupPayload(backupJsonStr: String, passphrase: CharArray): String {
        val container = JSONObject(backupJsonStr)
        if (container.optString("app") != "BankCardVault") {
            throw IllegalArgumentException("فرمت فایل پشتیبان نامعتبر است")
        }
        val salt = Base64.decode(container.getString("salt"), Base64.NO_WRAP)
        val iv = Base64.decode(container.getString("iv"), Base64.NO_WRAP)
        val cipherBytes = Base64.decode(container.getString("ciphertext"), Base64.NO_WRAP)

        val keySpec = PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secretKeyBytes = factory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(secretKeyBytes, "AES")

        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, StandardCharsets.UTF_8)
    }

    private fun generateRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }
}
