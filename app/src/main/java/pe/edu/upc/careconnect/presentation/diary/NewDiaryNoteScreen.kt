package pe.edu.upc.careconnect.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.repository.CareCacheRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.BackgroundSoft
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryDark
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun NewDiaryNoteScreen(
    onBackClick: () -> Unit,
    onNoteSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { CareCacheRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(repository) {
        repository.seedIfEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
    ) {
        CareScreenHeader(
            title = "Nueva nota",
            navigationIcon = R.drawable.ic_arrow_back,
            navigationContentDescription = "Volver",
            onNavigationClick = onBackClick,
            actionIcon = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NoteInputField(
                label = "TÍTULO (OPCIONAL)",
                value = title,
                onValueChange = { title = it },
                placeholder = "Escribe un título...",
                singleLine = true
            )

            NoteInputField(
                label = "CONTENIDO DE LA NOTA",
                value = content,
                onValueChange = { content = it },
                placeholder = "Comienza a escribir aquí...",
                singleLine = false,
                minLines = 14
            )

            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContextInfoCard(
                    icon = R.drawable.ic_calendar,
                    iconBackground = GreenLight,
                    iconTint = GreenDark,
                    label = "FECHA",
                    value = "24 de Mayo, 2024"
                )
                ContextInfoCard(
                    icon = R.drawable.ic_profile,
                    iconBackground = PrimaryLight,
                    iconTint = PrimaryDark,
                    label = "PACIENTE",
                    value = "Elena Rodriguez"
                )
            }

            Column(
                modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            repository.saveDiaryNote(
                                title = title,
                                body = content
                            )
                            onNoteSaved()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = PrimaryLight
                    )
                ) {
                    AppIcon(
                        icon = R.drawable.ic_save,
                        contentDescription = null,
                        tint = PrimaryLight,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar nota",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Se guardará automáticamente en el diario de Elena.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun NoteInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (singleLine) Modifier.height(56.dp) else Modifier.height(400.dp)),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                cursorColor = Primary,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun ContextInfoCard(
    icon: Int,
    iconBackground: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(PrimaryLight.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBackground, RoundedCornerShape(999.dp)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = icon,
                contentDescription = null,
                tint = iconTint,
                size = 20.dp
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewDiaryNoteScreenPreview() {
    CareConnectTheme {
        NewDiaryNoteScreen(
            onBackClick = { },
            onNoteSaved = { }
        )
    }
}
