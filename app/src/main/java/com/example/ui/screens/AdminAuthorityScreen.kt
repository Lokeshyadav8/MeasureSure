package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.InstrumentEntity
import com.example.data.model.RiskLevel
import com.example.ui.components.RiskScoreBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminAuthorityScreen(
    instruments: List<InstrumentEntity>,
    auditLogs: List<AuditLogEntity>,
    onViewInstrument: (InstrumentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("AUDIT") } // AUDIT, RISK, INSPECTORS

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Admin Header
        Surface(color = Navy900) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authority Directorate Oversight", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Navy800
                    ) {
                        Text(
                            text = "CENTRAL ADMIN",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Navigation Tabs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminSubTab(title = "Audit Logs", count = "${auditLogs.size}", isSelected = selectedTab == "AUDIT", onClick = { selectedTab = "AUDIT" })
                    AdminSubTab(title = "Risk Radar", count = "${instruments.count { it.riskScore == RiskLevel.HIGH }} High", isSelected = selectedTab == "RISK", onClick = { selectedTab = "RISK" })
                    AdminSubTab(title = "Inspectors", count = "3 Active", isSelected = selectedTab == "INSPECTORS", onClick = { selectedTab = "INSPECTORS" })
                }
            }
        }

        // Sub Tab Content
        when (selectedTab) {
            "AUDIT" -> {
                AuditLogsView(auditLogs = auditLogs)
            }
            "RISK" -> {
                RiskRadarView(instruments = instruments, onViewInstrument = onViewInstrument)
            }
            "INSPECTORS" -> {
                InspectorsDirectoryView()
            }
        }
    }
}

@Composable
private fun AdminSubTab(
    title: String,
    count: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (isSelected) CyanPrimary else Navy800, RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) CyanLight else Navy700, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, color = if (isSelected) Color.White else Slate300, fontSize = 11.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Spacer(modifier = Modifier.width(6.dp))
            Surface(shape = RoundedCornerShape(4.dp), color = if (isSelected) Navy900 else Slate700) {
                Text(text = count, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
        }
    }
}

@Composable
fun AuditLogsView(auditLogs: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Statutory Metrological Audit Trail",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Navy900
            )
            Text(
                text = "Immutable log of all registrations, inspections, certificate issues and status transitions.",
                fontSize = 11.5.sp,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                when (log.action) {
                                    "CERTIFICATE_ISSUED" -> StatusVerifiedGreenBg
                                    "INSPECTION_FAILED" -> StatusFailedRedBg
                                    "VERIFICATION_REQUESTED" -> StatusPendingOrangeBg
                                    else -> StatusInfoBlueBg
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (log.action) {
                                "CERTIFICATE_ISSUED" -> Icons.Default.Verified
                                "INSPECTION_FAILED" -> Icons.Default.Cancel
                                "VERIFICATION_REQUESTED" -> Icons.Default.Assignment
                                else -> Icons.Default.History
                            },
                            contentDescription = null,
                            tint = when (log.action) {
                                "CERTIFICATE_ISSUED" -> StatusVerifiedGreen
                                "INSPECTION_FAILED" -> StatusFailedRed
                                "VERIFICATION_REQUESTED" -> StatusPendingOrange
                                else -> StatusInfoBlue
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.action.replace("_", " "),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Navy900
                            )
                            Text(
                                text = SimpleDateFormat("HH:mm • MM/dd", Locale.US).format(Date(log.timestamp)),
                                fontSize = 10.sp,
                                color = Slate600
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = log.details,
                            fontSize = 11.5.sp,
                            color = Slate800
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Actor: ${log.performedBy} (${log.role})",
                            fontSize = 10.5.sp,
                            color = Slate600
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RiskRadarView(
    instruments: List<InstrumentEntity>,
    onViewInstrument: (InstrumentEntity) -> Unit
) {
    val highRisk = instruments.filter { it.riskScore == RiskLevel.HIGH }
    val medRisk = instruments.filter { it.riskScore == RiskLevel.MEDIUM }
    val lowRisk = instruments.filter { it.riskScore == RiskLevel.LOW }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "AI Predictive Risk & Anomaly Radar",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Navy900
            )
            Text(
                text = "Calculates failure probabilities based on calibration drift, usage volume, and sensor age.",
                fontSize = 11.5.sp,
                color = Slate600
            )
        }

        // Summary row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RiskCountCard(title = "High Risk", count = "${highRisk.size}", color = StatusFailedRed, modifier = Modifier.weight(1f))
                RiskCountCard(title = "Medium Risk", count = "${medRisk.size}", color = StatusPendingOrange, modifier = Modifier.weight(1f))
                RiskCountCard(title = "Low Risk", count = "${lowRisk.size}", color = StatusVerifiedGreen, modifier = Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = "Instruments Requiring Attention",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Navy900
            )
        }

        items(highRisk + medRisk) { inst ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewInstrument(inst) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = inst.instrumentId, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = CyanPrimary, fontFamily = FontFamily.Monospace)
                        RiskScoreBadge(riskLevel = inst.riskScore)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = inst.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Text(text = "Reason: ${inst.riskReason}", fontSize = 11.sp, color = Slate700)
                }
            }
        }
    }
}

@Composable
private fun RiskCountCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
            Text(text = title, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun InspectorsDirectoryView() {
    val inspectors = listOf(
        Triple("Officer Sarah Jenkins", "Senior Metrology Officer", "Zone 1 - Industrial & Weighbridges (14 Completed this month)"),
        Triple("Officer Marcus Vance", "Flow & Petroleum Specialist", "Zone 2 - Fuel Meters & Gas Dispensers (9 Completed)"),
        Triple("Officer Elena Rostova", "Laboratory Precision Inspector", "Zone 3 - Micro balances & Class II scales (18 Completed)")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "Certified Inspector Directorate", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(inspectors) { (name, role, jurisdiction) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Navy900, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Navy900)
                        Text(text = role, fontSize = 11.5.sp, color = CyanPrimary, fontWeight = FontWeight.SemiBold)
                        Text(text = jurisdiction, fontSize = 11.sp, color = Slate600)
                    }
                }
            }
        }
    }
}
