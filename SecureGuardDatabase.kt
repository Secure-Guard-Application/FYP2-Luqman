package com.example.secureguardapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ScanHistory::class], version = 2)
abstract class SecureGuardDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: SecureGuardDatabase? = null

        // Migration from version 1 to version 2 with a check to avoid duplicate column errors
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Log the start of migration for debugging
                android.util.Log.d("DatabaseMigration", "Running migration from version 1 to 2")

                // Check and add columns only if they do not exist
                val cursor = database.query("PRAGMA table_info(scan_history)")
                val existingColumns = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    existingColumns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                }
                cursor.close()

                if (!existingColumns.contains("category")) {
                    database.execSQL("ALTER TABLE scan_history ADD COLUMN category TEXT NOT NULL DEFAULT ''")
                }

                if (!existingColumns.contains("message")) {
                    database.execSQL("ALTER TABLE scan_history ADD COLUMN message TEXT NOT NULL DEFAULT ''")
                }

                // Log the completion of migration for debugging
                android.util.Log.d("DatabaseMigration", "Migration from version 1 to 2 completed")
            }
        }

        fun getDatabase(context: Context): SecureGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecureGuardDatabase::class.java,
                    "secure_guard_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
