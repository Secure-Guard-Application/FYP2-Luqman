package com.example.secureguardapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scanHistory: ScanHistory)

    @Query("SELECT * FROM scan_history ORDER BY scanDate DESC")
    suspend fun getAllScans(): List<ScanHistory>

    @Delete
    suspend fun deleteScan(scanHistory: ScanHistory)


}