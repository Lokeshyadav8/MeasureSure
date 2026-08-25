package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.InstrumentStatus
import com.example.data.model.UserRole
import com.example.data.model.VerificationRequestEntity
import com.example.ui.components.InstrumentStatusBadge
import com.example.ui.components.VerificationWorkflowTimeline
import com.example.ui.theme.*

@Composable
fun VerificationRequestsScreen(
    requests: List<VerificationRequestEntity>,
    userRole: UserRole,
    onOpenInspectionWorkspace: (VerificationRequestEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Verification Requests & Workflow",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = "Track active requests from initial submission to digital certificate issuance.",
                    fontSize = 12.sp,
                    color = Slate600
                )
            }
        }

        // Requests List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(requests) { request ->
                RequestCard(
                    request = request,
                    userRole = userRole,
                    onOpenInspection = { onOpenInspectionWorkspace(request) }
                )
            }
        }
    }
}

@Composable
fun RequestCard(
    request: VerificationRequestEntity,
    userRole: UserRole,
    onOpenInspection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Request ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.requestId,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = CyanPrimary
                )

                InstrumentStatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${request.instrumentName} (${request.instrumentType})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = DarkNavyObsidian
            )

            Text(
                text = "Instrument ID: ${request.instrumentId} • ${request.businessName}",
                fontSize = 12.sp,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stepped Progress Timeline
            VerificationWorkflowTimeline(currentStatus = request.status)

            Spacer(modifier = Modifier.height(12.dp))

            // Inspector & Location Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Inspector: ${request.assignedInspectorName.ifBlank { "Duty Officer" }}",
                        fontSize = 11.sp,
                        color = Slate600,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = request.scheduledDate.ifBlank { "Pending" },
                        fontSize = 11.sp,
                        color = Slate600
                    )
                }
            }

            // Action for Inspector or Admin
            if (userRole == UserRole.INSPECTOR || userRole == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenInspection,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateNavySurface)
                ) {
                    Icon(Icons.Default.Biotech, contentDescription = null, tint = CyanLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (request.status == InstrumentStatus.CERTIFICATE_GENERATED) "Review Inspection Data" else "Open Inspection Workspace",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
