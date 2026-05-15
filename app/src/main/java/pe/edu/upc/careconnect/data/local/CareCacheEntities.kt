package pe.edu.upc.careconnect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_documents")
data class CachedDocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val date: String,
    val section: String,
    val tone: String,
    val description: String,
    val sortOrder: Long
)

@Entity(tableName = "cached_diary_notes")
data class CachedDiaryNoteEntity(
    @PrimaryKey val id: String,
    val author: String,
    val timestamp: String,
    val body: String,
    val tags: String,
    val tone: String,
    val isHighlighted: Boolean,
    val sortOrder: Long
)

@Entity(tableName = "cached_notifications")
data class CachedNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val status: String,
    val statusTone: String,
    val actionLabel: String,
    val timestamp: String,
    val iconTone: String,
    val sortOrder: Long
)
