package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstrumentStatus
import com.example.ui.theme.*

data class StepInfo(val stepNumber: Int, val title: String, val status: InstrumentStatus)

@Composable
fun VerificationWorkflowTimeline(
    currentStatus: InstrumentStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        StepInfo(1, "Draft", InstrumentStatus.DRAFT),
        StepInfo(2, "Submitted", InstrumentStatus.SUBMITTED),
        StepInfo(3, "Assigned", InstrumentStatus.ASSIGNED),
        StepInfo(4, "Scheduled", InstrumentStatus.INSPECTION_SCHEDULED),
        StepInfo(5, "Inspection", InstrumentStatus.UNDER_INSPECTION),
        StepInfo(6, "Result", if (currentStatus == InstrumentStatus.FAILED) InstrumentStatus.FAILED else InstrumentStatus.PASSED),
        StepInfo(7, "Certificate", InstrumentStatus.CERTIFICATE_GENERATED)
    )

    val currentStepIndex = when (currentStatus) {
        InstrumentStatus.DRAFT -> 0
        InstrumentStatus.SUBMITTED -> 1
        InstrumentStatus.ASSIGNED -> 2
        InstrumentStatus.INSPECTION_SCHEDULED -> 3
        InstrumentStatus.UNDER_INSPECTION -> 4
        InstrumentStatus.PASSED -> 5
        InstrumentStatus.FAILED -> 5
        InstrumentStatus.CERTIFICATE_GENERATED -> 6
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Slate100,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Verification Lifecycle Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Slate700
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, step ->
                    val isCompleted = index < currentStepIndex || (index == currentStepIndex && currentStatus == InstrumentStatus.CERTIFICATE_GENERATED)
                    val isCurrent = index == currentStepIndex && currentStatus != InstrumentStatus.CERTIFICATE_GENERATED
                    val isFailed = currentStatus == InstrumentStatus.FAILED && index == 5

                    val circleBg by animateColorAsState(
                        when {
                            isFailed -> StatusFailedRed
                            isCompleted -> StatusVerifiedGreen
                            isCurrent -> CyanPrimary
                            else -> Slate300
                        },
                        label = "circleColor"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(circleBg, CircleShape)
                                .then(
                                    if (isCurrent) Modifier.border(2.dp, CyanLight, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isFailed) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Failed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isCurrent) Color.White else Slate600,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = step.title,
                            fontSize = 9.5.sp,
                            fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isFailed) StatusFailedRed else if (isCurrent) CyanPrimary else if (isCompleted) StatusVerifiedGreenText else Slate600,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }

                    if (index < steps.size - 1) {
                        val linePassed = index < currentStepIndex
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .weight(0.6f)
                                .offset(y = (-10).dp)
                                .background(if (linePassed) StatusVerifiedGreen else Slate300, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}
