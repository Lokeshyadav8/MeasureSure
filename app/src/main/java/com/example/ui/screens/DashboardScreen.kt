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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.data.model.InstrumentEntity
import com.example.data.model.InstrumentStatus
import com.example.data.model.VerificationRequestEntity
import com.example.ui.components.ExpiryStatusBadge
import com.example.ui.components.InstrumentStatusBadge
import com.example.ui.components.PassRateGaugeCard
import com.example.ui.components.SleekMetricPill
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    instruments: List<InstrumentEntity>,
    requests: List<VerificationRequestEntity>,
    certificates: List<CertificateEntity>,
    onNavigateToRegister: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToInstruments: () -> Unit,
    onViewCertificate: (CertificateEntity) -> Unit,
    onViewInstrument: (InstrumentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalInstruments = instruments.size
    val pendingRequests = requests.count { it.status == InstrumentStatus.SUBMITTED || it.status == InstrumentStatus.ASSIGNED || it.status == InstrumentStatus.INSPECTION_SCHEDULED || it.status == InstrumentStatus.UNDER_INSPECTION }
    val verifiedCount = instruments.count { it.status == InstrumentStatus.CERTIFICATE_GENERATED || it.status == InstrumentStatus.PASSED }
    val failedCount = instruments.count { it.status == InstrumentStatus.FAILED }
    val expiringSoonCount = certificates.count { it.validUntil.startsWith("2026-09") || it.validUntil.startsWith("2026-08") }
    val expiredCount = certificates.count { it.status == "EXPIRED" || it.validUntil.contains("Failed") }

    val passRate = if (verifiedCount + failedCount > 0) {
        ((verifiedCount.toDouble() / (verifiedCount + failedCount)) * 100).toInt()
    } else 100

    val featuredInstrument = instruments.firstOrNull { it.status == InstrumentStatus.UNDER_INSPECTION || it.status == InstrumentStatus.SUBMITTED }
        ?: instruments.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateCanvas),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3-Pill Sleek Summary Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SleekMetricPill(
                    label = "Total",
                    value = "$totalInstruments",
                    bgColor = Slate100,
                    borderColor = Slate200,
                    textColor = DarkNavyObsidian,
                    modifier = Modifier.weight(1f)
                )

                SleekMetricPill(
                    label = "Verified",
                    value = "$verifiedCount",
                    bgColor = StatusVerifiedGreenBg,
                    borderColor = StatusVerifiedGreenBorder,
                    textColor = StatusVerifiedGreenText,
                    modifier = Modifier.weight(1f)
                )

                SleekMetricPill(
                    label = "Expiring",
                    value = "$expiringSoonCount",
                    bgColor = StatusPendingOrangeBg,
                    borderColor = StatusPendingOrangeBorder,
                    textColor = StatusPendingOrangeText,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title: Active Verification
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE VERIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )

                Surface(
                    color = CyanContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (featuredInstrument?.status == InstrumentStatus.UNDER_INSPECTION) "INSPECTION STAGE" else "ACTIVE REGISTRY",
                        color = StatusInfoBlueText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Featured Active Verification Card (Sleek Theme Spotlight)
        if (featuredInstrument != null) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewInstrument(featuredInstrument) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ID: ${featuredInstrument.instrumentId}",
                                    fontSize = 11.sp,
                                    color = Slate400,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = featuredInstrument.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateNavySurface
                                )
                                Text(
                                    text = featuredInstrument.location,
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Slate100, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Scale,
                                    contentDescription = null,
                                    tint = SlateNavySurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stepper Progress Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Slate100, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .fillMaxHeight()
                                    .background(CyanPrimary, RoundedCornerShape(2.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Reading comparison grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate50, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "STANDARD CAPACITY",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${featuredInstrument.capacity} ${featuredInstrument.unitOfMeasurement}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = DarkNavyObsidian
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TOLERANCE LIMIT",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "±${featuredInstrument.permissibleTolerancePercentage}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = StatusVerifiedGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Insight Pill
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = StatusInfoBlueBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBlueBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(CyanPrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Insight: Calibration stability index within ${featuredInstrument.permissibleTolerancePercentage}% OIML standard limit.",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusInfoBlueText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Dark primary button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToRegister() },
                        shape = RoundedCornerShape(22.dp),
                        color = SlateNavySurface,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "NEW SCALE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }

                    // Bordered white button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToRequests() },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = SlateNavySurface,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "REQUESTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateNavySurface,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }
            }
        }

        // Pass Rate Gauge Card
        item {
            PassRateGaugeCard(
                passRatePercentage = passRate,
                totalVerified = verifiedCount,
                totalFailed = failedCount
            )
        }

        // Category Breakdown Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "INSTRUMENTS BY SECTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val categories = listOf(
                        Triple("Industrial & Weighbridges", instruments.count { it.category == "Industrial" }, CyanPrimary),
                        Triple("Laboratory & Precision", instruments.count { it.category == "Laboratory" }, StatusVerifiedGreen),
                        Triple("Petroleum & Flow Meters", instruments.count { it.category == "Petroleum" }, StatusPendingOrange),
                        Triple("Retail & Commercial", instruments.count { it.category == "Retail" || it.category == "Commercial" }, StatusInfoBlue)
                    )

                    categories.forEach { (catName, count, catColor) ->
                        val ratio = if (totalInstruments > 0) count.toFloat() / totalInstruments else 0f
                        Column(modifier = Modifier.padding(vertical = 5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = catName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarkNavyObsidian)
                                Text(text = "$count units (${(ratio * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate600)
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = catColor,
                                trackColor = Slate100,
                            )
                        }
                    }
                }
            }
        }

        // Recent Certificates Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CERTIFICATES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "${certificates.size} issued",
                    fontSize = 11.5.sp,
                    color = Slate400,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(certificates) { cert ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewCertificate(cert) },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cert.certificateNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyanPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${cert.instrumentName} (${cert.instrumentType})",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyObsidian
                        )
                        Text(
                            text = "Valid Until: ${cert.validUntil} • ${cert.inspectorName}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        ExpiryStatusBadge(validUntilDate = cert.validUntil)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "View", fontSize = 11.sp, color = CyanPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

