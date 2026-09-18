package com.example.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val bankCardDao: BankCardDao) {

    val allCards: Flow<List<BankCard>> = bankCardDao.getAllCards()

    fun getCardsByType(isPersonal: Boolean): Flow<List<BankCard>> =
        bankCardDao.getCardsByType(isPersonal)

    suspend fun getCardById(id: Long): BankCard? = bankCardDao.getCardById(id)

    suspend fun getCardCount(): Int = bankCardDao.getCardCount()

    suspend fun insertCard(card: BankCard): Long = bankCardDao.insertCard(card)

    suspend fun updateCard(card: BankCard) = bankCardDao.updateCard(card)

    suspend fun deleteCard(card: BankCard) = bankCardDao.deleteCard(card)

    suspend fun deleteCardById(id: Long) = bankCardDao.deleteCardById(id)

    suspend fun insertAll(cards: List<BankCard>) = bankCardDao.insertAll(cards)

    suspend fun deleteAll() = bankCardDao.deleteAll()
}
