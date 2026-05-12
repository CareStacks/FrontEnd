package pe.edu.upc.careconnect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Border
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AppCardPreview() {
    CareConnectTheme {
        AppCard(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Medicamento programado",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Text(
                text = "Hoy a las 8:00 a. m.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}