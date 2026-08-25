package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MetrologyRepository(
    private val userDao: UserDao,
    private val instrumentDao: InstrumentDao,
    private val verificationRequestDao: VerificationRequestDao,
    private val inspectionDao: InspectionDao,
    private val inspectionReadingDao: InspectionReadingDao,
    private val certificateDao: CertificateDao,
    private val notificationDao: NotificationDao,
    private val auditLogDao: AuditLogDao
) {
    // Flow observables
    val allInstruments: Flow<List<InstrumentEntity>> = instrumentDao.getAllInstruments()
    val allRequests: Flow<List<VerificationRequestEntity>> = verificationRequestDao.getAllRequests()
    val allInspections: Flow<List<InspectionEntity>> = inspectionDao.getAllInspections()
    val allCertificates: Flow<List<CertificateEntity>> = certificateDao.getAllCertificates()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getAllLogs()

    fun getInstrumentsForOwner(ownerId: String): Flow<List<InstrumentEntity>> =
        instrumentDao.getInstrumentsByOwner(ownerId)

    fun getRequestsForInspector(inspectorId: String): Flow<List<VerificationRequestEntity>> =
        verificationRequestDao.getRequestsByInspector(inspectorId)

    fun getReadingsForInspection(inspectionId: String): Flow<List<InspectionReadingEntity>> =
        inspectionReadingDao.getReadingsForInspection(inspectionId)

    suspend fun getCertificateByNumber(certNumber: String): CertificateEntity? = withContext(Dispatchers.IO) {
        certificateDao.getCertificateByNumber(certNumber.trim())
    }

    suspend fun getInstrumentByCode(code: String): InstrumentEntity? = withContext(Dispatchers.IO) {
        instrumentDao.getInstrumentByCode(code.trim())
    }

    suspend fun getInstrumentById(id: String): InstrumentEntity? = withContext(Dispatchers.IO) {
        instrumentDao.getInstrumentById(id)
    }

    suspend fun getRequestById(id: String): VerificationRequestEntity? = withContext(Dispatchers.IO) {
        verificationRequestDao.getRequestById(id)
    }

    // Business Action: Register New Instrument
    suspend fun registerInstrument(
        name: String,
        type: String,
        category: String,
        manufacturer: String,
        modelNumber: String,
        serialNumber: String,
        capacity: String,
        unit: String,
        location: String,
        ownerBusiness: String,
        ownerId: String,
        tolerancePct: Double = 0.05
    ): InstrumentEntity = withContext(Dispatchers.IO) {
        val uniqueId = "INST-" + SimpleDateFormat("yyyy", Locale.US).format(Date()) + "-" + (1000..9999).random()
        val instrument = InstrumentEntity(
            id = UUID.randomUUID().toString(),
            instrumentId = uniqueId,
            name = name.ifBlank { "$manufacturer $type" },
            type = type,
            category = category,
            manufacturer = manufacturer,
            modelNumber = modelNumber,
            serialNumber = serialNumber,
            capacity = capacity,
            unitOfMeasurement = unit,
            location = location,
            ownerBusiness = ownerBusiness,
            ownerId = ownerId,
            purchaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            lastVerificationDate = "None",
            nextVerificationDate = "Pending Initial Verification",
            status = InstrumentStatus.DRAFT,
            permissibleTolerancePercentage = tolerancePct,
            riskScore = RiskLevel.LOW,
            riskReason = "New instrument registration awaiting initial baseline verification."
        )
        instrumentDao.insertInstrument(instrument)

        auditLogDao.insertLog(
            AuditLogEntity(
                action = "INSTRUMENT_REGISTERED",
                performedBy = ownerBusiness,
                role = "BUSINESS_OWNER",
                details = "Registered $type (ID: $uniqueId, S/N: $serialNumber)"
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = ownerId,
                title = "Instrument Registered",
                message = "Instrument $uniqueId ($name) registered successfully. You can now request verification.",
                type = "SUCCESS"
            )
        )

        instrument
    }

    // Business Action: Request Verification
    suspend fun submitVerificationRequest(instrument: InstrumentEntity, notes: String = ""): VerificationRequestEntity = withContext(Dispatchers.IO) {
        val reqId = "REQ-" + (100000..999999).random()
        val request = VerificationRequestEntity(
            id = UUID.randomUUID().toString(),
            requestId = reqId,
            instrumentId = instrument.instrumentId,
            instrumentName = instrument.name,
            instrumentType = instrument.type,
            businessId = instrument.ownerId,
            businessName = instrument.ownerBusiness,
            location = instrument.location,
            submittedAt = System.currentTimeMillis(),
            scheduledDate = "Pending Inspector Schedule",
            assignedInspectorId = "user_insp1", // Auto-route to duty inspector
            assignedInspectorName = "Officer Sarah Jenkins",
            status = InstrumentStatus.SUBMITTED,
            notes = notes.ifBlank { "Standard annual metrological verification request." }
        )

        verificationRequestDao.insertRequest(request)
        instrumentDao.updateInstrumentStatus(instrument.id, InstrumentStatus.SUBMITTED)

        auditLogDao.insertLog(
            AuditLogEntity(
                action = "VERIFICATION_REQUESTED",
                performedBy = instrument.ownerBusiness,
                role = "BUSINESS_OWNER",
                details = "Submitted verification request $reqId for instrument ${instrument.instrumentId}"
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = instrument.ownerId,
                title = "Verification Request Submitted",
                message = "Request $reqId submitted. A certified metrology inspector will be assigned shortly.",
                type = "INFO"
            )
        )

        request
    }

    // Inspector Action: Schedule inspection
    suspend fun scheduleInspection(requestId: String, instrumentId: String, date: String) = withContext(Dispatchers.IO) {
        verificationRequestDao.scheduleInspection(requestId, date, InstrumentStatus.INSPECTION_SCHEDULED)
        val inst = instrumentDao.getInstrumentByCode(instrumentId)
        if (inst != null) {
            instrumentDao.updateInstrumentStatus(inst.id, InstrumentStatus.INSPECTION_SCHEDULED)
        }
        auditLogDao.insertLog(
            AuditLogEntity(
                action = "INSPECTION_SCHEDULED",
                performedBy = "Officer Sarah Jenkins",
                role = "INSPECTOR",
                details = "Inspection for $instrumentId scheduled on $date"
            )
        )
    }

    // Inspector Action: Conduct & Submit Complete Inspection
    suspend fun submitInspection(
        requestId: String,
        instrumentId: String,
        inspectorId: String,
        inspectorName: String,
        standardMassUsed: String,
        readings: List<InspectionReadingEntity>,
        remarks: String,
        observations: String,
        isPass: Boolean
    ): CertificateEntity? = withContext(Dispatchers.IO) {
        val inspectionId = UUID.randomUUID().toString()
        val avgErr = readings.map { it.calculatedError }.average()
        val avgPctErr = readings.map { it.percentageError }.average()
        val resultStr = if (isPass) "PASS" else "FAIL"

        val inspection = InspectionEntity(
            id = inspectionId,
            requestId = requestId,
            instrumentId = instrumentId,
            inspectorId = inspectorId,
            inspectorName = inspectorName,
            inspectionDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            standardMassUsed = standardMassUsed,
            averageError = if (avgErr.isNaN()) 0.0 else avgErr,
            averagePercentageError = if (avgPctErr.isNaN()) 0.0 else avgPctErr,
            result = resultStr,
            remarks = remarks,
            observations = observations,
            photoUri = "",
            timestamp = System.currentTimeMillis()
        )

        inspectionDao.insertInspection(inspection)

        // Save readings with inspectionId
        val readingsWithId = readings.map { it.copy(inspectionId = inspectionId) }
        inspectionReadingDao.insertReadings(readingsWithId)

        val targetStatus = if (isPass) InstrumentStatus.CERTIFICATE_GENERATED else InstrumentStatus.FAILED
        verificationRequestDao.updateRequestStatus(requestId, targetStatus)

        val inst = instrumentDao.getInstrumentByCode(instrumentId)
        var createdCert: CertificateEntity? = null

        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        cal.add(Calendar.YEAR, 1)
        val validUntilStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

        if (inst != null) {
            instrumentDao.updateInstrument(
                inst.copy(
                    status = targetStatus,
                    lastVerificationDate = todayStr,
                    nextVerificationDate = if (isPass) validUntilStr else "Verification Failed - Re-test Required",
                    riskScore = if (isPass) RiskLevel.LOW else RiskLevel.HIGH,
                    riskReason = if (isPass) "Passed all ISO/OIML calibration tolerance checks" else "Failed calibration tolerances. Error exceeds permissible limits."
                )
            )

            if (isPass) {
                val certNum = "CERT-" + SimpleDateFormat("yyyy", Locale.US).format(Date()) + "-NLM-" + (1000..9999).random()
                val cert = CertificateEntity(
                    certificateNumber = certNum,
                    instrumentId = inst.instrumentId,
                    instrumentName = inst.name,
                    instrumentType = inst.type,
                    manufacturer = inst.manufacturer,
                    modelNumber = inst.modelNumber,
                    serialNumber = inst.serialNumber,
                    capacity = inst.capacity,
                    unit = inst.unitOfMeasurement,
                    ownerBusiness = inst.ownerBusiness,
                    location = inst.location,
                    verificationDate = todayStr,
                    validUntil = validUntilStr,
                    inspectorName = inspectorName,
                    inspectorId = inspectorId,
                    status = "VERIFIED",
                    standardCode = "OIML R 76-1 / ISO 17025 Compliant",
                    qrPayload = "https://metrology.gov.verify/cert/$certNum"
                )
                certificateDao.insertCertificate(cert)
                createdCert = cert

                notificationDao.insertNotification(
                    NotificationEntity(
                        id = UUID.randomUUID().toString(),
                        userId = inst.ownerId,
                        title = "Certificate Generated: $certNum",
                        message = "Your instrument ${inst.instrumentId} has PASSED verification. Digital certificate is now active.",
                        type = "SUCCESS"
                    )
                )
            } else {
                notificationDao.insertNotification(
                    NotificationEntity(
                        id = UUID.randomUUID().toString(),
                        userId = inst.ownerId,
                        title = "Verification Failed",
                        message = "Instrument ${inst.instrumentId} did not meet permissible error limits. Please service instrument and request re-verification.",
                        type = "ALERT"
                    )
                )
            }
        }

        auditLogDao.insertLog(
            AuditLogEntity(
                action = if (isPass) "CERTIFICATE_ISSUED" else "INSPECTION_FAILED",
                performedBy = inspectorName,
                role = "INSPECTOR",
                details = "Completed inspection for $instrumentId. Result: $resultStr (Avg Error: ${String.format("%.4f", avgErr)})"
            )
        )

        createdCert
    }

    // Admin Action: Assign inspector
    suspend fun assignInspector(requestId: String, inspectorId: String, inspectorName: String) = withContext(Dispatchers.IO) {
        verificationRequestDao.assignInspector(requestId, inspectorId, inspectorName)
        auditLogDao.insertLog(
            AuditLogEntity(
                action = "INSPECTOR_ASSIGNED",
                performedBy = "Admin Directorate",
                role = "ADMIN",
                details = "Assigned $inspectorName to request $requestId"
            )
        )
    }

    // Initialize Rich Demo Seed Data for instant judge testing
    suspend fun seedDemoDataIfEmpty() = withContext(Dispatchers.IO) {
        val existing = userDao.getUserById("user_biz1")
        if (existing != null) return@withContext

        // 1. Users
        userDao.insertUsers(
            UserEntity(
                id = "user_biz1",
                username = "business_apex",
                fullName = "David Chen",
                email = "david@apexlogistics.com",
                role = UserRole.BUSINESS_OWNER,
                organizationOrBusiness = "Apex Global Logistics & Retail Ltd.",
                phone = "+1 (555) 342-9810"
            ),
            UserEntity(
                id = "user_insp1",
                username = "inspector_sarah",
                fullName = "Officer Sarah Jenkins",
                email = "s.jenkins@metrology.gov",
                role = UserRole.INSPECTOR,
                organizationOrBusiness = "National Metrology Directorate",
                phone = "+1 (555) 789-2341"
            ),
            UserEntity(
                id = "user_admin1",
                username = "admin_director",
                fullName = "Director Robert Miller",
                email = "admin@metrology.gov",
                role = UserRole.ADMIN,
                organizationOrBusiness = "Central Weights & Measures Authority",
                phone = "+1 (555) 901-4455"
            )
        )

        // 2. Instruments
        val inst1 = InstrumentEntity(
            id = "inst_id_1",
            instrumentId = "INST-2026-0841",
            name = "Precision Lab Balance",
            type = "Digital weighing scale",
            category = "Laboratory",
            manufacturer = "Mettler Toledo",
            modelNumber = "Precision-X300",
            serialNumber = "SN-MT-90421-2025",
            capacity = "30 kg",
            unitOfMeasurement = "kg",
            location = "Apex Central Lab, Zone B, Room 102",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            ownerId = "user_biz1",
            purchaseDate = "2025-01-15",
            lastVerificationDate = "2026-08-10",
            nextVerificationDate = "2027-08-10",
            status = InstrumentStatus.CERTIFICATE_GENERATED,
            permissibleTolerancePercentage = 0.05,
            riskScore = RiskLevel.LOW,
            riskReason = "Optimal calibration linearity. Regular maintenance record."
        )

        val inst2 = InstrumentEntity(
            id = "inst_id_2",
            instrumentId = "INST-2026-0842",
            name = "Heavy Duty Platform Scale",
            type = "Platform scale",
            category = "Industrial",
            manufacturer = "Avery Weigh-Tronix",
            modelNumber = "BridgeMaster Pro-5000",
            serialNumber = "SN-AW-77821-2024",
            capacity = "1000 kg",
            unitOfMeasurement = "kg",
            location = "Apex Freight Terminal, Loading Dock #4",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            ownerId = "user_biz1",
            purchaseDate = "2024-06-20",
            lastVerificationDate = "2025-08-15",
            nextVerificationDate = "2026-08-15",
            status = InstrumentStatus.INSPECTION_SCHEDULED,
            permissibleTolerancePercentage = 0.1,
            riskScore = RiskLevel.MEDIUM,
            riskReason = "High usage cycle; approaching 12 months since last full calibration."
        )

        val inst3 = InstrumentEntity(
            id = "inst_id_3",
            instrumentId = "INST-2026-0843",
            name = "Dual-Nozzle Fuel Meter",
            type = "Petrol pump measuring instrument",
            category = "Petroleum",
            manufacturer = "Gilbarco Veeder-Root",
            modelNumber = "Horizon-Plus-80",
            serialNumber = "SN-GV-33109-2025",
            capacity = "80 L/min",
            unitOfMeasurement = "L",
            location = "Apex Express Station #12, Pump 3",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            ownerId = "user_biz1",
            purchaseDate = "2025-03-10",
            lastVerificationDate = "2025-09-01",
            nextVerificationDate = "2026-09-01",
            status = InstrumentStatus.SUBMITTED,
            permissibleTolerancePercentage = 0.2,
            riskScore = RiskLevel.LOW,
            riskReason = "Annual flow nozzle verification due."
        )

        val inst4 = InstrumentEntity(
            id = "inst_id_4",
            instrumentId = "INST-2026-0844",
            name = "Highway Truck Weighbridge",
            type = "Weighbridge",
            category = "Industrial",
            manufacturer = "Cardinal Scale",
            modelNumber = "EPR-60-10",
            serialNumber = "SN-CS-44982-2023",
            capacity = "60 Ton",
            unitOfMeasurement = "Ton",
            location = "Apex Logistics Hub, Inbound Gate",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            ownerId = "user_biz1",
            purchaseDate = "2023-11-05",
            lastVerificationDate = "2025-08-01",
            nextVerificationDate = "2026-08-01",
            status = InstrumentStatus.UNDER_INSPECTION,
            permissibleTolerancePercentage = 0.15,
            riskScore = RiskLevel.HIGH,
            riskReason = "Heavy continuous tonnage; zero balance drift noted by inspector."
        )

        val inst5 = InstrumentEntity(
            id = "inst_id_5",
            instrumentId = "INST-2026-0845",
            name = "Retail Price Computing Scale",
            type = "Retail weighing machine",
            category = "Retail",
            manufacturer = "CAS Corporation",
            modelNumber = "CL-5000",
            serialNumber = "SN-CAS-11204-2024",
            capacity = "15 kg",
            unitOfMeasurement = "kg",
            location = "Apex Fresh Supermarket, Counter 2",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            ownerId = "user_biz1",
            purchaseDate = "2024-02-18",
            lastVerificationDate = "2025-08-10",
            nextVerificationDate = "Verification Failed",
            status = InstrumentStatus.FAILED,
            permissibleTolerancePercentage = 0.05,
            riskScore = RiskLevel.HIGH,
            riskReason = "Exceeded allowable error by +0.12 kg on 10 kg standard weight."
        )

        instrumentDao.insertInstruments(listOf(inst1, inst2, inst3, inst4, inst5))

        // 3. Verification Requests
        val req1 = VerificationRequestEntity(
            id = "req_1",
            requestId = "REQ-849102",
            instrumentId = "INST-2026-0841",
            instrumentName = "Precision Lab Balance",
            instrumentType = "Digital weighing scale",
            businessId = "user_biz1",
            businessName = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Central Lab, Zone B, Room 102",
            submittedAt = System.currentTimeMillis() - 86400000L * 15,
            scheduledDate = "2026-08-10",
            assignedInspectorId = "user_insp1",
            assignedInspectorName = "Officer Sarah Jenkins",
            status = InstrumentStatus.CERTIFICATE_GENERATED,
            notes = "Annual scheduled verification completed."
        )

        val req2 = VerificationRequestEntity(
            id = "req_2",
            requestId = "REQ-849103",
            instrumentId = "INST-2026-0842",
            instrumentName = "Heavy Duty Platform Scale",
            instrumentType = "Platform scale",
            businessId = "user_biz1",
            businessName = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Freight Terminal, Loading Dock #4",
            submittedAt = System.currentTimeMillis() - 86400000L * 3,
            scheduledDate = "Tomorrow at 10:00 AM",
            assignedInspectorId = "user_insp1",
            assignedInspectorName = "Officer Sarah Jenkins",
            status = InstrumentStatus.INSPECTION_SCHEDULED,
            notes = "Annual statutory reverification before expiry."
        )

        val req3 = VerificationRequestEntity(
            id = "req_3",
            requestId = "REQ-849104",
            instrumentId = "INST-2026-0843",
            instrumentName = "Dual-Nozzle Fuel Meter",
            instrumentType = "Petrol pump measuring instrument",
            businessId = "user_biz1",
            businessName = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Express Station #12, Pump 3",
            submittedAt = System.currentTimeMillis() - 3600000L * 4,
            scheduledDate = "Unscheduled",
            assignedInspectorId = "user_insp1",
            assignedInspectorName = "Officer Sarah Jenkins",
            status = InstrumentStatus.SUBMITTED,
            notes = "Routine verification for volumetric flow meters."
        )

        val req4 = VerificationRequestEntity(
            id = "req_4",
            requestId = "REQ-849105",
            instrumentId = "INST-2026-0844",
            instrumentName = "Highway Truck Weighbridge",
            instrumentType = "Weighbridge",
            businessId = "user_biz1",
            businessName = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Logistics Hub, Inbound Gate",
            submittedAt = System.currentTimeMillis() - 86400000L * 2,
            scheduledDate = "Today (In Progress)",
            assignedInspectorId = "user_insp1",
            assignedInspectorName = "Officer Sarah Jenkins",
            status = InstrumentStatus.UNDER_INSPECTION,
            notes = "Test weights truck on site. Multi-axle load testing."
        )

        verificationRequestDao.insertRequests(listOf(req1, req2, req3, req4))

        // 4. Certificates
        val cert1 = CertificateEntity(
            certificateNumber = "CERT-2026-NLM-0841",
            instrumentId = "INST-2026-0841",
            instrumentName = "Precision Lab Balance",
            instrumentType = "Digital weighing scale",
            manufacturer = "Mettler Toledo",
            modelNumber = "Precision-X300",
            serialNumber = "SN-MT-90421-2025",
            capacity = "30 kg",
            unit = "kg",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Central Lab, Zone B, Room 102",
            verificationDate = "2026-08-10",
            validUntil = "2027-08-10",
            inspectorName = "Officer Sarah Jenkins",
            inspectorId = "user_insp1",
            status = "VERIFIED",
            standardCode = "OIML R 76-1 / Class II High Accuracy Standard",
            qrPayload = "https://metrology.gov.verify/cert/CERT-2026-NLM-0841"
        )

        val cert2Expiring = CertificateEntity(
            certificateNumber = "CERT-2025-NLM-0219",
            instrumentId = "INST-2026-0842",
            instrumentName = "Heavy Duty Platform Scale",
            instrumentType = "Platform scale",
            manufacturer = "Avery Weigh-Tronix",
            modelNumber = "BridgeMaster Pro-5000",
            serialNumber = "SN-AW-77821-2024",
            capacity = "1000 kg",
            unit = "kg",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Freight Terminal, Loading Dock #4",
            verificationDate = "2025-09-05",
            validUntil = "2026-09-05", // 11 days left (Expiring Soon!)
            inspectorName = "Officer Sarah Jenkins",
            inspectorId = "user_insp1",
            status = "VERIFIED",
            standardCode = "OIML R 76-1 / Class III Industrial",
            qrPayload = "https://metrology.gov.verify/cert/CERT-2025-NLM-0219"
        )

        val cert3Expired = CertificateEntity(
            certificateNumber = "CERT-2024-NLM-0782",
            instrumentId = "INST-2026-0845",
            instrumentName = "Retail Price Computing Scale",
            instrumentType = "Retail weighing machine",
            manufacturer = "CAS Corporation",
            modelNumber = "CL-5000",
            serialNumber = "SN-CAS-11204-2024",
            capacity = "15 kg",
            unit = "kg",
            ownerBusiness = "Apex Global Logistics & Retail Ltd.",
            location = "Apex Fresh Supermarket, Counter 2",
            verificationDate = "2024-06-15",
            validUntil = "2025-06-15", // Expired
            inspectorName = "Officer Sarah Jenkins",
            inspectorId = "user_insp1",
            status = "EXPIRED",
            standardCode = "OIML R 76-1 / Class III Commercial",
            qrPayload = "https://metrology.gov.verify/cert/CERT-2024-NLM-0782"
        )

        certificateDao.insertCertificates(listOf(cert1, cert2Expiring, cert3Expired))

        // 5. Notifications
        notificationDao.insertNotifications(
            listOf(
                NotificationEntity(
                    id = "notif_1",
                    userId = "user_biz1",
                    title = "Certificate Expiring Soon",
                    message = "Certificate CERT-2025-NLM-0219 for Heavy Duty Platform Scale expires in 11 days. Re-verification scheduled.",
                    type = "WARNING"
                ),
                NotificationEntity(
                    id = "notif_2",
                    userId = "user_biz1",
                    title = "Inspection Completed",
                    message = "Officer Sarah Jenkins approved certificate CERT-2026-NLM-0841 for Precision Lab Balance.",
                    type = "SUCCESS"
                ),
                NotificationEntity(
                    id = "notif_3",
                    userId = "user_insp1",
                    title = "New Inspection Assigned",
                    message = "Verification request REQ-849103 for Dual-Nozzle Fuel Meter is assigned to you.",
                    type = "INFO"
                )
            )
        )

        // 6. Audit Logs
        auditLogDao.insertLogs(
            listOf(
                AuditLogEntity(
                    action = "CERTIFICATE_ISSUED",
                    performedBy = "Officer Sarah Jenkins",
                    role = "INSPECTOR",
                    details = "Issued certificate CERT-2026-NLM-0841 after verifying error < 0.02kg"
                ),
                AuditLogEntity(
                    action = "INSPECTION_SCHEDULED",
                    performedBy = "Officer Sarah Jenkins",
                    role = "INSPECTOR",
                    details = "Scheduled on-site verification for REQ-849103"
                ),
                AuditLogEntity(
                    action = "SYSTEM_INITIALIZED",
                    performedBy = "System Core",
                    role = "SYSTEM",
                    details = "Legal Metrology Digital Verification System initialized with ISO 17025 tables"
                )
            )
        )
    }
}
