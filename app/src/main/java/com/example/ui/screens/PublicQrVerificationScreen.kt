package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.ui.components.ExpiryStatusBadge
import com.example.ui.components.MetrologyQrCode
import com.example.ui.theme.*

@Composable
fun PublicQrVerificationScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResult: CertificateEntity?,
    hasSearched: Boolean,
    onViewFullCertificate: (CertificateEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var manualInput by remember { mutableStateOf(query) }
    var isSimulatingCamera by remember { mutableStateOf(true) }

    // Scanner beam animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Slate50)
    ) {
        // Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy900)
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Public Certificate Verification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Verify legal metrology seals and calibration authenticity instantly.",
                    fontSize = 12.sp,
                    color = Slate300,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Scanner Viewfinder Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan QR Code on Instrument",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Navy900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Camera Scanner Frame
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .background(Navy900, RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MetrologyQrCode(
                            data = "CERT-2026-NLM-0841",
                            size = 150.dp,
                            showEmblem = false
                        )

                        // Animated Laser Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = ((laserOffset - 0.5f) * 140).dp)
                                .background(Color(0xFF38BDF8))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Demo Scan Buttons
                    Text(
                        text = "Quick Test Scans (Judges & Consumers)",
                        fontSize = 11.5.sp,
                        color = Slate600,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                manualInput = "CERT-2026-NLM-0841"
                                onSearch("CERT-2026-NLM-0841")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusVerifiedGreen),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text("Valid Cert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                manualInput = "CERT-2025-NLM-0219"
                                onSearch("CERT-2025-NLM-0219")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusPendingOrange),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text("Expiring", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                manualInput = "CERT-2024-NLM-0782"
                                onSearch("CERT-2024-NLM-0782")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusFailedRed),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text("Expired", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manual Certificate Number Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Or Enter Certificate Number Manually",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Navy900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualInput,
                            onValueChange = { manualInput = it },
                            placeholder = { Text("e.g. CERT-2026-NLM-0841") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = { onSearch(manualInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Verify")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verification Result Display Card
            if (hasSearched) {
                if (searchResult != null) {
                    val isExpired = searchResult.status.equals("EXPIRED", ignoreCase = true)
                    val isExpiringSoon = searchResult.validUntil.startsWith("2026-09") || searchResult.validUntil.startsWith("2026-08")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpired) StatusFailedRedBg else if (isExpiringSoon) StatusExpiringSoonBg else StatusVerifiedGreenBg
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isExpired) StatusFailedRed else if (isExpiringSoon) StatusExpiringSoon else StatusVerifiedGreen
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Status Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isExpired) Icons.Default.Cancel else Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = if (isExpired) StatusFailedRed else if (isExpiringSoon) StatusExpiringSoonText else StatusVerifiedGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isExpired) "VERIFICATION EXPIRED" else if (isExpiringSoon) "VERIFIED (EXPIRING SOON)" else "OFFICIALLY VERIFIED",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = if (isExpired) StatusFailedRedText else if (isExpiringSoon) StatusExpiringSoonText else StatusVerifiedGreenText
                                        )
                                        Text(
                                            text = searchResult.certificateNumber,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                    }
                                }

                                ExpiryStatusBadge(validUntilDate = searchResult.validUntil)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Safe Public Details
                            Text("Instrument: ${searchResult.instrumentName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                            Text("Type: ${searchResult.instrumentType} (${searchResult.capacity} ${searchResult.unit})", fontSize = 12.sp, color = Slate700)
                            Text("Serial Number: ${searchResult.serialNumber}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                            Text("Business: ${searchResult.ownerBusiness}", fontSize = 12.sp, color = Slate700)
                            Text("Operating Location: ${searchResult.location}", fontSize = 12.sp, color = Slate700)
                            Text("Standard: ${searchResult.standardCode}", fontSize = 12.sp, color = Slate700)
                            Text("Verified Date: ${searchResult.verificationDate}", fontSize = 12.sp, color = Slate700)
                            Text("Valid Until: ${searchResult.validUntil}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                            Text("Inspected By: ${searchResult.inspectorName}", fontSize = 12.sp, color = Slate700)

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { onViewFullCertificate(searchResult) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Official Certificate Document")
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusFailedRedBg),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusFailedRed)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusFailedRed, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Certificate Not Found", fontWeight = FontWeight.Bold, color = StatusFailedRedText, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No official legal metrology certificate matches \"$query\". The instrument may not be registered or verified.",
                                color = Slate700,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
