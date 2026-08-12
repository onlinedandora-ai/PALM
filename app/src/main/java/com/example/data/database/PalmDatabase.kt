package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ModuleEntity::class,
        EventEntity::class,
        ExpenseEntity::class,
        BudgetEntity::class,
        VehicleEntity::class,
        SubscriptionEntity::class,
        VaultDocumentEntity::class,
        PasswordEntity::class,
        SmsLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PalmDatabase : RoomDatabase() {

    abstract fun palmDao(): PalmDao

    companion object {
        @Volatile
        private var INSTANCE: PalmDatabase? = null

        fun getDatabase(context: Context): PalmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PalmDatabase::class.java,
                    "palm_local_encrypted.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
