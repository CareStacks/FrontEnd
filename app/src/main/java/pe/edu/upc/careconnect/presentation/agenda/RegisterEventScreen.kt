package pe.edu.upc.careconnect.presentation.agenda

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.edu.upc.careconnect.R
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
fun RegisterEventScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val agendaRepository = remember(context) { AgendaRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    var eventType by remember { mutableStateOf("Medicación") }
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 28.dp)
    ) {
        RegisterEventHeader(
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.img_register_event_header),
            contentDescription = "Registrar evento",
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(26.dp))

        FieldLabel(text = "TIPO DE EVENTO")

        Spacer(modifier = Modifier.height(10.dp))

        SelectEventTypeField(
            value = eventType,
            onValueChange = {
                eventType = it
            }
        )

        Spacer(modifier = Modifier.height(26.dp))

        FieldLabel(text = "NOMBRE DEL EVENTO")

        Spacer(modifier = Modifier.height(10.dp))

        AppInputField(
            value = eventName,
            onValueChange = {
                eventName = it
            },
            placeholder = "Ej. Revisión cardiológica",
            singleLine = true
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                FieldLabel(text = "FECHA")

                Spacer(modifier = Modifier.height(10.dp))

                AppInputField(
                    value = eventDate,
                    onValueChange = {
                        eventDate = it
                    },
                    placeholder = "mm/dd/yyyy",
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                FieldLabel(text = "HORA")

                Spacer(modifier = Modifier.height(10.dp))

                AppInputField(
                    value = eventTime,
                    onValueChange = {
                        eventTime = it
                    },
                    placeholder = "--:-- --",
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        FieldLabel(text = "DESCRIPCIÓN")

        Spacer(modifier = Modifier.height(10.dp))

        AppInputField(
            value = description,
            onValueChange = {
                description = it
            },
            placeholder = "Detalles adicionales, dosis o notas\nimportantes...",
            singleLine = false,
            height = 118.dp
        )

        Spacer(modifier = Modifier.height(26.dp))

        ReminderCard(
            checked = reminderEnabled,
            onCheckedChange = {
                reminderEnabled = it
            }
        )

        Spacer(modifier = Modifier.height(56.dp))

        Button(
            onClick = {
                errorMessage = null
                val startAt = parseEventDateTime(eventDate, eventTime)
                if (eventName.isBlank()) {
                    errorMessage = "Ingresá el nombre del evento."
                    return@Button
                }
                if (startAt == null) {
                    errorMessage = "Usá fecha `mm/dd/yyyy` y hora `hh:mm AM/PM`."
                    return@Button
                }

                scope.launch {
                    isSaving = true
                    runCatching {
                        agendaRepository.createAgendaEvent(
                            title = eventName.trim(),
                            description = description.trim(),
                            type = eventType.toBackendEventType(),
                            startAt = startAt,
                            endAt = startAt.plusHours(1)
                        )
                    }.onSuccess {
                        onSaveClick()
                    }.onFailure { throwable ->
                        errorMessage = throwable.toUserMessage("No se pudo guardar el evento")
                    }
                    isSaving = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Surface
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_save),
                contentDescription = "Guardar evento",
                tint = Surface,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isSaving) "Guardando..." else "Guardar evento",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = Color(0xFFB91C1C),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = "Toda la información es privada y segura.",
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private fun String.toBackendEventType(): String {
    return when (this.lowercase(Locale.getDefault())) {
        "medicación" -> "MEDICATION"
        "cita médica" -> "APPOINTMENT"
        "terapia" -> "THERAPY"
        else -> "CARE_ACTIVITY"
    }
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

@Composable
private fun RegisterEventHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Volver",
                tint = Primary,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Registrar evento",
            color = Primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more_vert),
                contentDescription = "Más opciones",
                tint = Neutral,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(
    text: String
) {
    Text(
        text = text,
        color = Neutral.copy(alpha = 0.82f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun SelectEventTypeField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_keyboard_arrow_down),
                contentDescription = "Seleccionar tipo de evento",
                tint = Neutral,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun AppInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    height: androidx.compose.ui.unit.Dp = 56.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = TextSecondary.copy(alpha = 0.82f),
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }

                innerTextField()
            }
        )
    }
}

@Composable
private fun ReminderCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Secondary.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Recordatorio",
                    tint = Color(0xFF4E6F61),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Activar recordatorio",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enviar notificación antes del\nevento",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Surface,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Surface,
                    uncheckedTrackColor = TextMuted.copy(alpha = 0.5f)
                )
            )
        }
    }
}
