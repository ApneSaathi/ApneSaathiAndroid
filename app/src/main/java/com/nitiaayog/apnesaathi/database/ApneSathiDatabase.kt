package com.nitiaayog.apnesaathi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nitiaayog.apnesaathi.model.SeniorCitizen
import com.nitiaayog.apnesaathi.utility.Converter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SeniorCitizen::class], version = 1)
@TypeConverters(Converter::class)
abstract class ApneSathiDatabase : RoomDatabase() {
    abstract fun ApneSathiDao(): ApneSathiDao


    companion object {
        @Volatile
        private var INSTANCE: ApneSathiDatabase? = null
        private val DATABASE_NAME: String = "apne_sathi.db"
        @JvmField
        val MIGRATION = ApneSathiMigration(1, 2)

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
                    DATABASE_NAME
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
//                    .fallbackToDestructiveMigration()
                    .addCallback(ApneSathiDatabaseCallback(scope))
                    .addMigrations(MIGRATION)
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
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {

                    }
                }
            }
        }
    }
}