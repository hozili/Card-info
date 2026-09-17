package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BankCard
import com.example.data.CardRepository
import com.example.security.AuthMode
import com.example.security.CryptoManager
import com.example.security.SecurityPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CardRepository
    val securityPrefs: SecurityPreferences = SecurityPreferences(application)

    val isUnlocked = MutableStateFlow(securityPrefs.authMode == AuthMode.NONE)
    val twoFactorPinPassed = MutableStateFlow(false)

    val isPersonalTab = MutableStateFlow(true) // true: My cards, false: Others' cards
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("همه")

    val authMode = MutableStateFlow(securityPrefs.authMode)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CardRepository(db.bankCardDao())
        seedSampleCardsIfEmpty()
    }

    private val allCardsFlow = repository.allCards

    val displayedCards: StateFlow<List<BankCard>> = combine(
        allCardsFlow,
        isPersonalTab,
        searchQuery,
        selectedCategory
    ) { cards, isPersonal, query, category ->
        cards.filter { card ->
            val matchesType = card.isPersonal == isPersonal
            val cleanQuery = query.trim().lowercase()
            val matchesQuery = if (cleanQuery.isEmpty()) {
                true
            } else {
                card.bankName.lowercase().contains(cleanQuery) ||
                card.holderName.lowercase().contains(cleanQuery) ||
                card.cardNumber.contains(cleanQuery) ||
                card.accountNumber.contains(cleanQuery) ||
                card.iban.lowercase().contains(cleanQuery) ||
                card.notes.lowercase().contains(cleanQuery)
            }
            val matchesCategory = if (category == "همه") true else card.category == category

            matchesType && matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalPersonalCount: StateFlow<Int> = allCardsFlow.combine(MutableStateFlow(Unit)) { cards, _ ->
        cards.count { it.isPersonal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalOthersCount: StateFlow<Int> = allCardsFlow.combine(MutableStateFlow(Unit)) { cards, _ ->
        cards.count { !it.isPersonal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onTabChanged(isPersonal: Boolean) {
        isPersonalTab.value = isPersonal
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    fun addCard(card: BankCard) {
        viewModelScope.launch {
            repository.insertCard(card)
        }
    }

    fun updateCard(card: BankCard) {
        viewModelScope.launch {
            repository.updateCard(card.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteCard(card: BankCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun verifyPin(pin: String): Boolean {
        return securityPrefs.verifyPin(pin)
    }

    fun unlockWithBiometricSuccess() {
        if (securityPrefs.authMode == AuthMode.TWO_FACTOR) {
            // In 2FA, biometric is step 2 after PIN
            if (twoFactorPinPassed.value) {
                isUnlocked.value = true
            }
        } else {
            isUnlocked.value = true
        }
    }

    fun unlockWithPinSuccess() {
        if (securityPrefs.authMode == AuthMode.TWO_FACTOR) {
            twoFactorPinPassed.value = true
            // Step 1 done, now requires biometric!
        } else {
            isUnlocked.value = true
        }
    }

    fun lockApp() {
        if (securityPrefs.authMode != AuthMode.NONE) {
            isUnlocked.value = false
            twoFactorPinPassed.value = false
        }
    }

    fun updateSecuritySettings(newMode: AuthMode, newPin: String? = null) {
        if (newPin != null && newPin.length >= 4) {
            securityPrefs.savePin(newPin)
        }
        securityPrefs.authMode = newMode
        authMode.value = newMode
        if (newMode == AuthMode.NONE) {
            isUnlocked.value = true
        }
    }

    /**
     * Export all cards into an encrypted string container using user-chosen password
     */
    suspend fun exportEncryptedBackup(passphrase: String): String {
        val currentCards = repository.getCardsByType(true) // will read all
        val allCardsList = mutableListOf<BankCard>()
        val job = viewModelScope.launch {
            repository.allCards.collect {
                allCardsList.clear()
                allCardsList.addAll(it)
            }
        }
        // Yield shortly to ensure list is populated
        kotlinx.coroutines.delay(100)
        job.cancel()

        val jsonArray = JSONArray()
        for (c in allCardsList) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("bankName", c.bankName)
                put("holderName", c.holderName)
                put("cardNumber", c.cardNumber)
                put("accountNumber", c.accountNumber)
                put("iban", c.iban)
                put("cvv2", c.cvv2)
                put("expiryMonth", c.expiryMonth)
                put("expiryYear", c.expiryYear)
                put("isPersonal", c.isPersonal)
                put("category", c.category)
                put("notes", c.notes)
            }
            jsonArray.put(obj)
        }
        val plainPayload = jsonArray.toString()
        return CryptoManager.encryptBackupPayload(plainPayload, passphrase.toCharArray())
    }

    /**
     * Decrypt and restore backup file content
     */
    suspend fun importEncryptedBackup(encryptedContent: String, passphrase: String): Result<Int> {
        return try {
            val decryptedJson = CryptoManager.decryptBackupPayload(encryptedContent, passphrase.toCharArray())
            val jsonArray = JSONArray(decryptedJson)
            val restoredCards = mutableListOf<BankCard>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                restoredCards.add(
                    BankCard(
                        bankName = obj.optString("bankName", "بانک"),
                        holderName = obj.optString("holderName", ""),
                        cardNumber = obj.optString("cardNumber", ""),
                        accountNumber = obj.optString("accountNumber", ""),
                        iban = obj.optString("iban", ""),
                        cvv2 = obj.optString("cvv2", ""),
                        expiryMonth = obj.optString("expiryMonth", ""),
                        expiryYear = obj.optString("expiryYear", ""),
                        isPersonal = obj.optBoolean("isPersonal", true),
                        category = obj.optString("category", "عادی"),
                        notes = obj.optString("notes", ""),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            if (restoredCards.isNotEmpty()) {
                repository.insertAll(restoredCards)
            }
            Result.success(restoredCards.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun seedSampleCardsIfEmpty() {
        viewModelScope.launch {
            val initialCards = listOf(
                BankCard(
                    bankName = "بانک سامان",
                    holderName = "علی رضایی",
                    cardNumber = "6219861045239871",
                    accountNumber = "842-152-498210-1",
                    iban = "IR820560084215249821000001",
                    cvv2 = "734",
                    expiryMonth = "08",
                    expiryYear = "06",
                    isPersonal = true,
                    category = "اصلی",
                    notes = "کارت حقوق و واریزی‌های ماهانه"
                ),
                BankCard(
                    bankName = "بانک ملی ایران",
                    holderName = "علی رضایی",
                    cardNumber = "6037997541238965",
                    accountNumber = "0105432981002",
                    iban = "IR170170000000105432981002",
                    cvv2 = "921",
                    expiryMonth = "11",
                    expiryYear = "07",
                    isPersonal = true,
                    category = "پس‌انداز",
                    notes = "حساب سپرده و قرض‌الحسنه"
                ),
                BankCard(
                    bankName = "بانک ملت",
                    holderName = "سارا محمدی",
                    cardNumber = "6104337890123456",
                    accountNumber = "4820938174",
                    iban = "IR540120000000004820938174",
                    cvv2 = "482",
                    expiryMonth = "04",
                    expiryYear = "05",
                    isPersonal = false,
                    category = "همکار",
                    notes = "پروژه طراحی و مشاوره"
                )
            )

            // Seed only once if empty
            // Check if any card exists
            val existing = repository.getCardById(1L)
            if (existing == null) {
                repository.insertAll(initialCards)
            }
        }
    }
}
