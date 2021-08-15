package edu.uw.fallalarm.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [ContactEntity::class, HistoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val contactDao: ContactDao?
    abstract  val historyDao: HistoryDao?


    companion object {
        private val LOG_TAG = AppDatabase::class.java
            .simpleName
        private const val DATABASE_NAME = "fallalarmdb"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {

            return synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(LOG_TAG, "Creating new database instance")
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    ).allowMainThreadQueries().build()
                    INSTANCE = instance
                }
                instance
            }
        }
    }
}