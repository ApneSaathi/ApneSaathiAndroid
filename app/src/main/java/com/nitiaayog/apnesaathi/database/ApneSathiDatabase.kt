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
    entities = [CallData::class, SrCitizenGrievance::class, SyncSrCitizenGrievance::class, DistrictDetails::class,
        GrievanceData::class, Volunteer::class],
    version = BuildConfig.DatabaseVersion
)
@TypeConverters(Converter::class)
abstract class ApneSathiDatabase : RoomDatabase() {

    abstract fun provideCallDataDao(): CallDataDao
    abstract fun provideGrievancesDao(): GrievancesDao
    abstract fun provideGrievancesTrackingDao(): GrievanceTrackingDao
    abstract fun provideSrCitizenGrievancesDao(): SyncSrCitizenGrievancesDao
    abstract fun provideVolunteerDao(): VolunteerDao
    abstract fun provideDistrictDao(): DistrictDataDao

    /**
     * This method will only be called when user will log out and we need to remove all the
     * data from the database
     * */
    @Transaction
    fun clearDatabase() = clearAllTables()

    companion object {
        @Volatile
        private var INSTANCE: ApneSathiDatabase? = null

        private val migration = ApneSathiMigration(1, 2)

        fun getDatabase(context: Context): ApneSathiDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApneSathiDatabase::class.java,
                    BuildConfig.DatabaseName
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    //.addCallback(ApneSathiDatabaseCallback(scope))
                    //.setQueryExecutor(Executors.newScheduledThreadPool(3))
                    .fallbackToDestructiveMigration()
                    .addMigrations(migration)
                    .fallbackToDestructiveMigration()
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
}