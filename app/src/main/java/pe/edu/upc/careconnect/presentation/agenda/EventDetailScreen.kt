package pe.edu.upc.careconnect.presentation.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upc.careconnect.data.remote.HealthEventDto
import pe.edu.upc.careconnect.data.remote.toUserMessage
import pe.edu.upc.careconnect.data.repository.AgendaRepository
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventDetailScreen(
    eventId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEventUpdated: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember(context) { AgendaRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    var event by remember(eventId) { mutableStateOf<HealthEventDto?>(null) }
    var errorMessage by remember(eventId) { mutableStateOf<String?>(null) }
    var isLoading by remember(eventId) { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }

    fun loadEvent() {
        scope.launch {
            isLoading = true
            errorMessage = null
            runCatching {
                repository.getAgendaEventById(eventId)
            }.onSuccess { loadedEvent ->
                event = loadedEvent
            }.onFailure { throwable ->
                errorMessage = throwable.toUserMessage("No se pudo cargar el detalle del evento")
            }
            isLoading = false
        }
    }

    LaunchedEffect(eventId) {
        loadEvent()
    }

    if (showRescheduleDialog && event != null) {
        RescheduleEventDialog(
            event = event!!,
            onDismiss = { showRescheduleDialog = false },
            onConfirm = { startAt, endAt ->
                scope.launch {
                    isSubmitting = true
                    runCatching {
                        repository.rescheduleAgendaEvent(eventId, startAt, endAt)
                    }.onSuccess { updatedEvent ->
                        event = updatedEvent
                        errorMessage = null
                        showRescheduleDialog = false
                        onEventUpdated()
                    }.onFailure { throwable ->
                        errorMessage = throwable.toUserMessage("No se pudo reprogramar el evento")
                    }
                    isSubmitting = false
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {
        CareScreenHeader(
            title = "Detalle del evento",
            navigationIcon = pe.edu.upc.careconnect.R.drawable.ic_arrow_back,
            navigationContentDescription = "Volver",
            onNavigationClick = onBackClick,
            actionIcon = pe.edu.upc.careconnect.R.drawable.ic_notifications,
            actionContentDescription = "Notificaciones",
            onActionClick = onNotificationsClick
        )

        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando detalle del evento...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            event == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage ?: "No se encontró el evento solicitado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = ::loadEvent) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                val currentEvent = event!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        EventSummaryCard(currentEvent)
                    }

                    errorMessage?.let { message ->
                        item {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    item {
                        EventInfoCard(
                            label = "Fecha",
                            value = AgendaRepository.formatEventDate(currentEvent.startAt)
                        )
                    }

                    item {
                        EventInfoCard(
                            label = "Horario",
                            value = AgendaRepository.formatEventTimeRange(currentEvent.startAt, currentEvent.endAt)
                        )
                    }

                    item {
                        EventInfoCard(
                            label = "Estado",
                            value = currentEvent.status.toAgendaStatusLabel()
                        )
                    }

                    item {
                        EventInfoCard(
                            label = "Recordatorio",
                            value = currentEvent.reminderAt?.let {
                                AgendaRepository.formatEventDate(it) + " · " + AgendaRepository.formatEventTimeRange(it, null)
                            } ?: "El backend no devolvió recordatorio para este evento."
                        )
                    }

                    item {
                        EventInfoCard(
                            label = "Descripción",
                            value = currentEvent.description.orEmpty().ifBlank {
                                "El backend no devolvió una descripción para este evento."
                            }
                        )
                    }

                    item {
                        EventActions(
                            event = currentEvent,
                            isSubmitting = isSubmitting,
                            onConfirmClick = {
                                scope.launch {
                                    isSubmitting = true
                                    runCatching {
                                        repository.confirmAgendaEvent(eventId)
                                    }.onSuccess { updatedEvent ->
                                        event = updatedEvent
                                        errorMessage = null
                                        onEventUpdated()
                                    }.onFailure { throwable ->
                                        errorMessage = throwable.toUserMessage("No se pudo confirmar el evento")
                                    }
                                    isSubmitting = false
                                }
                            },
                            onRescheduleClick = {
                                showRescheduleDialog = true
                            },
                            onCancelClick = {
                                scope.launch {
                                    isSubmitting = true
                                    runCatching {
                                        repository.cancelAgendaEvent(eventId)
                                    }.onSuccess { updatedEvent ->
                                        event = updatedEvent
                                        errorMessage = null
                                        onEventUpdated()
                                    }.onFailure { throwable ->
                                        errorMessage = throwable.toUserMessage("No se pudo cancelar el evento")
                                    }
                                    isSubmitting = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventSummaryCard(event: HealthEventDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = event.type.toDisplayType(),
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    event.caregiverId.isNullOrBlank() -> "Evento creado sin caregiver asociado."
                    else -> "Evento asociado a un caregiver en backend."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun EventInfoCard(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun EventActions(
    event: HealthEventDto,
    isSubmitting: Boolean,
    onConfirmClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val canConfirm = event.status == "PENDING"
    val canModifySchedule = event.status != "CANCELLED" && event.status != "MISSED"
    val canCancel = event.status != "CANCELLED"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (canConfirm) {
            Button(
                onClick = onConfirmClick,
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = PrimaryLight
                )
            ) {
                Text(if (isSubmitting) "Procesando..." else "Confirmar evento")
            }
        }

        OutlinedButton(
            onClick = onRescheduleClick,
            enabled = canModifySchedule && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Primary)
        ) {
            Text("Reprogramar")
        }

        OutlinedButton(
            onClick = onCancelClick,
            enabled = canCancel && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cancelar evento")
        }

        if (!canModifySchedule) {
            Text(
                text = "El backend no permite reprogramar eventos cancelados o marcados como incumplidos.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun RescheduleEventDialog(
    event: HealthEventDto,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime, LocalDateTime) -> Unit
) {
    var date by remember(event.id) { mutableStateOf(event.startAt.toEditableDate()) }
    var time by remember(event.id) { mutableStateOf(event.startAt.toEditableTime()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reprogramar evento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Usá fecha `mm/dd/yyyy` y hora `hh:mm AM/PM`. El backend solo permite actualizar horario, no ubicación ni profesional porque esos campos no existen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        validationError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha") },
                    placeholder = { Text("mm/dd/yyyy") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border
                    )
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = {
                        time = it
                        validationError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Hora") },
                    placeholder = { Text("hh:mm AM/PM") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border
                    )
                )
                validationError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startAt = parseEventDateTime(date, time)
                    if (startAt == null) {
                        validationError = "Ingresá una fecha y hora válidas."
                    } else {
                        onConfirm(startAt, startAt.plusHours(1))
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

private fun String.toAgendaStatusLabel(): String {
    return when (this) {
        "CONFIRMED" -> "Confirmado"
        "PENDING" -> "Pendiente"
        "MISSED" -> "Incumplido"
        "CANCELLED" -> "Cancelado"
        else -> this
    }
}

private fun String.toDisplayType(): String {
    return when (this) {
        "APPOINTMENT" -> "Cita médica"
        "MEDICATION" -> "Medicación"
        "THERAPY" -> "Terapia"
        "CARE_ACTIVITY" -> "Actividad de cuidado"
        else -> this
    }
}

private fun String?.toEditableDate(): String {
    if (this.isNullOrBlank()) return ""
    return runCatching {
        LocalDateTime.parse(this).format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US))
    }.getOrDefault("")
}

private fun String?.toEditableTime(): String {
    if (this.isNullOrBlank()) return ""
    return runCatching {
        LocalDateTime.parse(this).format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
    }.getOrDefault("")
}

private fun parseEventDateTime(date: String, time: String): LocalDateTime? {
    val value = "${date.trim()} ${time.trim()}"
    val formatters = listOf(
        DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.US),
        DateTimeFormatter.ofPattern("M/d/yyyy hh:mm a", Locale.US)
    )

    return formatters.firstNotNullOfOrNull { formatter ->
        runCatching { LocalDateTime.parse(value, formatter) }.getOrNull()
    }
}
