package pe.edu.upc.careconnect.presentation.documents

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.local.CachedDocumentEntity
import pe.edu.upc.careconnect.data.repository.CareCacheRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.OrangeDark
import pe.edu.upc.careconnect.presentation.theme.OrangeLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryDark
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun DocumentsScreen(
    onUploadClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { CareCacheRepository.getInstance(context) }
    val documents by repository.documents.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }

    LaunchedEffect(repository) {
        repository.seedIfEmpty()
    }

    val filteredDocuments = remember(documents, query) {
        if (query.isBlank()) {
            documents
        } else {
            documents.filter { document ->
                document.title.contains(query, ignoreCase = true) ||
                    document.type.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CareScreenHeader(
            title = "CareConnect",
            onActionClick = onNotificationsClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            DocumentsTitle()
            DocumentSearchField(
                value = query,
                onValueChange = { query = it }
            )
            DocumentList(documents = filteredDocuments)
            UploadDocumentButton(onClick = onUploadClick)
        }
    }
}

@Composable
private fun DocumentsTitle() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Documentos",
            style = MaterialTheme.typography.headlineLarge,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gestiona y revisa el historial médico de forma segura.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

@Composable
private fun DocumentSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Buscar documentos...",
                color = TextMuted
            )
        },
        leadingIcon = {
            AppIcon(
                icon = R.drawable.ic_search,
                contentDescription = "Buscar documentos",
                tint = TextMuted,
                size = 20.dp
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

@Composable
private fun DocumentList(documents: List<CachedDocumentEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DocumentSection(
            title = "RECIENTES",
            documents = documents.filter { it.section == "RECIENTES" }
        )
        DocumentSection(
            title = "HISTORIAL ANUAL",
            documents = documents.filter { it.section == "HISTORIAL ANUAL" }
        )
    }
}

@Composable
private fun DocumentSection(
    title: String,
    documents: List<CachedDocumentEntity>
) {
    if (documents.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.4.sp),
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )

        documents.forEach { document ->
            DocumentCard(document = document)
        }
    }
}

@Composable
private fun DocumentCard(document: CachedDocumentEntity) {
    val tone = rememberDocumentTone(document.tone)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(tone.background, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    icon = R.drawable.ic_file,
                    contentDescription = null,
                    tint = tone.content,
                    size = 22.dp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DocumentTypeBadge(
                        text = document.type,
                        background = tone.background,
                        content = tone.content
                    )
                    Text(
                        text = document.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            AppIcon(
                icon = R.drawable.ic_chevron_right,
                contentDescription = "Abrir ${document.title}",
                tint = TextMuted,
                size = 18.dp
            )
        }
    }
}

@Composable
private fun DocumentTypeBadge(
    text: String,
    background: Color,
    content: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UploadDocumentButton(onClick: () -> Unit) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Surface
        )
    ) {
        AppIcon(
            icon = R.drawable.ic_upload,
            contentDescription = null,
            tint = Surface,
            size = 18.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Subir documento",
            style = MaterialTheme.typography.bodyLarge,
            color = Surface
        )
    }
}

private data class DocumentTone(
    val background: Color,
    val content: Color
)

@Composable
private fun rememberDocumentTone(tone: String): DocumentTone {
    return when (tone) {
        "green" -> DocumentTone(GreenLight, GreenDark)
        "orange" -> DocumentTone(OrangeLight, OrangeDark)
        else -> DocumentTone(PrimaryLight, PrimaryDark)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DocumentsScreenPreview() {
    CareConnectTheme {
        DocumentsScreen(
            onUploadClick = { },
            onNotificationsClick = { }
        )
    }
}
