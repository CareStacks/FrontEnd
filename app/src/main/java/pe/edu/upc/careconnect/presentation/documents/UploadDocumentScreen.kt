package pe.edu.upc.careconnect.presentation.documents

import android.net.Uri
import android.provider.OpenableColumns
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.remote.toUserMessage
import pe.edu.upc.careconnect.data.repository.CareCacheRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.BackgroundSoft
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.OrangeDark
import pe.edu.upc.careconnect.presentation.theme.OrangeLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDocumentScreen(
    onBackClick: () -> Unit,
    onDocumentSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { CareCacheRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val documentTypes = remember { listOf("PDF", "Digital", "Imagen", "DOCX") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var documentDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            selectedFileUri = uri
            selectedFileName = uri?.lastPathSegment?.substringAfterLast('/')
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
    ) {
        CareScreenHeader(
            title = "Subir documento",
            navigationIcon = R.drawable.ic_arrow_back,
            navigationContentDescription = "Volver",
            onNavigationClick = onBackClick,
            actionIcon = null
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(PrimaryLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .height(4.dp)
                    .background(Primary)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                FilePickerSection(
                    selectedFileName = selectedFileName,
                    onPickFile = {
                        filePicker.launch(
                            arrayOf("application/pdf", "image/jpeg", "image/png")
                        )
                    }
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Border.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(25.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        LabeledDropdown(
                            label = "Tipo de documento",
                            value = selectedType,
                            placeholder = "Selecciona una opción",
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            options = documentTypes,
                            onOptionSelected = { type ->
                                selectedType = type
                                expanded = false
                            }
                        )

                        UploadTextField(
                            label = "Descripción",
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Ej: Resultados cardiólogo Mayo"
                        )

                        UploadTextField(
                            label = "Fecha del documento",
                            value = documentDate,
                            onValueChange = { documentDate = it },
                            placeholder = "mm/dd/yyyy",
                            trailingIcon = R.drawable.ic_calendar,
                            keyboardType = KeyboardType.Text
                        )
                    }
                }

                Button(
                    onClick = {
                        errorMessage = null

                        val fileUri = selectedFileUri
                        if (fileUri == null) {
                            errorMessage = "Seleccioná un archivo antes de continuar."
                            return@Button
                        }

                        if (selectedType.isBlank()) {
                            errorMessage = "Elegí el tipo de documento."
                            return@Button
                        }

                        scope.launch {
                            isSaving = true
                            runCatching {
                                repository.saveDocument(
                                    documentType = selectedType.toBackendDocumentType(),
                                    title = selectedFileName ?: "Documento médico",
                                    description = description.ifBlank {
                                        selectedFileName ?: "Documento médico"
                                    },
                                    fileUri = fileUri,
                                    mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream",
                                    fileSizeBytes = context.contentResolver.readFileSize(fileUri),
                                    uploadedAt = parseDocumentDate(documentDate)
                                )
                            }.onSuccess {
                                onDocumentSaved()
                            }.onFailure { throwable ->
                                errorMessage = throwable.toUserMessage("No se pudo subir el documento")
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Surface
                    )
                ) {
                    AppIcon(
                        icon = R.drawable.ic_upload,
                        contentDescription = null,
                        tint = Surface,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaving) "Subiendo..." else "Subir documento",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Surface
                    )
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = "Tus documentos se guardan de forma segura y solo son accesibles por ti y tus cuidadores autorizados.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            UploadTipsSection()
        }
    }
}

@Composable
private fun FilePickerSection(
    selectedFileName: String?,
    onPickFile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UploadLabel(text = "Seleccionar Archivo")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .background(Surface, RoundedCornerShape(12.dp))
                .border(2.dp, Border, RoundedCornerShape(12.dp))
                .clickable(onClick = onPickFile)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryLight, RoundedCornerShape(999.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        icon = R.drawable.ic_file,
                        contentDescription = null,
                        tint = Primary,
                        size = 28.dp
                    )
                }
                Text(
                    text = selectedFileName ?: "Toca para subir",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "PDF, JPG o PNG hasta 10MB",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun UploadTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: Int? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UploadLabel(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = placeholder, color = TextMuted)
            },
            trailingIcon = trailingIcon?.let { icon ->
                {
                    AppIcon(
                        icon = icon,
                        contentDescription = label,
                        tint = TextSecondary,
                        size = 20.dp
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(8.dp),
            colors = uploadTextFieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledDropdown(
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UploadLabel(text = label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                placeholder = {
                    Text(text = placeholder, color = TextMuted)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(8.dp),
                colors = uploadTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun uploadTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = Border,
    cursorColor = Primary,
    focusedContainerColor = BackgroundSoft,
    unfocusedContainerColor = BackgroundSoft,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

private fun String.toBackendDocumentType(): String {
    return when (this) {
        "PDF" -> "CLINICAL_REPORT"
        "Digital" -> "PRESCRIPTION"
        "Imagen" -> "IMAGING"
        "DOCX" -> "OTHER"
        else -> "OTHER"
    }
}

private fun android.content.ContentResolver.readFileSize(uri: Uri): Long {
    query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (columnIndex >= 0) {
                return cursor.getLong(columnIndex)
            }
        }
    }

    return 1L
}

private fun parseDocumentDate(rawValue: String): LocalDateTime? {
    if (rawValue.isBlank()) {
        return null
    }

    val formatters = listOf(
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),
        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US)
    )

    return formatters.firstNotNullOfOrNull { formatter ->
        runCatching {
            LocalDate.parse(rawValue.trim(), formatter).atStartOfDay()
        }.getOrNull()
    }
}

@Composable
private fun UploadTipsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Consejos para una buena subida",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        UploadTipCard(
            icon = R.drawable.ic_file,
            text = "Asegúrate de que el documento esté bien iluminado y que todo el texto sea legible antes de subirlo.",
            background = GreenLight.copy(alpha = 0.3f),
            border = GreenLight,
            content = GreenDark
        )

        UploadTipCard(
            icon = R.drawable.ic_alert,
            text = "Evita reflejos de luz directamente sobre el papel si estás sacando una foto.",
            background = OrangeLight.copy(alpha = 0.3f),
            border = OrangeLight,
            content = OrangeDark
        )
    }
}

@Composable
private fun UploadTipCard(
    icon: Int,
    text: String,
    background: androidx.compose.ui.graphics.Color,
    border: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(12.dp))
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        AppIcon(
            icon = icon,
            contentDescription = null,
            tint = content,
            size = 22.dp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = content
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UploadDocumentScreenPreview() {
    CareConnectTheme {
        UploadDocumentScreen(
            onBackClick = { },
            onDocumentSaved = { }
        )
    }
}
