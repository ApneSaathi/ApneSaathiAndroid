package com.nitiaayog.apnesaathi.database

import androidx.room.RoomMasterTable.TABLE_NAME
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class ApneSathiMigration(startVersion: Int, endVersion: Int) : Migration(startVersion, endVersion) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val TABLE_NAME_TEMP = "apnesathiNew"

        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `SeniorCitizen` " +
                "(`user_id` INTEGER NOT NULL, `flag_` INTEGER NOT NULL, `flag_comment` TEXT NOT NULL," +
                " `status_` TEXT NOT NULL, `whom_did_talk_with` TEXT NOT NULL, `medical_history` TEXT NOT NULL," +
                " `related_info_talked_about` TEXT NOT NULL, `behaviour_practices` TEXT NOT NULL," +
                " `other_medical_problems` TEXT NOT NULL, `covid_symptoms` TEXT NOT NULL," +
                " `non_covid_symptoms` TEXT NOT NULL, `quarantine_hospitalization_status` TEXT NOT NULL, " +
                "`is_there_lack_of_essential_service` INTEGER NOT NULL, `lack_of_essential_services` TEXT NOT NULL," +
                " `lack_of_essential_services_desc` TEXT NOT NULL, `is_there_need_of_emergency_escalation` INTEGER NOT NULL," +
                " `other_imp_info_desc` TEXT NOT NULL, PRIMARY KEY(`user_id`))")

        // 2. Copy the data
        database.execSQL("INSERT INTO $TABLE_NAME_TEMP"
                + "FROM $TABLE_NAME")

        // 3. Remove the old table
        database.execSQL("DROP TABLE $TABLE_NAME")

        // 4. Change the table name to the correct one
        database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO $TABLE_NAME")
    }
}