package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BankCardDao {
    @Query("SELECT * FROM bank_cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<BankCard>>

    @Query("SELECT * FROM bank_cards WHERE isPersonal = :isPersonal ORDER BY updatedAt DESC")
    fun getCardsByType(isPersonal: Boolean): Flow<List<BankCard>>

    @Query("SELECT * FROM bank_cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Long): BankCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BankCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<BankCard>)

    @Update
    suspend fun updateCard(card: BankCard)

    @Delete
    suspend fun deleteCard(card: BankCard)

    @Query("DELETE FROM bank_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("DELETE FROM bank_cards")
    suspend fun deleteAll()
}
