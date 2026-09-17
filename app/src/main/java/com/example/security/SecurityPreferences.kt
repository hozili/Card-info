package com.example.security

import android.content.Context
import android.content.SharedPreferences

enum class AuthMode {
    NONE,
    PIN,
    BIOMETRIC,
    TWO_FACTOR // PIN + Biometric
}

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vault_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_MODE = "auth_mode"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_AUTO_LOCK = "auto_lock"
    }

    var authMode: AuthMode
        get() {
            val modeName = prefs.getString(KEY_AUTH_MODE, AuthMode.NONE.name) ?: AuthMode.NONE.name
            return try {
                AuthMode.valueOf(modeName)
            } catch (e: Exception) {
                AuthMode.NONE
            }
        }
        set(value) {
            prefs.edit().putString(KEY_AUTH_MODE, value.name).apply()
        }

    var autoLockOnBackground: Boolean
        get() = prefs.getBoolean(KEY_AUTO_LOCK, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_LOCK, value).apply()

    fun isPinSet(): Boolean {
        val hash = prefs.getString(KEY_PIN_HASH, null)
        val salt = prefs.getString(KEY_PIN_SALT, null)
        return !hash.isNullOrEmpty() && !salt.isNullOrEmpty()
    }

    fun savePin(pin: String) {
        val (salt, hash) = CryptoManager.hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_SALT, salt)
            .putString(KEY_PIN_HASH, hash)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val salt = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return CryptoManager.verifyPin(pin, salt, storedHash)
    }

    fun clearPin() {
        prefs.edit()
            .remove(KEY_PIN_SALT)
            .remove(KEY_PIN_HASH)
            .apply()
    }
}
