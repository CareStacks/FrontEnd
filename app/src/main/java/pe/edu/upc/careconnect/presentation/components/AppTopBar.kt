package pe.edu.upc.careconnect.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.TextPrimary

@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(48.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (onBackClick != null) {
                    Text(
                        text = "‹",
                        color = Primary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onBackClick() }
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.width(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AppTopBarPreview() {
    CareConnectTheme {
        AppTopBar(
            title = "Registrar evento",
            onBackClick = { }
        )
    }
}