package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_cards")
data class BankCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val bankName: String,
    val holderName: String,
    val cardNumber: String,
    val accountNumber: String = "",
    val iban: String = "",
    val cvv2: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val isPersonal: Boolean = true, // true = My Cards, false = Others' Cards
    val category: String = "عادی",
    val colorIndex: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
