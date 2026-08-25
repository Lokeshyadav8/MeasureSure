package com.example.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CertificateEntity
import com.example.ui.theme.*

@Composable
fun CertificateViewDialog(
    certificate: CertificateEntity,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit = {}
) {
    val isExpired = certificate.status.equals("EXPIRED", ignoreCase = true)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Navy900)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Legal Metrology Digital Certificate",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Official Certificate Body (Guilloche Border Style)
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFEFDFB), Color(0xFFFAF8F3))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(3.dp, Color(0xFFD4AF37), RoundedCornerShape(12.dp)) // Gold Border
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // National Seal & Directorate Header
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Navy900, CircleShape)
                                .border(2.dp, Color(0xFFD4AF37), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Authority Seal",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "DIRECTORATE OF LEGAL METROLOGY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Navy900
                        )

                        Text(
                            text = "CENTRAL WEIGHTS & MEASURES VERIFICATION AUTHORITY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate600,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "CERTIFICATE OF VERIFICATION",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Navy900,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text = "Issued under the Statutory Metrology & Measurement Standards Act",
                            fontSize = 10.5.sp,
                            color = Slate600,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Certificate Number Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy900
                        ) {
                            Text(
                                text = "No: ${certificate.certificateNumber}",
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Certificate Details Table
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            CertRow(label = "Instrument ID", value = certificate.instrumentId, isMono = true)
                            CertRow(label = "Instrument Type", value = certificate.instrumentType)
                            CertRow(label = "Manufacturer / Model", value = "${certificate.manufacturer} / ${certificate.modelNumber}")
                            CertRow(label = "Serial Number", value = certificate.serialNumber, isMono = true)
                            CertRow(label = "Capacity / Unit", value = "${certificate.capacity} (${certificate.unit})")
                            CertRow(label = "Registered Business", value = certificate.ownerBusiness)
                            CertRow(label = "Verification Site", value = certificate.location)
                            CertRow(label = "Verification Standard", value = certificate.standardCode)
                            CertRow(label = "Date of Verification", value = certificate.verificationDate)
                            CertRow(label = "Valid Until", value = certificate.validUntil, isHighlight = true)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verification Status & QR Code Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isExpired) StatusFailedRedBg else StatusVerifiedGreenBg, RoundedCornerShape(10.dp))
                                .border(1.dp, if (isExpired) StatusFailedRed else StatusVerifiedGreen, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isExpired) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isExpired) StatusFailedRed else StatusVerifiedGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isExpired) "STATUS: EXPIRED" else "STATUS: VERIFIED & COMPLIANT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isExpired) StatusFailedRedText else StatusVerifiedGreenText
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Inspected & certified by ${certificate.inspectorName}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                                Text(
                                    text = "Official holographic tamper seal applied.",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                            }

                            // QR Code
                            MetrologyQrCode(
                                data = "https://metrology.gov.verify/cert/${certificate.certificateNumber}",
                                size = 80.dp,
                                showEmblem = true
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Signatures footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = certificate.inspectorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Navy900
                                )
                                Text(
                                    text = "Authorized Inspector",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "ELECTRONIC SEAL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFD4AF37)
                                )
                                Text(
                                    text = "Secured via Hash-256",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = {
                            onDownloadPdf()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download Certificate")
                    }
                }
            }
        }
    }
}

@Composable
private fun CertRow(
    label: String,
    value: String,
    isMono: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Slate600
        )
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) StatusVerifiedGreenText else Slate900,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default
        )
    }
}
