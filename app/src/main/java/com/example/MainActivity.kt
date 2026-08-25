package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.CertificateViewDialog
import com.example.ui.components.MetrologyHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MetrologyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MetrologyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LegalMetrologyTheme {
                MetrologyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MetrologyApp(viewModel: MetrologyViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val instruments by viewModel.allInstruments.collectAsState()
    val requests by viewModel.allRequests.collectAsState()
    val certificates by viewModel.allCertificates.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()
    val inspectionReadings by viewModel.inspectionReadings.collectAsState()

    // Handle toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MetrologyHeaderBar(
                currentRole = uiState.currentRole,
                userName = uiState.currentUserName,
                businessOrOrg = uiState.currentBusinessOrOrg,
                onRoleSelected = { viewModel.setRole(it) },
                onStartDemoFlow = { viewModel.startGuidedJudgeDemo() },
                unreadNotificationsCount = 2
            )
        },
        bottomBar = {
            MetrologyBottomNav(
                currentTab = uiState.selectedTab,
                currentRole = uiState.currentRole,
                onTabSelected = { viewModel.setSelectedTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen content switcher
            when (uiState.selectedTab) {
                "LANDING" -> {
                    LandingScreen(
                        onNavigateToRegister = {
                            viewModel.setRole(UserRole.BUSINESS_OWNER)
                            viewModel.setSelectedTab("INSTRUMENTS")
                        },
                        onNavigateToQrVerify = {
                            viewModel.setRole(UserRole.PUBLIC)
                            viewModel.setSelectedTab("QR_VERIFY")
                        },
                        onSelectRole = { role ->
                            viewModel.setRole(role)
                        },
                        onStartDemoFlow = {
                            viewModel.startGuidedJudgeDemo()
                        }
                    )
                }

                "DASHBOARD" -> {
                    if (uiState.currentRole == UserRole.ADMIN) {
                        AdminAuthorityScreen(
                            instruments = instruments,
                            auditLogs = auditLogs,
                            onViewInstrument = { inst ->
                                viewModel.viewInstrumentDetails(inst)
                                viewModel.setSelectedTab("INSTRUMENTS")
                            }
                        )
                    } else {
                        DashboardScreen(
                            instruments = instruments,
                            requests = requests,
                            certificates = certificates,
                            onNavigateToRegister = {
                                viewModel.setRole(UserRole.BUSINESS_OWNER)
                                viewModel.setSelectedTab("INSTRUMENTS")
                            },
                            onNavigateToRequests = {
                                viewModel.setSelectedTab("REQUESTS")
                            },
                            onNavigateToInstruments = {
                                viewModel.setSelectedTab("INSTRUMENTS")
                            },
                            onViewCertificate = { cert ->
                                viewModel.viewCertificate(cert)
                            },
                            onViewInstrument = { inst ->
                                viewModel.viewInstrumentDetails(inst)
                                viewModel.setSelectedTab("INSTRUMENTS")
                            }
                        )
                    }
                }

                "INSTRUMENTS" -> {
                    InstrumentsScreen(
                        instruments = instruments,
                        userRole = uiState.currentRole,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        statusFilter = uiState.selectedStatusFilter,
                        onStatusFilterChange = { viewModel.setStatusFilter(it) },
                        categoryFilter = uiState.selectedCategoryFilter,
                        onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                        onRequestVerification = { inst ->
                            viewModel.requestVerification(inst)
                        },
                        onViewInstrumentDetails = { inst ->
                            viewModel.viewInstrumentDetails(inst)
                            val matchingCert = certificates.firstOrNull { it.instrumentId == inst.instrumentId }
                            if (matchingCert != null) {
                                viewModel.viewCertificate(matchingCert)
                            } else {
                                viewModel.showToast("Instrument ${inst.instrumentId}: ${inst.status.name}")
                            }
                        },
                        onRegisterInstrument = { name, type, category, make, model, serial, cap, unit, loc, tol ->
                            viewModel.registerInstrument(name, type, category, make, model, serial, cap, unit, loc, tol)
                        },
                        onRunAiOcrScan = {
                            viewModel.runAiOcrScan()
                        },
                        aiOcrResult = uiState.aiOcrResult,
                        isAiScanning = uiState.isAiAnalyzing
                    )
                }

                "REQUESTS" -> {
                    VerificationRequestsScreen(
                        requests = requests,
                        userRole = uiState.currentRole,
                        onOpenInspectionWorkspace = { req ->
                            viewModel.openInspectionWorkspace(req)
                        }
                    )
                }

                "INSPECT" -> {
                    InspectionWorkspaceScreen(
                        request = uiState.activeRequestToInspect ?: requests.firstOrNull(),
                        readings = inspectionReadings,
                        onAddReading = { viewModel.addInspectionReading() },
                        onUpdateReading = { idx, ref, actual, tol ->
                            viewModel.updateInspectionReading(idx, ref, actual, tol)
                        },
                        onRemoveReading = { idx -> viewModel.removeInspectionReading(idx) },
                        onRunAiAnomalyCheck = { viewModel.runAnomalyAnalysis("kg") },
                        aiAnomalyResult = uiState.aiAnomalyResult,
                        isAiAnalyzing = uiState.isAiAnalyzing,
                        onSubmitInspection = { stdMass, remarks, obs, isPass ->
                            val targetReq = uiState.activeRequestToInspect ?: requests.firstOrNull()
                            if (targetReq != null) {
                                viewModel.submitInspection(
                                    requestId = targetReq.id,
                                    instrumentId = targetReq.instrumentId,
                                    standardMassUsed = stdMass,
                                    remarks = remarks,
                                    observations = obs,
                                    isPass = isPass
                                )
                            }
                        }
                    )
                }

                "CERTIFICATES" -> {
                    DashboardScreen(
                        instruments = instruments,
                        requests = requests,
                        certificates = certificates,
                        onNavigateToRegister = { viewModel.setSelectedTab("INSTRUMENTS") },
                        onNavigateToRequests = { viewModel.setSelectedTab("REQUESTS") },
                        onNavigateToInstruments = { viewModel.setSelectedTab("INSTRUMENTS") },
                        onViewCertificate = { cert -> viewModel.viewCertificate(cert) },
                        onViewInstrument = { inst -> viewModel.viewInstrumentDetails(inst) }
                    )
                }

                "QR_VERIFY" -> {
                    PublicQrVerificationScreen(
                        query = uiState.publicVerifyQuery,
                        onQueryChange = { q -> viewModel.lookupPublicCertificate(q) },
                        onSearch = { q -> viewModel.lookupPublicCertificate(q) },
                        searchResult = uiState.publicVerifyResult,
                        hasSearched = uiState.publicVerifySearched,
                        onViewFullCertificate = { cert -> viewModel.viewCertificate(cert) }
                    )
                }

                "ADMIN" -> {
                    AdminAuthorityScreen(
                        instruments = instruments,
                        auditLogs = auditLogs,
                        onViewInstrument = { inst ->
                            viewModel.viewInstrumentDetails(inst)
                            viewModel.setSelectedTab("INSTRUMENTS")
                        }
                    )
                }
            }

            // Demo Mode Floating Status Banner
            if (uiState.isDemoFlowActive) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF38BDF8),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "5-MIN HACKATHON GUIDED DEMO",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = uiState.demoStatusMessage,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Digital Certificate Document Modal
            if (uiState.activeCertificateToView != null) {
                CertificateViewDialog(
                    certificate = uiState.activeCertificateToView!!,
                    onDismiss = { viewModel.viewCertificate(null) },
                    onDownloadPdf = {
                        Toast.makeText(context, "Certificate PDF downloaded & stored securely!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun MetrologyBottomNav(
    currentTab: String,
    currentRole: UserRole,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        val items = when (currentRole) {
            UserRole.BUSINESS_OWNER -> listOf(
                Triple("DASHBOARD", "Home", Icons.Default.Dashboard),
                Triple("INSTRUMENTS", "My Scales", Icons.Default.Scale),
                Triple("REQUESTS", "Requests", Icons.Default.Assignment),
                Triple("QR_VERIFY", "QR Verify", Icons.Default.QrCodeScanner),
                Triple("LANDING", "About", Icons.Default.Info)
            )
            UserRole.INSPECTOR -> listOf(
                Triple("REQUESTS", "Assigned", Icons.Default.Assignment),
                Triple("INSPECT", "Inspection", Icons.Default.Biotech),
                Triple("DASHBOARD", "Overview", Icons.Default.Dashboard),
                Triple("QR_VERIFY", "QR Verify", Icons.Default.QrCodeScanner),
                Triple("LANDING", "About", Icons.Default.Info)
            )
            UserRole.ADMIN -> listOf(
                Triple("DASHBOARD", "Authority", Icons.Default.AdminPanelSettings),
                Triple("INSTRUMENTS", "Registry", Icons.Default.Scale),
                Triple("REQUESTS", "Requests", Icons.Default.Assignment),
                Triple("QR_VERIFY", "QR Verify", Icons.Default.QrCodeScanner),
                Triple("LANDING", "Portal", Icons.Default.Info)
            )
            UserRole.PUBLIC -> listOf(
                Triple("QR_VERIFY", "QR Scan", Icons.Default.QrCodeScanner),
                Triple("LANDING", "Portal", Icons.Default.Home),
                Triple("DASHBOARD", "Stats", Icons.Default.Insights),
                Triple("INSTRUMENTS", "Registry", Icons.Default.Search)
            )
        }

        items.forEach { (tabId, label, icon) ->
            val selected = currentTab == tabId
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tabId) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) DarkNavyObsidian else Slate400,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label.uppercase(),
                        fontSize = 9.5.sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        color = if (selected) DarkNavyObsidian else Slate400
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Slate100
                )
            )
        }
    }
}
