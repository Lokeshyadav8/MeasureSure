package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToQrVerify: () -> Unit,
    onSelectRole: (UserRole) -> Unit,
    onStartDemoFlow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Slate50)
    ) {
        // Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Navy900, Navy800, Navy700)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pill badge
                Surface(
                    color = Navy600.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Statutory Legal Metrology Platform",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Verify Every Measurement.\nBuild Public Trust.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "A digital enterprise platform for registering, inspecting, verifying, and monitoring weighing and measuring instruments with ISO 17025 compliance.",
                    fontSize = 13.sp,
                    color = Slate300,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Graphic
                Image(
                    painter = painterResource(id = R.drawable.metrology_hero_banner_1787664793611),
                    contentDescription = "Metrology Instruments and Verification Seal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Register Instrument", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onNavigateToQrVerify,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8))
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Verify QR Code", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Quick Role Portal Selector
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Select Portal by Role",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RolePortalCard(
                    title = "Business Owner",
                    subtitle = "Manage & submit verifications",
                    icon = Icons.Default.Storefront,
                    color = CyanPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectRole(UserRole.BUSINESS_OWNER) }
                )

                RolePortalCard(
                    title = "Inspector",
                    subtitle = "Conduct tests & issue certs",
                    icon = Icons.Default.Biotech,
                    color = StatusPendingOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectRole(UserRole.INSPECTOR) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RolePortalCard(
                    title = "Admin Authority",
                    subtitle = "Analytics & audit oversight",
                    icon = Icons.Default.AdminPanelSettings,
                    color = Navy700,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectRole(UserRole.ADMIN) }
                )

                RolePortalCard(
                    title = "Public Scanner",
                    subtitle = "Verify certificates instantly",
                    icon = Icons.Default.QrCode2,
                    color = StatusVerifiedGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectRole(UserRole.PUBLIC) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // How It Works Section
            Text(
                text = "How the Digital Verification Works",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )

            Spacer(modifier = Modifier.height(12.dp))

            WorkflowStepRow(
                number = "1",
                title = "Business Registration",
                desc = "Register scales, weighbridges, fuel dispensers with serial numbers and photos."
            )

            WorkflowStepRow(
                number = "2",
                title = "Request & Schedule",
                desc = "Submit digital verification requests with automated duty inspector dispatch."
            )

            WorkflowStepRow(
                number = "3",
                title = "Calibration Inspection",
                desc = "Inspectors record standard mass vs actual readings with automatic error % calculation."
            )

            WorkflowStepRow(
                number = "4",
                title = "QR Digital Certificate",
                desc = "Tamper-proof digital certificates generated with instant public QR scanning."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // AI Features Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SlateNavySurface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = CyanLight, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "AI-Powered Metrology Diagnostics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Anomaly Detection: Identifies sensor drift, hysteresis and repeatability anomalies.\n• Smart OCR: Automatically extracts specs from instrument nameplates.\n• Predictive Risk Score: Assesses failure probability based on historical wear.",
                        color = Slate300,
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom CTA
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkNavyObsidian,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Make Measurement Verification Digital.",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Trusted by national metrology directorates, commercial enterprises, and consumers.",
                        color = Slate300,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onStartDemoFlow,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Interactive 5-Min Judge Demo", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun RolePortalCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = DarkNavyObsidian)
                Text(text = subtitle, fontSize = 10.5.sp, color = Slate500, maxLines = 1)
            }
        }
    }
}

@Composable
private fun WorkflowStepRow(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(CyanPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
            Text(text = desc, fontSize = 11.5.sp, color = Slate600)
        }
    }
}
