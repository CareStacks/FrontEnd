package pe.edu.upc.careconnect.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CachedDocumentEntity::class,
        CachedDiaryNoteEntity::class,
        CachedNotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CareCacheDatabase : RoomDatabase() {
    abstract fun careCacheDao(): CareCacheDao

    companion object {
        @Volatile
        private var instance: CareCacheDatabase? = null

        fun getInstance(context: Context): CareCacheDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CareCacheDatabase::class.java,
                    "care_connect_cache.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { database ->
                    instance = database
                }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN backendDocumentType TEXT NOT NULL DEFAULT 'OTHER'")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN localUri TEXT")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN storageBucket TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN storagePath TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN mimeType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN fileSizeBytes INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN uploadedAt TEXT")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
                db.execSQL("ALTER TABLE cached_documents ADD COLUMN errorMessage TEXT")
            }
        }
    }
}
