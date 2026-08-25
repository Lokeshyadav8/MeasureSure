package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun MetrologyHeaderBar(
    currentRole: UserRole,
    userName: String,
    businessOrOrg: String,
    onRoleSelected: (UserRole) -> Unit,
    onStartDemoFlow: () -> Unit,
    unreadNotificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            // Top branding row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GOVVERIFY SYSTEM",
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Verification Center",
                        color = DarkNavyObsidian,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        letterSpacing = (-0.3).sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Hackathon Demo Button
                    Button(
                        onClick = onStartDemoFlow,
                        colors = ButtonDefaults.buttonColors(containerColor = SlateNavySurface),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = CyanLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Demo",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Avatar with status dot
                    Box(modifier = Modifier.size(38.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SteelNavyMuted),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userName.length >= 2) userName.take(2).uppercase() else "GOV",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        // Notification / active dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.TopEnd)
                                .background(AccentAmberDot, CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User Info & Role Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        color = DarkNavyObsidian,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = businessOrOrg,
                        color = Slate500,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }

                // Sleek Role selector buttons
                Row(
                    modifier = Modifier
                        .background(Slate100, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    RoleChip(
                        label = "Business",
                        isSelected = currentRole == UserRole.BUSINESS_OWNER,
                        onClick = { onRoleSelected(UserRole.BUSINESS_OWNER) }
                    )
                    RoleChip(
                        label = "Inspector",
                        isSelected = currentRole == UserRole.INSPECTOR,
                        onClick = { onRoleSelected(UserRole.INSPECTOR) }
                    )
                    RoleChip(
                        label = "Admin",
                        isSelected = currentRole == UserRole.ADMIN,
                        onClick = { onRoleSelected(UserRole.ADMIN) }
                    )
                    RoleChip(
                        label = "Public",
                        isSelected = currentRole == UserRole.PUBLIC,
                        onClick = { onRoleSelected(UserRole.PUBLIC) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) Color.White else Color.Transparent,
                RoundedCornerShape(9.dp)
            )
            .then(
                if (isSelected) Modifier.border(1.dp, Slate200, RoundedCornerShape(9.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DarkNavyObsidian else Slate500
        )
    }
}

