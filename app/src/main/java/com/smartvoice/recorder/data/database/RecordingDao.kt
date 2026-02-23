package com.smartvoice.recorder.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.smartvoice.recorder.data.model.Recording
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Insert
    suspend fun insertRecording(recording: Recording): Long

    @Update
    suspend fun updateRecording(recording: Recording)

    @Delete
    suspend fun deleteRecording(recording: Recording)

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: String): Recording?

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<Recording>>

    @Query("SELECT * FROM recordings WHERE title LIKE '%' || :keyword || '%' OR transcript LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    fun searchRecordings(keyword: String): Flow<List<Recording>>

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentRecordings(limit: Int): Flow<List<Recording>>

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteRecordingById(id: String)
}
