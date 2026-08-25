package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    BUSINESS_OWNER,
    INSPECTOR,
    ADMIN,
    PUBLIC
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val organizationOrBusiness: String = "",
    val phone: String = ""
)

enum class InstrumentStatus {
    DRAFT,
    SUBMITTED,
    ASSIGNED,
    INSPECTION_SCHEDULED,
    UNDER_INSPECTION,
    PASSED,
    FAILED,
    CERTIFICATE_GENERATED
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "instruments")
data class InstrumentEntity(
    @PrimaryKey val id: String,
    val instrumentId: String,
    val name: String,
    val type: String, // Digital weighing scale, Platform scale, Weighbridge, Petrol pump, etc.
    val category: String, // Commercial, Industrial, Retail, Laboratory, Petroleum
    val manufacturer: String,
    val modelNumber: String,
    val serialNumber: String,
    val capacity: String,
    val unitOfMeasurement: String,
    val location: String,
    val ownerBusiness: String,
    val ownerId: String,
    val purchaseDate: String,
    val lastVerificationDate: String,
    val nextVerificationDate: String,
    val status: InstrumentStatus,
    val photoUri: String = "",
    val previousCertificateNumber: String = "",
    val permissibleTolerancePercentage: Double = 0.5, // e.g. ±0.5%
    val riskScore: RiskLevel = RiskLevel.LOW,
    val riskReason: String = "Normal compliance history"
)

@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val instrumentId: String,
    val instrumentName: String,
    val instrumentType: String,
    val businessId: String,
    val businessName: String,
    val location: String,
    val submittedAt: Long,
    val scheduledDate: String = "",
    val assignedInspectorId: String = "",
    val assignedInspectorName: String = "",
    val status: InstrumentStatus,
    val notes: String = ""
)

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val instrumentId: String,
    val inspectorId: String,
    val inspectorName: String,
    val inspectionDate: String,
    val standardMassUsed: String,
    val averageError: Double,
    val averagePercentageError: Double,
    val result: String, // PASS or FAIL
    val remarks: String,
    val observations: String,
    val photoUri: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "inspection_readings")
data class InspectionReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inspectionId: String,
    val testIndex: Int,
    val referenceValue: Double,
    val actualReading: Double,
    val unit: String,
    val calculatedError: Double,
    val percentageError: Double,
    val permissibleError: Double,
    val isPass: Boolean
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val certificateNumber: String,
    val instrumentId: String,
    val instrumentName: String,
    val instrumentType: String,
    val manufacturer: String,
    val modelNumber: String,
    val serialNumber: String,
    val capacity: String,
    val unit: String,
    val ownerBusiness: String,
    val location: String,
    val verificationDate: String,
    val validUntil: String,
    val inspectorName: String,
    val inspectorId: String,
    val status: String, // VERIFIED, EXPIRED, REVOKED
    val standardCode: String = "OIML R 76-1 / ISO 17025 Compliant",
    val qrPayload: String = ""
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String, // or "ALL" / "ROLE_ADMIN" etc.
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "INFO" // SUCCESS, WARNING, ALERT, INFO
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val performedBy: String,
    val role: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String
)
