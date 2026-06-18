package pe.edu.upc.careconnect.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CareCacheDao {
    @Query("SELECT * FROM cached_documents ORDER BY sortOrder ASC")
    fun observeDocuments(): Flow<List<CachedDocumentEntity>>

    @Query("SELECT * FROM cached_diary_notes ORDER BY sortOrder ASC")
    fun observeDiaryNotes(): Flow<List<CachedDiaryNoteEntity>>

    @Query("SELECT * FROM cached_notifications ORDER BY sortOrder ASC")
    fun observeNotifications(): Flow<List<CachedNotificationEntity>>

    @Query("SELECT COUNT(*) FROM cached_documents")
    suspend fun countDocuments(): Int

    @Query("SELECT COUNT(*) FROM cached_diary_notes")
    suspend fun countDiaryNotes(): Int

    @Query("SELECT COUNT(*) FROM cached_notifications")
    suspend fun countNotifications(): Int

    @Query("DELETE FROM cached_documents")
    suspend fun clearDocuments()

    @Query("DELETE FROM cached_documents WHERE syncStatus = 'SYNCED'")
    suspend fun clearSyncedDocuments()

    @Query("DELETE FROM cached_documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)

    @Query("SELECT * FROM cached_documents WHERE id = :documentId LIMIT 1")
    suspend fun getDocumentById(documentId: String): CachedDocumentEntity?

    @Query("SELECT * FROM cached_documents WHERE syncStatus IN ('PENDING', 'UPLOADING', 'ERROR') ORDER BY sortOrder ASC")
    suspend fun getPendingDocuments(): List<CachedDocumentEntity>

    @Query("DELETE FROM cached_diary_notes")
    suspend fun clearDiaryNotes()

    @Query("DELETE FROM cached_notifications")
    suspend fun clearNotifications()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDocuments(documents: List<CachedDocumentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDocument(document: CachedDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDiaryNotes(notes: List<CachedDiaryNoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDiaryNote(note: CachedDiaryNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotifications(notifications: List<CachedNotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotification(notification: CachedNotificationEntity)
}
