package com.nitiaayog.apnesaathi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nitiaayog.apnesaathi.model.SeniorCitizen
import kotlinx.coroutines.CoroutineScope

@Database(entities = [SeniorCitizen::class], version = 1)
abstract class ApneSathiDatabase : RoomDatabase() {
    abstract fun ApneSathiDao(): ApneSathiDao

    companion object {
        @Volatile
        private var INSTANCE: ApneSathiDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ApneSathiDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApneSathiDatabase::class.java,
                    "apne_sathi.db"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(ApneSathiDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ApneSathiDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
//            override fun onOpen(db: SupportSQLiteDatabase) {
//                super.onOpen(db)
//                // If you want to keep the data through app restarts,
//                // comment out the following line.
//                INSTANCE?.let { database ->
//                    scope.launch(Dispatchers.IO) {
//                        populateDatabase(database.apnethiDao())
//                    }
//                }
//            }
        }
    }
}