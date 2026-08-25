package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstrumentStatus
import com.example.data.model.RiskLevel
import com.example.ui.theme.*

private data class BadgeConfig(
    val bg: Color,
    val border: Color,
    val text: Color,
    val label: String,
    val icon: ImageVector? = null
)

@Composable
fun InstrumentStatusBadge(
    status: InstrumentStatus,
    modifier: Modifier = Modifier
) {
    val config = when (status) {
        InstrumentStatus.DRAFT -> BadgeConfig(
            Slate100, Slate200, Slate700, "Draft", Icons.Default.EditNote
        )
        InstrumentStatus.SUBMITTED -> BadgeConfig(
            StatusInfoBlueBg, StatusInfoBlueBorder, StatusInfoBlueText, "Submitted", Icons.Default.Schedule
        )
        InstrumentStatus.ASSIGNED -> BadgeConfig(
            StatusInfoBlueBg, StatusInfoBlueBorder, StatusInfoBlueText, "Inspector Assigned", Icons.Default.Person
        )
        InstrumentStatus.INSPECTION_SCHEDULED -> BadgeConfig(
            StatusPendingOrangeBg, StatusPendingOrangeBorder, StatusPendingOrangeText, "Inspection Scheduled", Icons.Default.Event
        )
        InstrumentStatus.UNDER_INSPECTION -> BadgeConfig(
            StatusPendingOrangeBg, StatusPendingOrangeBorder, StatusPendingOrangeText, "Under Inspection", Icons.Default.Biotech
        )
        InstrumentStatus.PASSED -> BadgeConfig(
            StatusVerifiedGreenBg, StatusVerifiedGreenBorder, StatusVerifiedGreenText, "Passed Verification", Icons.Default.CheckCircle
        )
        InstrumentStatus.FAILED -> BadgeConfig(
            StatusFailedRedBg, StatusFailedRedBorder, StatusFailedRedText, "Verification Failed", Icons.Default.Cancel
        )
        InstrumentStatus.CERTIFICATE_GENERATED -> BadgeConfig(
            StatusVerifiedGreenBg, StatusVerifiedGreenBorder, StatusVerifiedGreenText, "Certified & Verified", Icons.Default.Verified
        )
    }

    Box(
        modifier = modifier
            .background(config.bg, RoundedCornerShape(10.dp))
            .border(1.dp, config.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = config.text,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = config.label,
                color = config.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RiskScoreBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val config = when (riskLevel) {
        RiskLevel.LOW -> BadgeConfig(StatusVerifiedGreenBg, StatusVerifiedGreenBorder, StatusVerifiedGreenText, "LOW RISK")
        RiskLevel.MEDIUM -> BadgeConfig(StatusPendingOrangeBg, StatusPendingOrangeBorder, StatusPendingOrangeText, "MED RISK")
        RiskLevel.HIGH -> BadgeConfig(StatusFailedRedBg, StatusFailedRedBorder, StatusFailedRedText, "HIGH RISK")
    }

    Box(
        modifier = modifier
            .background(config.bg, RoundedCornerShape(8.dp))
            .border(1.dp, config.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(config.text, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = config.label,
                color = config.text,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ExpiryStatusBadge(
    validUntilDate: String,
    modifier: Modifier = Modifier
) {
    val isExpired = validUntilDate.contains("Failed", ignoreCase = true) || validUntilDate.startsWith("2024") || validUntilDate.startsWith("2025-06")
    val isExpiringSoon = validUntilDate.startsWith("2026-09") || validUntilDate.startsWith("2026-08")

    val config = when {
        isExpired -> BadgeConfig(StatusFailedRedBg, StatusFailedRedBorder, StatusFailedRedText, "Expired", Icons.Default.Warning)
        isExpiringSoon -> BadgeConfig(StatusExpiringSoonBg, StatusExpiringSoonBorder, StatusExpiringSoonText, "Expiring Soon", Icons.Default.HourglassBottom)
        else -> BadgeConfig(StatusVerifiedGreenBg, StatusVerifiedGreenBorder, StatusVerifiedGreenText, "Valid & Active", Icons.Default.CheckCircle)
    }

    Box(
        modifier = modifier
            .background(config.bg, RoundedCornerShape(8.dp))
            .border(1.dp, config.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            config.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = config.text,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = config.label,
                color = config.text,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
