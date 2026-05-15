package pe.edu.upc.careconnect.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedDocumentEntity::class,
        CachedDiaryNoteEntity::class,
        CachedNotificationEntity::class
    ],
    version = 1,
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
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}
