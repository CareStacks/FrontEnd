package pe.edu.upc.careconnect.presentation.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.local.CachedNotificationEntity
import pe.edu.upc.careconnect.data.repository.CareCacheRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.BackgroundSoft
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.OrangeDark
import pe.edu.upc.careconnect.presentation.theme.OrangeLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryDark
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.RedDark
import pe.edu.upc.careconnect.presentation.theme.RedLight
import pe.edu.upc.careconnect.presentation.theme.StatusReadBackground
import pe.edu.upc.careconnect.presentation.theme.StatusReadText
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { CareCacheRepository.getInstance(context) }
    val notifications by repository.notifications.collectAsState(initial = emptyList())

    LaunchedEffect(repository) {
        repository.seedIfEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CareScreenHeader(
            title = "Notificaciones",
            navigationIcon = R.drawable.ic_arrow_back,
            navigationContentDescription = "Volver",
            onNavigationClick = onBackClick,
            actionIcon = R.drawable.ic_notification,
            actionContentDescription = "Notificaciones"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NotificationSummaryCard()

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                notifications.forEach { notification ->
                    NotificationCard(notification = notification)
                }
            }

            Text(
                text = "Has llegado al final de tus notificaciones.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NotificationSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(25.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    icon = R.drawable.ic_alert,
                    contentDescription = null,
                    tint = Primary,
                    size = 24.dp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Estado de hoy",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tienes 2 alertas pendientes de revisión.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: CachedNotificationEntity) {
    val iconTone = notificationIconTone(notification.iconTone)
    val statusTone = notificationStatusTone(notification.statusTone)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(iconTone.background, RoundedCornerShape(999.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            icon = iconForNotification(notification.iconTone),
                            contentDescription = null,
                            tint = iconTone.content,
                            size = 20.dp
                        )
                    }

                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (notification.status.isNotBlank()) {
                    NotificationStatusBadge(
                        text = notification.status,
                        background = statusTone.background,
                        content = statusTone.content
                    )
                }
            }

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        icon = R.drawable.ic_clock,
                        contentDescription = null,
                        tint = TextSecondary,
                        size = 16.dp
                    )
                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.actionLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    AppIcon(
                        icon = if (notification.actionLabel == "Descargar") {
                            R.drawable.ic_download
                        } else {
                            R.drawable.ic_chevron_right
                        },
                        contentDescription = null,
                        tint = Primary,
                        size = 16.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationStatusBadge(
    text: String,
    background: Color,
    content: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class NotificationTone(
    val background: Color,
    val content: Color
)

@Composable
private fun notificationIconTone(tone: String): NotificationTone {
    return when (tone) {
        "error" -> NotificationTone(RedLight, RedDark)
        "green" -> NotificationTone(GreenLight, GreenDark)
        "purple" -> NotificationTone(PrimaryLight, PrimaryDark)
        else -> NotificationTone(Primary, Surface)
    }
}

@Composable
private fun notificationStatusTone(tone: String): NotificationTone {
    return when (tone) {
        "warning" -> NotificationTone(OrangeDark, OrangeLight)
        "error" -> NotificationTone(RedDark, Surface)
        "read" -> NotificationTone(StatusReadBackground, StatusReadText)
        else -> NotificationTone(BackgroundSoft, TextSecondary)
    }
}

private fun iconForNotification(tone: String): Int {
    return when (tone) {
        "error" -> R.drawable.ic_alert
        "green" -> R.drawable.ic_file
        "purple" -> R.drawable.ic_note
        else -> R.drawable.ic_medical
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotificationsScreenPreview() {
    CareConnectTheme {
        NotificationsScreen(onBackClick = { })
    }
}
