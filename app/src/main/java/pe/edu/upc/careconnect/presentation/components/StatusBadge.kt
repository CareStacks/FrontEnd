package pe.edu.upc.careconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.StatusConfirmedBackground
import pe.edu.upc.careconnect.presentation.theme.StatusConfirmedText
import pe.edu.upc.careconnect.presentation.theme.StatusMedicalBackground
import pe.edu.upc.careconnect.presentation.theme.StatusMedicalText
import pe.edu.upc.careconnect.presentation.theme.StatusMissedBackground
import pe.edu.upc.careconnect.presentation.theme.StatusMissedText
import pe.edu.upc.careconnect.presentation.theme.StatusPendingBackground
import pe.edu.upc.careconnect.presentation.theme.StatusPendingText
import pe.edu.upc.careconnect.presentation.theme.StatusReadBackground
import pe.edu.upc.careconnect.presentation.theme.StatusReadText
import pe.edu.upc.careconnect.presentation.theme.StatusUrgentBackground
import pe.edu.upc.careconnect.presentation.theme.StatusUrgentText

enum class StatusType {
    Pending,
    Confirmed,
    Missed,
    Read,
    Urgent,
    Medical
}

@Composable
fun StatusBadge(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val text: String
    val backgroundColor: Color
    val textColor: Color

    when (status) {
        StatusType.Pending -> {
            text = "PENDIENTE"
            backgroundColor = StatusPendingBackground
            textColor = StatusPendingText
        }

        StatusType.Confirmed -> {
            text = "CONFIRMADO"
            backgroundColor = StatusConfirmedBackground
            textColor = StatusConfirmedText
        }

        StatusType.Missed -> {
            text = "INCUMPLIDO"
            backgroundColor = StatusMissedBackground
            textColor = StatusMissedText
        }

        StatusType.Read -> {
            text = "LEÍDO"
            backgroundColor = StatusReadBackground
            textColor = StatusReadText
        }

        StatusType.Urgent -> {
            text = "URGENTE"
            backgroundColor = StatusUrgentBackground
            textColor = StatusUrgentText
        }

        StatusType.Medical -> {
            text = "MÉDICO"
            backgroundColor = StatusMedicalBackground
            textColor = StatusMedicalText
        }
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun StatusBadgePreview() {
    CareConnectTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge(status = StatusType.Pending)
            StatusBadge(status = StatusType.Confirmed)
        }
    }
}