package com.smartvoice.recorder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val audioFilePath: String,
    val duration: Long = 0L, // in seconds
    val fileSize: Long = 0L, // in bytes
    val createdAt: Long = System.currentTimeMillis(),
    val tags: String = "", // comma-separated
    val transcript: String = "",
    val summary: String = "",
    val keyPoints: String = "", // comma-separated
    val actionItems: String = "", // JSON array
    val waveformData: String = "", // JSON array
    val isTranscribing: Boolean = false,
    val isSummarizing: Boolean = false
)

data class ActionItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val assignee: String = "",
    val deadline: Long = 0L,
    val completed: Boolean = false
)
