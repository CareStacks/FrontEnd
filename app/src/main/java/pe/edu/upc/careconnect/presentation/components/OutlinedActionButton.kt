package pe.edu.upc.careconnect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.Disabled
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.TextMuted

@Composable
fun OutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) GreenDark else Disabled
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = GreenDark,
            disabledContentColor = TextMuted
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun OutlinedActionButtonPreview() {
    CareConnectTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedActionButton(
                text = "Cancelar",
                onClick = { }
            )
        }
    }
}