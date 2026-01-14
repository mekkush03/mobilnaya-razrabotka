package com.example.dailynotifications.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE ownerId = :ownerId ORDER BY dateTimeMillis DESC")
    fun observeRemindersByOwner(ownerId: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(entity: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: String)
}
