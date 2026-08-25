package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.ai.AnomalyResult
import com.example.data.model.VerificationRequestEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestReadingInput
import java.util.Locale

@Composable
fun InspectionWorkspaceScreen(
    request: VerificationRequestEntity?,
    readings: List<TestReadingInput>,
    onAddReading: () -> Unit,
    onUpdateReading: (index: Int, ref: String, actual: String, tol: String) -> Unit,
    onRemoveReading: (Int) -> Unit,
    onRunAiAnomalyCheck: () -> Unit,
    aiAnomalyResult: AnomalyResult?,
    isAiAnalyzing: Boolean,
    onSubmitInspection: (standardMassUsed: String, remarks: String, observations: String, isPass: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (request == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active verification request selected.", color = Slate600)
        }
        return
    }

    var standardMassUsed by remember { mutableStateOf("Class M1 Stainless Steel Test Weight Set #TW-09") }
    var inspectorRemarks by remember { mutableStateOf("All load points within legal metrological tolerance. Sealing wire affixed.") }
    var observations by remember { mutableStateOf("Zero-point balance perfect, eccentric loading error within class limits.") }

    val allPass = readings.isNotEmpty() && readings.all { it.isPass }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Target Instrument Details Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = SlateNavySurface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INSPECTION WORKSPACE",
                            color = CyanLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = request.requestId,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = request.instrumentName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        text = "${request.instrumentType} • ${request.businessName}",
                        color = Slate300,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Site: ${request.location}",
                        color = Slate400,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // Standard Mass / Calibration Device Used
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                shadowElevation = 0.5.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CALIBRATION STANDARD USED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = Slate500,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = standardMassUsed,
                        onValueChange = { standardMassUsed = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("e.g. Class M1 / F1 Standard Mass Set #TW-09", fontSize = 12.sp) }
                    )
                }
            }
        }

        // Test Readings Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TEST MEASUREMENT READINGS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Slate500,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Real-time error & statutory tolerance evaluation",
                        fontSize = 11.5.sp,
                        color = Slate500
                    )
                }

                Button(
                    onClick = onAddReading,
                    colors = ButtonDefaults.buttonColors(containerColor = SlateNavySurface),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CyanLight, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Load Point", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Readings List
        itemsIndexed(readings) { index, reading ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                shadowElevation = 0.5.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Row top: Index + Pass/Fail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Test Point #${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = DarkNavyObsidian
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (reading.isPass) StatusVerifiedGreenBg else StatusFailedRedBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (reading.isPass) StatusVerifiedGreenBorder else StatusFailedRedBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (reading.isPass) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (reading.isPass) StatusVerifiedGreen else StatusFailedRed,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (reading.isPass) "PASS" else "FAIL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        color = if (reading.isPass) StatusVerifiedGreenText else StatusFailedRedText
                                    )
                                }
                            }

                            if (readings.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { onRemoveReading(index) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = StatusFailedRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Inputs Row (Ref vs Actual vs Tol)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = reading.referenceValue,
                            onValueChange = { onUpdateReading(index, it, reading.actualReading, reading.tolerancePercentage) },
                            label = { Text("Standard Ref (kg)", fontSize = 10.5.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = reading.actualReading,
                            onValueChange = { onUpdateReading(index, reading.referenceValue, it, reading.tolerancePercentage) },
                            label = { Text("Actual Read (kg)", fontSize = 10.5.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = reading.tolerancePercentage,
                            onValueChange = { onUpdateReading(index, reading.referenceValue, reading.actualReading, it) },
                            label = { Text("Tol (%)", fontSize = 10.5.sp) },
                            modifier = Modifier.weight(0.7f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live Calculated Output Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(10.dp))
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Error Δ: ${String.format(Locale.US, "%+.4f", reading.error)} kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkNavyObsidian,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Dev: ${String.format(Locale.US, "%+.2f", reading.percentageError)}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (reading.isPass) StatusVerifiedGreenText else StatusFailedRedText
                        )
                        Text(
                            text = "MPE: ±${String.format(Locale.US, "%.4f", reading.maxPermissibleError)} kg",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
            }
        }

        // AI Anomaly Diagnostics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini Metrology AI Diagnostics",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }

                        Button(
                            onClick = onRunAiAnomalyCheck,
                            enabled = !isAiAnalyzing,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            if (isAiAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Analyze Readings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (aiAnomalyResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (aiAnomalyResult.hasAnomaly) StatusFailedRedBg else StatusVerifiedGreenBg
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (aiAnomalyResult.hasAnomaly) "ANOMALY FLAGGED" else "NO ANOMALY - LINEAR & COMPLIANT",
                                    color = if (aiAnomalyResult.hasAnomaly) StatusFailedRedText else StatusVerifiedGreenText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = aiAnomalyResult.summary,
                                    color = Slate900,
                                    fontSize = 11.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Recommendation: ${aiAnomalyResult.recommendation}",
                                    color = Slate700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inspector Remarks & Observations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Inspector Remarks & Compliance Notes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inspectorRemarks,
                        onValueChange = { inspectorRemarks = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = observations,
                        onValueChange = { observations = it },
                        label = { Text("Physical Condition & Observations") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        }

        // Action Decision Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onSubmitInspection(standardMassUsed, inspectorRemarks, observations, false)
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusFailedRed),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusFailedRed)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fail Inspection", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onSubmitInspection(standardMassUsed, inspectorRemarks, observations, true)
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusVerifiedGreen)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Approve & Issue Cert", fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
