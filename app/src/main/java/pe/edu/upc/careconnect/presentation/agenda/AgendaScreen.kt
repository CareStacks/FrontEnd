package pe.edu.upc.careconnect.presentation.agenda

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
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
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier,
    onAddEventClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val agendaRepository = remember(context) { AgendaRepository.getInstance(context) }
    val today = remember { LocalDate.now() }
    var events by remember { mutableStateOf<List<HealthEventDto>>(emptyList()) }
    var syncError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(agendaRepository) {
        runCatching {
            agendaRepository.getAgendaEvents()
        }.onSuccess { loadedEvents ->
            events = loadedEvents
            syncError = null
        }.onFailure { throwable ->
            syncError = throwable.toUserMessage("No se pudo cargar la agenda")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        AgendaHeader(
            onNotificationsClick = onNotificationsClick
        )

        Spacer(modifier = Modifier.height(22.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                CalendarCard(currentDate = today)

                Spacer(modifier = Modifier.height(28.dp))

                EventsHeader(currentDate = today)

                Spacer(modifier = Modifier.height(18.dp))

                syncError?.let { message ->
                    Text(
                        text = message,
                        color = Color(0xFFB91C1C),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (events.isEmpty()) {
                    EmptyAgendaState(onCreateClick = onAddEventClick)
                } else {
                    events.forEachIndexed { index, event ->
                        val statusColors = agendaStatusColors(event.status)
                        EventCard(
                            time = agendaTime(event.startAt),
                            period = agendaPeriod(event.startAt),
                            title = event.title,
                            description = event.description.orEmpty().ifBlank { "Sin descripción" },
                            status = event.status.toAgendaStatusLabel(),
                            statusBackground = statusColors.background,
                            statusTextColor = statusColors.content,
                            accentColor = statusColors.accent,
                            onClick = { onEventClick(event.id) }
                        )

                        if (index != events.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddEventClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Surface
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(
                text = "+  Agregar evento",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class CalendarDay(
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

private data class AgendaStatusColors(
    val background: Color,
    val content: Color,
    val accent: Color = Primary
)

private fun agendaStatusColors(status: String): AgendaStatusColors {
    return when (status) {
        "CONFIRMED" -> AgendaStatusColors(
            background = Secondary.copy(alpha = 0.8f),
            content = Color(0xFF4E6F61),
            accent = Color(0xFF4E6F61)
        )
        "PENDING" -> AgendaStatusColors(
            background = Primary.copy(alpha = 0.16f),
            content = Neutral,
            accent = Primary
        )
        "MISSED" -> AgendaStatusColors(
            background = Color(0xFFB91C1C),
            content = Color.White,
            accent = Color(0xFFB91C1C)
        )
        else -> AgendaStatusColors(
            background = Color(0xFFB36A18),
            content = Color.White,
            accent = Color(0xFFB36A18)
        )
    }
}

private fun String.toAgendaStatusLabel(): String {
    return when (this) {
        "CONFIRMED" -> "COMPLETADO"
        "PENDING" -> "PENDIENTE"
        "MISSED" -> "INCUMPLIDO"
        "CANCELLED" -> "CANCELADO"
        else -> this
    }
}

private fun agendaTime(rawValue: String?): String {
    return AgendaRepository.formatEventTimeRange(rawValue, null)
        .substringBefore(' ')
        .ifBlank { "--:--" }
}

private fun agendaPeriod(rawValue: String?): String {
    return AgendaRepository.formatEventTimeRange(rawValue, null)
        .substringAfterLast(' ', "")
}

private fun LocalDate.monthTitle(): String {
    val monthName = month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { character ->
            if (character.isLowerCase()) character.titlecase(Locale.getDefault()) else character.toString()
        }
    return "$monthName $year"
}

private fun LocalDate.shortDateLabel(): String {
    val monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
        .replace(".", "")
        .uppercase(Locale.getDefault())
    return "$monthName $dayOfMonth"
}

private fun LocalDate.calendarWeeks(): List<List<CalendarDay>> {
    val currentMonth = YearMonth.from(this)
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysFromPreviousMonth = firstDayOfMonth.dayOfWeek.value - 1
    val firstVisibleDate = firstDayOfMonth.minusDays(daysFromPreviousMonth.toLong())
    val weeks = mutableListOf<List<CalendarDay>>()

    repeat(6) { weekIndex ->
        val week = List(7) { dayIndex ->
            val date = firstVisibleDate.plusDays((weekIndex * 7 + dayIndex).toLong())
            CalendarDay(
                dayOfMonth = date.dayOfMonth,
                isCurrentMonth = YearMonth.from(date) == currentMonth,
                isToday = date == this
            )
        }
        weeks.add(week)
    }

    return weeks.dropLastWhile { week -> week.none { day -> day.isCurrentMonth } }
}

@Composable
private fun AgendaHeader(
    onNotificationsClick: () -> Unit
) {
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
            text = "Agenda",
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
}

@Composable
private fun CalendarCard(currentDate: LocalDate) {
    val monthWeeks = remember(currentDate) { currentDate.calendarWeeks() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 22.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDate.monthTitle(),
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                CalendarArrowButton(
                    icon = R.drawable.ic_chevron_left,
                    contentDescription = "Mes anterior"
                )

                Spacer(modifier = Modifier.width(12.dp))

                CalendarArrowButton(
                    icon = R.drawable.ic_chevron_right,
                    contentDescription = "Mes siguiente"
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            WeekDaysRow()

            Spacer(modifier = Modifier.height(12.dp))

            monthWeeks.forEachIndexed { index, week ->
                CalendarDatesRow(days = week)

                if (index != monthWeeks.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarArrowButton(
    @DrawableRes icon: Int,
    contentDescription: String
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = TextPrimary,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun WeekDaysRow() {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        days.forEach { day ->
            Text(
                text = day,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarDatesRow(
    days: List<CalendarDay>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                contentAlignment = Alignment.Center
            ) {
                if (day.isToday) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            color = Surface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = day.dayOfMonth.toString(),
                        color = if (!day.isCurrentMonth) {
                            TextMuted.copy(alpha = 0.65f)
                        } else {
                            TextPrimary
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsHeader(currentDate: LocalDate) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Eventos creados",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = currentDate.shortDateLabel(),
            color = Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyAgendaState(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    contentDescription = "Agenda vacía",
                    tint = Color(0xFF4E6F61),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aún no tienes eventos creados.",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crea tu primer recordatorio para comenzar.",
                color = TextSecondary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Surface
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "Crear recordatorio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EventCard(
    time: String,
    period: String,
    title: String,
    description: String,
    status: String,
    statusBackground: Color,
    statusTextColor: Color,
    accentColor: Color? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                        .align(Alignment.CenterStart)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = time,
                        color = accentColor ?: Primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = period,
                        color = Neutral,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Border)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )

                        StatusBadge(
                            text = status,
                            backgroundColor = statusBackground,
                            textColor = statusTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        color = TextSecondary,
                        fontSize = 16.sp,
                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
