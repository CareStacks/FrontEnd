package pe.edu.upc.careconnect.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.presentation.theme.BackgroundSoft
import pe.edu.upc.careconnect.presentation.theme.Primary

@Composable
fun CareScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes navigationIcon: Int? = R.drawable.ic_menu,
    navigationContentDescription: String = "Abrir menú",
    onNavigationClick: (() -> Unit)? = null,
    @DrawableRes actionIcon: Int? = R.drawable.ic_notification,
    actionContentDescription: String = "Abrir notificaciones",
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundSoft)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationIcon != null) {
            IconButton(onClick = { onNavigationClick?.invoke() }) {
                AppIcon(
                    icon = navigationIcon,
                    contentDescription = navigationContentDescription,
                    tint = Primary
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        if (actionIcon != null) {
            IconButton(onClick = { onActionClick?.invoke() }) {
                AppIcon(
                    icon = actionIcon,
                    contentDescription = actionContentDescription,
                    tint = Primary
                )
            }
        }
    }
}
