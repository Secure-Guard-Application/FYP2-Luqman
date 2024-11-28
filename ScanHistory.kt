package com.example.secureguardapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "scan_history")
data class ScanHistory(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val isMalicious: Boolean,
    val scanDate: Long,
    val category: String,
    val message: String


)
