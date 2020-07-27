package com.nitiaayog.apnesaathi.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nitiaayog.apnesaathi.BuildConfig
import com.nitiaayog.apnesaathi.database.dao.*
import com.nitiaayog.apnesaathi.model.*
import com.nitiaayog.apnesaathi.utility.Converter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SeniorCitizen::class, CallData::class, SrCitizenGrievance::class, SyncSrCitizenGrievance::class, GrievanceData::class],
    version = BuildConfig.DB_VERSION
)
@TypeConverters(Converter::class)
abstract class ApneSathiDatabase : RoomDatabase() {

    abstract fun apneSathiDao(): ApneSathiDao
    abstract fun provideCallDataDao(): CallDataDao
    abstract fun provideGrievancesDao(): GrievancesDao
    abstract fun provideGrievancesTrackingDao(): GrievanceTrackingDao
    abstract fun provideSrCitizenGrievancesDao(): SyncSrCitizenGrievancesDao

    companion object {
        @Volatile
        private var INSTANCE: ApneSathiDatabase? = null

        @JvmField
        val MIGRATION = ApneSathiMigration(1, 2)

        fun getDatabase(context: Context): ApneSathiDatabase {
            //, scope: CoroutineScope
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, ApneSathiDatabase::class.java, BuildConfig.DB_NAME
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
//                    .fallbackToDestructiveMigration()
                    //.addCallback(ApneSathiDatabaseCallback(scope))
                    .addMigrations(MIGRATION)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ApneSathiDatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {
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

    @Transaction
    fun clearDatabase() = clearAllTables()
}