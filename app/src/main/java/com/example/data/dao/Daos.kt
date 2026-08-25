package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(vararg users: UserEntity)
}

@Dao
interface InstrumentDao {
    @Query("SELECT * FROM instruments ORDER BY id DESC")
    fun getAllInstruments(): Flow<List<InstrumentEntity>>

    @Query("SELECT * FROM instruments WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getInstrumentsByOwner(ownerId: String): Flow<List<InstrumentEntity>>

    @Query("SELECT * FROM instruments WHERE instrumentId = :instrumentId LIMIT 1")
    suspend fun getInstrumentByCode(instrumentId: String): InstrumentEntity?

    @Query("SELECT * FROM instruments WHERE id = :id LIMIT 1")
    suspend fun getInstrumentById(id: String): InstrumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrument(instrument: InstrumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstruments(instruments: List<InstrumentEntity>)

    @Update
    suspend fun updateInstrument(instrument: InstrumentEntity)

    @Query("UPDATE instruments SET status = :status WHERE id = :id")
    suspend fun updateInstrumentStatus(id: String, status: InstrumentStatus)

    @Query("DELETE FROM instruments WHERE id = :id")
    suspend fun deleteInstrumentById(id: String)
}

@Dao
interface VerificationRequestDao {
    @Query("SELECT * FROM verification_requests ORDER BY submittedAt DESC")
    fun getAllRequests(): Flow<List<VerificationRequestEntity>>

    @Query("SELECT * FROM verification_requests WHERE businessId = :businessId ORDER BY submittedAt DESC")
    fun getRequestsByBusiness(businessId: String): Flow<List<VerificationRequestEntity>>

    @Query("SELECT * FROM verification_requests WHERE assignedInspectorId = :inspectorId ORDER BY submittedAt DESC")
    fun getRequestsByInspector(inspectorId: String): Flow<List<VerificationRequestEntity>>

    @Query("SELECT * FROM verification_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: String): VerificationRequestEntity?

    @Query("SELECT * FROM verification_requests WHERE instrumentId = :instrumentId LIMIT 1")
    suspend fun getRequestByInstrumentId(instrumentId: String): VerificationRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: VerificationRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<VerificationRequestEntity>)

    @Update
    suspend fun updateRequest(request: VerificationRequestEntity)

    @Query("UPDATE verification_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: InstrumentStatus)

    @Query("UPDATE verification_requests SET assignedInspectorId = :inspectorId, assignedInspectorName = :inspectorName, status = :status WHERE id = :id")
    suspend fun assignInspector(id: String, inspectorId: String, inspectorName: String, status: InstrumentStatus = InstrumentStatus.ASSIGNED)

    @Query("UPDATE verification_requests SET scheduledDate = :scheduledDate, status = :status WHERE id = :id")
    suspend fun scheduleInspection(id: String, scheduledDate: String, status: InstrumentStatus = InstrumentStatus.INSPECTION_SCHEDULED)
}

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspections WHERE instrumentId = :instrumentId ORDER BY timestamp DESC")
    fun getInspectionsForInstrument(instrumentId: String): Flow<List<InspectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspections(inspections: List<InspectionEntity>)
}

@Dao
interface InspectionReadingDao {
    @Query("SELECT * FROM inspection_readings WHERE inspectionId = :inspectionId ORDER BY testIndex ASC")
    fun getReadingsForInspection(inspectionId: String): Flow<List<InspectionReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<InspectionReadingEntity>)
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates ORDER BY verificationDate DESC")
    fun getAllCertificates(): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates WHERE certificateNumber = :certNumber LIMIT 1")
    suspend fun getCertificateByNumber(certNumber: String): CertificateEntity?

    @Query("SELECT * FROM certificates WHERE instrumentId = :instrumentId ORDER BY verificationDate DESC LIMIT 1")
    suspend fun getLatestCertificateForInstrument(instrumentId: String): CertificateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: CertificateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificates(certificates: List<CertificateEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AuditLogEntity>)
}
