package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AnomalyResult
import com.example.ai.GeminiMetrologyService
import com.example.ai.OcrExtractionResult
import com.example.ai.RiskAssessmentResult
import com.example.data.db.MetrologyDatabase
import com.example.data.model.*
import com.example.data.repository.MetrologyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

data class TestReadingInput(
    val id: String = UUID.randomUUID().toString(),
    val referenceValue: String = "",
    val actualReading: String = "",
    val tolerancePercentage: String = "0.5"
) {
    val refNum: Double get() = referenceValue.toDoubleOrNull() ?: 0.0
    val actualNum: Double get() = actualReading.toDoubleOrNull() ?: 0.0
    val tolPct: Double get() = tolerancePercentage.toDoubleOrNull() ?: 0.5

    val error: Double get() = actualNum - refNum
    val percentageError: Double get() = if (refNum != 0.0) ((actualNum - refNum) / refNum) * 100.0 else 0.0
    val maxPermissibleError: Double get() = refNum * (tolPct / 100.0)
    val isPass: Boolean get() = refNum > 0 && abs(error) <= maxPermissibleError
}

data class MetrologyUiState(
    val currentRole: UserRole = UserRole.BUSINESS_OWNER,
    val currentUserId: String = "user_biz1",
    val currentUserName: String = "David Chen",
    val currentBusinessOrOrg: String = "Apex Global Logistics & Retail Ltd.",
    val isDemoFlowActive: Boolean = false,
    val demoStep: Int = 0,
    val demoStatusMessage: String = "",
    val searchQuery: String = "",
    val selectedStatusFilter: String = "ALL", // ALL, VERIFIED, PENDING, FAILED, EXPIRED, EXPIRING_SOON
    val selectedCategoryFilter: String = "ALL",
    val selectedTab: String = "DASHBOARD", // DASHBOARD, INSTRUMENTS, REQUESTS, INSPECT, CERTIFICATES, QR_VERIFY, AUDIT, AI_ANALYTICS, LANDING
    val activeCertificateToView: CertificateEntity? = null,
    val activeRequestToInspect: VerificationRequestEntity? = null,
    val activeInstrumentToView: InstrumentEntity? = null,
    val publicVerifyQuery: String = "",
    val publicVerifyResult: CertificateEntity? = null,
    val publicVerifySearched: Boolean = false,
    val isAiAnalyzing: Boolean = false,
    val aiAnomalyResult: AnomalyResult? = null,
    val aiOcrResult: OcrExtractionResult? = null,
    val aiRiskAssessment: RiskAssessmentResult? = null,
    val toastMessage: String? = null
)

class MetrologyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MetrologyRepository
    private val aiService = GeminiMetrologyService()

    private val _uiState = MutableStateFlow(MetrologyUiState())
    val uiState: StateFlow<MetrologyUiState> = _uiState.asStateFlow()

    // Inspection inputs state
    private val _inspectionReadings = MutableStateFlow<List<TestReadingInput>>(
        listOf(
            TestReadingInput(referenceValue = "5.000", actualReading = "5.002", tolerancePercentage = "0.1"),
            TestReadingInput(referenceValue = "10.000", actualReading = "10.004", tolerancePercentage = "0.1"),
            TestReadingInput(referenceValue = "20.000", actualReading = "20.008", tolerancePercentage = "0.1")
        )
    )
    val inspectionReadings: StateFlow<List<TestReadingInput>> = _inspectionReadings.asStateFlow()

    val allInstruments: StateFlow<List<InstrumentEntity>>
    val allRequests: StateFlow<List<VerificationRequestEntity>>
    val allInspections: StateFlow<List<InspectionEntity>>
    val allCertificates: StateFlow<List<CertificateEntity>>
    val allNotifications: StateFlow<List<NotificationEntity>>
    val allAuditLogs: StateFlow<List<AuditLogEntity>>

    init {
        val db = MetrologyDatabase.getDatabase(application)
        repository = MetrologyRepository(
            userDao = db.userDao(),
            instrumentDao = db.instrumentDao(),
            verificationRequestDao = db.verificationRequestDao(),
            inspectionDao = db.inspectionDao(),
            inspectionReadingDao = db.inspectionReadingDao(),
            certificateDao = db.certificateDao(),
            notificationDao = db.notificationDao(),
            auditLogDao = db.auditLogDao()
        )

        allInstruments = repository.allInstruments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allRequests = repository.allRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allInspections = repository.allInspections.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allCertificates = repository.allCertificates.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allNotifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allAuditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.seedDemoDataIfEmpty()
        }
    }

    fun setRole(role: UserRole) {
        val (id, name, org) = when (role) {
            UserRole.BUSINESS_OWNER -> Triple("user_biz1", "David Chen", "Apex Global Logistics & Retail Ltd.")
            UserRole.INSPECTOR -> Triple("user_insp1", "Officer Sarah Jenkins", "National Metrology Directorate")
            UserRole.ADMIN -> Triple("user_admin1", "Director Robert Miller", "Central Weights & Measures Authority")
            UserRole.PUBLIC -> Triple("user_public", "Public Citizen / Consumer", "Consumer Verification Portal")
        }
        val defaultTab = when (role) {
            UserRole.BUSINESS_OWNER -> "DASHBOARD"
            UserRole.INSPECTOR -> "REQUESTS"
            UserRole.ADMIN -> "DASHBOARD"
            UserRole.PUBLIC -> "QR_VERIFY"
        }
        _uiState.update {
            it.copy(
                currentRole = role,
                currentUserId = id,
                currentUserName = name,
                currentBusinessOrOrg = org,
                selectedTab = defaultTab
            )
        }
    }

    fun setSelectedTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(filter: String) {
        _uiState.update { it.copy(selectedStatusFilter = filter) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun viewCertificate(cert: CertificateEntity?) {
        _uiState.update { it.copy(activeCertificateToView = cert) }
    }

    fun viewInstrumentDetails(instrument: InstrumentEntity?) {
        _uiState.update { it.copy(activeInstrumentToView = instrument) }
    }

    fun openInspectionWorkspace(request: VerificationRequestEntity) {
        _uiState.update {
            it.copy(
                activeRequestToInspect = request,
                selectedTab = "INSPECT",
                aiAnomalyResult = null
            )
        }
        // Initialize default readings based on instrument type
        _inspectionReadings.value = listOf(
            TestReadingInput(referenceValue = "5.000", actualReading = "5.002", tolerancePercentage = "0.1"),
            TestReadingInput(referenceValue = "10.000", actualReading = "10.004", tolerancePercentage = "0.1"),
            TestReadingInput(referenceValue = "20.000", actualReading = "20.007", tolerancePercentage = "0.1")
        )
    }

    fun addInspectionReading() {
        val current = _inspectionReadings.value.toMutableList()
        val nextVal = ((current.size + 1) * 10).toDouble()
        current.add(TestReadingInput(referenceValue = String.format(Locale.US, "%.3f", nextVal), actualReading = String.format(Locale.US, "%.3f", nextVal), tolerancePercentage = "0.1"))
        _inspectionReadings.value = current
    }

    fun updateInspectionReading(index: Int, ref: String, actual: String, tol: String) {
        val current = _inspectionReadings.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(
                referenceValue = ref,
                actualReading = actual,
                tolerancePercentage = tol
            )
            _inspectionReadings.value = current
        }
    }

    fun removeInspectionReading(index: Int) {
        val current = _inspectionReadings.value.toMutableList()
        if (current.size > 1 && index in current.indices) {
            current.removeAt(index)
            _inspectionReadings.value = current
        }
    }

    // Business Owner: Register new instrument
    fun registerInstrument(
        name: String,
        type: String,
        category: String,
        manufacturer: String,
        modelNumber: String,
        serialNumber: String,
        capacity: String,
        unit: String,
        location: String,
        tolerance: Double
    ) {
        viewModelScope.launch {
            val instrument = repository.registerInstrument(
                name = name,
                type = type,
                category = category,
                manufacturer = manufacturer,
                modelNumber = modelNumber,
                serialNumber = serialNumber,
                capacity = capacity,
                unit = unit,
                location = location,
                ownerBusiness = _uiState.value.currentBusinessOrOrg,
                ownerId = _uiState.value.currentUserId,
                tolerancePct = tolerance
            )
            showToast("Instrument ${instrument.instrumentId} registered successfully!")
            _uiState.update { it.copy(selectedTab = "INSTRUMENTS") }
        }
    }

    // Business Owner: Request verification
    fun requestVerification(instrument: InstrumentEntity) {
        viewModelScope.launch {
            val req = repository.submitVerificationRequest(instrument)
            showToast("Verification requested! Request ID: ${req.requestId}")
            _uiState.update { it.copy(selectedTab = "REQUESTS") }
        }
    }

    // Inspector: Run AI Anomaly Analysis
    fun runAnomalyAnalysis(unit: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiAnalyzing = true) }
            val readings = _inspectionReadings.value
            val refs = readings.map { it.refNum }
            val actuals = readings.map { it.actualNum }
            val tols = readings.map { it.maxPermissibleError }

            val result = aiService.detectAnomalies(refs, actuals, tols, unit)
            _uiState.update {
                it.copy(
                    isAiAnalyzing = false,
                    aiAnomalyResult = result
                )
            }
        }
    }

    // AI OCR extract nameplate
    fun runAiOcrScan(sampleText: String = "Mettler Toledo Precision-X300 S/N: SN-88392-2026 Max: 30kg e=1g") {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiAnalyzing = true) }
            val result = aiService.extractInstrumentSpecs(sampleText)
            _uiState.update {
                it.copy(
                    isAiAnalyzing = false,
                    aiOcrResult = result
                )
            }
            showToast("AI OCR extracted instrument parameters!")
        }
    }

    // AI Risk calculation
    fun evaluateRisk(instrument: InstrumentEntity) {
        val result = aiService.assessRisk(
            failuresCount = if (instrument.status == InstrumentStatus.FAILED) 1 else 0,
            lastErrorPct = instrument.permissibleTolerancePercentage,
            ageInMonths = 18,
            instrumentType = instrument.type
        )
        _uiState.update { it.copy(aiRiskAssessment = result) }
    }

    // Inspector: Submit verification report
    fun submitInspection(
        requestId: String,
        instrumentId: String,
        standardMassUsed: String,
        remarks: String,
        observations: String,
        isPass: Boolean
    ) {
        viewModelScope.launch {
            val readingsInputs = _inspectionReadings.value
            val readingEntities = readingsInputs.mapIndexed { idx, input ->
                InspectionReadingEntity(
                    inspectionId = "",
                    testIndex = idx + 1,
                    referenceValue = input.refNum,
                    actualReading = input.actualNum,
                    unit = "kg",
                    calculatedError = input.error,
                    percentageError = input.percentageError,
                    permissibleError = input.maxPermissibleError,
                    isPass = input.isPass
                )
            }

            val cert = repository.submitInspection(
                requestId = requestId,
                instrumentId = instrumentId,
                inspectorId = _uiState.value.currentUserId,
                inspectorName = _uiState.value.currentUserName,
                standardMassUsed = standardMassUsed,
                readings = readingEntities,
                remarks = remarks,
                observations = observations,
                isPass = isPass
            )

            if (cert != null) {
                showToast("Certificate ${cert.certificateNumber} generated & published!")
                _uiState.update {
                    it.copy(
                        activeCertificateToView = cert,
                        selectedTab = "CERTIFICATES"
                    )
                }
            } else {
                showToast("Inspection recorded as FAILED. Correction notice sent.")
                _uiState.update { it.copy(selectedTab = "REQUESTS") }
            }
        }
    }

    // Public QR Verification Lookup
    fun lookupPublicCertificate(certNumber: String) {
        viewModelScope.launch {
            val trimmed = certNumber.trim()
            _uiState.update { it.copy(publicVerifyQuery = trimmed) }
            val cert = repository.getCertificateByNumber(trimmed)
            _uiState.update {
                it.copy(
                    publicVerifyResult = cert,
                    publicVerifySearched = true
                )
            }
        }
    }

    fun clearPublicLookup() {
        _uiState.update {
            it.copy(
                publicVerifyQuery = "",
                publicVerifyResult = null,
                publicVerifySearched = false
            )
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // 5-Minute Interactive Hackathon Judge Guided Demo Flow
    fun startGuidedJudgeDemo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDemoFlowActive = true,
                    demoStep = 1,
                    demoStatusMessage = "Step 1: Switched to Business Owner. Registering new digital weighing scale..."
                )
            }
            setRole(UserRole.BUSINESS_OWNER)
            setSelectedTab("INSTRUMENTS")
            delay(1500)

            // Step 2: Auto register instrument
            val newInst = repository.registerInstrument(
                name = "Demo Precision Platform Scale",
                type = "Platform scale",
                category = "Commercial",
                manufacturer = "Mettler Toledo",
                modelNumber = "Demo-Scale-2026",
                serialNumber = "SN-DEMO-9912",
                capacity = "100 kg",
                unit = "kg",
                location = "Exhibition Hall A, Booth 104",
                ownerBusiness = "Apex Global Logistics & Retail Ltd.",
                ownerId = "user_biz1",
                tolerancePct = 0.05
            )
            _uiState.update {
                it.copy(
                    demoStep = 2,
                    demoStatusMessage = "Step 2: Instrument ${newInst.instrumentId} registered! Submitting verification request..."
                )
            }
            delay(1500)

            // Step 3: Submit request
            val req = repository.submitVerificationRequest(newInst)
            setSelectedTab("REQUESTS")
            _uiState.update {
                it.copy(
                    demoStep = 3,
                    demoStatusMessage = "Step 3: Verification Request ${req.requestId} submitted. Switching role to Certified Inspector..."
                )
            }
            delay(2000)

            // Step 4: Switch to Inspector
            setRole(UserRole.INSPECTOR)
            openInspectionWorkspace(req)
            _uiState.update {
                it.copy(
                    demoStep = 4,
                    demoStatusMessage = "Step 4: Inspector opened verification workspace. Loading ISO standard test masses..."
                )
            }
            delay(1800)

            // Step 5: Test readings & calculate error
            _inspectionReadings.value = listOf(
                TestReadingInput(referenceValue = "10.000", actualReading = "10.002", tolerancePercentage = "0.05"),
                TestReadingInput(referenceValue = "25.000", actualReading = "25.003", tolerancePercentage = "0.05"),
                TestReadingInput(referenceValue = "50.000", actualReading = "50.005", tolerancePercentage = "0.05")
            )
            _uiState.update {
                it.copy(
                    demoStep = 5,
                    demoStatusMessage = "Step 5: Reference values vs Actual readings entered. Calculating measurement error: +0.005 kg (+0.01% error <= 0.05% tol) -> PASS."
                )
            }
            delay(2200)

            // Step 6: AI Anomaly check
            runAnomalyAnalysis("kg")
            _uiState.update {
                it.copy(
                    demoStep = 6,
                    demoStatusMessage = "Step 6: AI Anomaly Diagnostics executed -> Standard linearity confirmed. Issuing official Digital Certificate..."
                )
            }
            delay(2000)

            // Step 7: Issue certificate
            val readingEntities = _inspectionReadings.value.mapIndexed { idx, input ->
                InspectionReadingEntity(
                    inspectionId = "",
                    testIndex = idx + 1,
                    referenceValue = input.refNum,
                    actualReading = input.actualNum,
                    unit = "kg",
                    calculatedError = input.error,
                    percentageError = input.percentageError,
                    permissibleError = input.maxPermissibleError,
                    isPass = input.isPass
                )
            }
            val cert = repository.submitInspection(
                requestId = req.id,
                instrumentId = newInst.instrumentId,
                inspectorId = "user_insp1",
                inspectorName = "Officer Sarah Jenkins",
                standardMassUsed = "Class M1 Stainless Steel Test Weight Set #TW-09",
                readings = readingEntities,
                remarks = "All load points within legal metrological tolerance. Sealing wire affixed.",
                observations = "Zero-point balance perfect, eccentric loading error within class limits.",
                isPass = true
            )
            _uiState.update {
                it.copy(
                    demoStep = 7,
                    activeCertificateToView = cert,
                    demoStatusMessage = "Step 7: Official Digital Certificate ${cert?.certificateNumber} Generated with QR Code!"
                )
            }
            setSelectedTab("CERTIFICATES")
            delay(2500)

            // Step 8: Public QR scan lookup
            if (cert != null) {
                setRole(UserRole.PUBLIC)
                lookupPublicCertificate(cert.certificateNumber)
                _uiState.update {
                    it.copy(
                        demoStep = 8,
                        demoStatusMessage = "Step 8: Public verification scan validated -> ✓ VERIFIED status confirmed without exposing private business data!"
                    )
                }
                delay(2500)
            }

            // Step 9: Admin Analytics
            setRole(UserRole.ADMIN)
            setSelectedTab("DASHBOARD")
            _uiState.update {
                it.copy(
                    demoStep = 9,
                    demoStatusMessage = "Step 9: Admin Authority Dashboard displaying real-time compliance metrics, audit logs & risk radars. Complete flow completed in under 2 mins!"
                )
            }
            delay(3000)
            _uiState.update { it.copy(isDemoFlowActive = false) }
        }
    }
}
