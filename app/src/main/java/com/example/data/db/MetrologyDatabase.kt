package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        InstrumentEntity::class,
        VerificationRequestEntity::class,
        InspectionEntity::class,
        InspectionReadingEntity::class,
        CertificateEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MetrologyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun instrumentDao(): InstrumentDao
    abstract fun verificationRequestDao(): VerificationRequestDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun inspectionReadingDao(): InspectionReadingDao
    abstract fun certificateDao(): CertificateDao
    abstract fun notificationDao(): NotificationDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: MetrologyDatabase? = null

        fun getDatabase(context: Context): MetrologyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetrologyDatabase::class.java,
                    "metrology_verification_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
