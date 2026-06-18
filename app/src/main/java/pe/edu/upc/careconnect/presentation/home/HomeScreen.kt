package pe.edu.upc.careconnect.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.remote.HealthEventDto
import pe.edu.upc.careconnect.data.remote.toUserMessage
import pe.edu.upc.careconnect.data.repository.AgendaRepository
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.Neutral
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.Secondary
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "Usuario",
    onRegisterEventClick: () -> Unit = {},
    onUploadDocumentClick: () -> Unit = {},
    onWriteNoteClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val agendaRepository = remember(context) { AgendaRepository.getInstance(context) }
    var events by remember { mutableStateOf<List<HealthEventDto>>(emptyList()) }
    var isLoadingEvents by remember { mutableStateOf(true) }
    var syncError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(agendaRepository) {
        isLoadingEvents = true
        runCatching {
            agendaRepository.getAgendaEvents()
        }.onSuccess { loadedEvents ->
            events = loadedEvents
            syncError = null
        }.onFailure { throwable ->
            syncError = throwable.toUserMessage("No se pudieron cargar tus eventos")
        }
        isLoadingEvents = false
    }

    val nextEvent = remember(events) {
        events.nextHomeEvent()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        HomeHeader(
            userName = userName,
            onNotificationsClick = onNotificationsClick
        )

        Spacer(modifier = Modifier.height(22.dp))

        syncError?.let { message ->
            HomeErrorText(message = message)

            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            isLoadingEvents -> LoadingEventsCard()
            nextEvent == null -> EmptyEventsCard(onCreateClick = onRegisterEventClick)
            else -> UpcomingEventCard(event = nextEvent)
        }

        Spacer(modifier = Modifier.height(22.dp))

        DailySummaryCard(events = events)

        Spacer(modifier = Modifier.height(22.dp))

        QuickActionsSection(
            onRegisterEventClick = onRegisterEventClick,
            onUploadDocumentClick = onUploadDocumentClick,
            onWriteNoteClick = onWriteNoteClick
        )
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onNotificationsClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menú",
                    tint = Primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "CareConnect",
                color = Primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Notificaciones",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Buenos días,",
            color = TextSecondary,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Hola, $userName",
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HomeErrorText(message: String) {
    Text(
        text = message,
        color = Color(0xFFB91C1C),
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

@Composable
private fun LoadingEventsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Cargando tus eventos...",
                color = Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estamos revisando tus recordatorios guardados.",
                color = TextSecondary,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun EmptyEventsCard(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Secondary.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Eventos",
                        tint = Color(0xFF4E6F61),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Aún no tienes eventos creados.",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Crea tu primer recordatorio para comenzar.",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Surface
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "Crear recordatorio",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UpcomingEventCard(event: HealthEventDto) {
    val statusColors = homeStatusColors(event.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = homeEventIcon(event.type)),
                        contentDescription = homeEventTypeLabel(event.type),
                        tint = Surface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = homeEventTypeLabel(event.type),
                            color = Primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        HomeStatusBadge(
                            text = homeStatusLabel(event.status),
                            backgroundColor = statusColors.background,
                            textColor = statusColors.content
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = AgendaRepository.formatEventTimeRange(event.startAt, event.endAt),
                        color = Primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = AgendaRepository.formatEventDate(event.startAt),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.title.ifBlank { "Evento sin título" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 21.sp
                    )

                    val description = event.description.orEmpty().trim()
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            color = TextSecondary,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeStatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DailySummaryCard(events: List<HealthEventDto>) {
    val pendingCount = events.count { it.status == "PENDING" }
    val confirmedCount = events.count { it.status == "CONFIRMED" }
    val missedCount = events.count { it.status == "MISSED" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 24.dp
            )
        ) {
            Text(
                text = "RESUMEN DE EVENTOS",
                color = Neutral,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryItem(
                    value = pendingCount.toString(),
                    label = "PENDIENTES",
                    valueColor = Primary,
                    modifier = Modifier.weight(1f)
                )

                SummaryDivider()

                SummaryItem(
                    value = confirmedCount.toString(),
                    label = "CONFIRMADOS",
                    valueColor = Color(0xFF4E6F61),
                    modifier = Modifier.weight(1f)
                )

                SummaryDivider()

                SummaryItem(
                    value = missedCount.toString(),
                    label = "INCUMPLIDOS",
                    valueColor = Color(0xFFB91C1C),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        modifier = Modifier
            .height(56.dp)
            .width(1.dp)
            .background(Border)
    )
}

private data class HomeStatusColors(
    val background: Color,
    val content: Color
)

private fun List<HealthEventDto>.nextHomeEvent(): HealthEventDto? {
    val now = LocalDateTime.now()
    val activeEvents = filterNot { event -> event.status == "CANCELLED" }

    return activeEvents
        .filter { event ->
            val start = event.startDateTimeOrNull()
            start == null || !start.isBefore(now)
        }
        .minByOrNull { event -> event.startAt.orEmpty() }
        ?: activeEvents.minByOrNull { event -> event.startAt.orEmpty() }
}

private fun HealthEventDto.startDateTimeOrNull(): LocalDateTime? {
    return startAt?.let { value ->
        runCatching { LocalDateTime.parse(value) }.getOrNull()
    }
}

private fun homeStatusColors(status: String): HomeStatusColors {
    return when (status) {
        "CONFIRMED" -> HomeStatusColors(
            background = Secondary.copy(alpha = 0.8f),
            content = Color(0xFF4E6F61)
        )
        "PENDING" -> HomeStatusColors(
            background = Primary.copy(alpha = 0.16f),
            content = Neutral
        )
        "MISSED" -> HomeStatusColors(
            background = Color(0xFFFFD2D2),
            content = Color(0xFFB91C1C)
        )
        else -> HomeStatusColors(
            background = Color(0xFFFFD8B8),
            content = Color(0xFFA85E00)
        )
    }
}

private fun homeStatusLabel(status: String): String {
    return when (status) {
        "CONFIRMED" -> "COMPLETADO"
        "PENDING" -> "PENDIENTE"
        "MISSED" -> "INCUMPLIDO"
        "CANCELLED" -> "CANCELADO"
        else -> status.ifBlank { "EVENTO" }
    }
}

private fun homeEventTypeLabel(type: String): String {
    return when (type) {
        "MEDICATION" -> "Medicación"
        "APPOINTMENT" -> "Cita médica"
        "THERAPY" -> "Terapia"
        "CARE_ACTIVITY" -> "Actividad de cuidado"
        else -> "Evento"
    }
}

@DrawableRes
private fun homeEventIcon(type: String): Int {
    return when (type) {
        "MEDICATION" -> R.drawable.ic_medical
        "APPOINTMENT" -> R.drawable.ic_calendar
        "THERAPY", "CARE_ACTIVITY" -> R.drawable.ic_note
        else -> R.drawable.ic_calendar
    }
}

@Composable
private fun QuickActionsSection(
    onRegisterEventClick: () -> Unit,
    onUploadDocumentClick: () -> Unit,
    onWriteNoteClick: () -> Unit
) {
    Column {
        Text(
            text = "ACCIONES RÁPIDAS",
            color = Neutral,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickActionItem(
            iconRes = R.drawable.ic_add_circle,
            title = "Registrar evento",
            onClick = onRegisterEventClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        QuickActionItem(
            iconRes = R.drawable.ic_upload_file,
            title = "Subir documento",
            onClick = onUploadDocumentClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        QuickActionItem(
            iconRes = R.drawable.ic_edit_note,
            title = "Escribir nota",
            onClick = onWriteNoteClick
        )
    }
}

@Composable
private fun QuickActionItem(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Secondary.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = Color(0xFF4E6F61),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Ir",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
