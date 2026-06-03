package pe.edu.upc.careconnect.presentation.diary

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.local.CachedDiaryNoteEntity
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
import pe.edu.upc.careconnect.presentation.theme.RedDark
import pe.edu.upc.careconnect.presentation.theme.RedLight
import pe.edu.upc.careconnect.presentation.theme.StatusReadBackground
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun DiaryScreen(
    onNewNoteClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { CareCacheRepository.getInstance(context) }
    val notes by repository.diaryNotes.collectAsState(initial = emptyList())
    var syncError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(repository) {
        syncError = repository.safeSyncDiaryNotes().exceptionOrNull()?.message
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
            DiaryTitleSection(onNewNoteClick = onNewNoteClick)

            syncError?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                notes.forEach { note ->
                    DiaryNoteCard(note = note)
                }
            }
        }
    }
}

@Composable
private fun DiaryTitleSection(onNewNoteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Diario",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registro del bienestar\ndiario",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Button(
            onClick = onNewNoteClick,
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = PrimaryLight
            )
        ) {
            AppIcon(
                icon = R.drawable.ic_plus,
                contentDescription = null,
                tint = PrimaryLight,
                size = 16.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Nueva\nnota",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryLight,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DiaryNoteCard(note: CachedDiaryNoteEntity) {
    val tone = diaryTone(note.tone)
    val tags = remember(note.tags) {
        note.tags.split('|').filter { it.isNotBlank() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (note.isHighlighted) 6.dp else 3.dp
        )
    ) {
        Box {
            Column(
                modifier = Modifier.padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DiaryAvatar(
                            author = note.author,
                            background = tone.background,
                            content = tone.content
                        )
                        Column {
                            Text(
                                text = note.author,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = note.timestamp,
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (note.isHighlighted) {
                        AppIcon(
                            icon = R.drawable.ic_more_vertical,
                            contentDescription = "Más opciones",
                            tint = TextMuted,
                            size = 20.dp
                        )
                    }
                }

                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (note.isHighlighted) TextPrimary else TextSecondary
                )

                if (tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tags.forEach { tag ->
                            DiaryTag(
                                text = tag,
                                background = tone.background,
                                content = tone.content
                            )
                        }
                    }
                }
            }

            if (note.isHighlighted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 0.dp, end = 0.dp)
                        .size(96.dp)
                        .background(GreenLight.copy(alpha = 0.2f), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun DiaryAvatar(
    author: String,
    background: Color,
    content: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = author.initials(),
            style = MaterialTheme.typography.labelLarge,
            color = content,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DiaryTag(
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
            style = MaterialTheme.typography.bodySmall,
            color = content
        )
    }
}

private data class DiaryTone(
    val background: Color,
    val content: Color
)

@Composable
private fun diaryTone(tone: String): DiaryTone {
    return when (tone) {
        "green" -> DiaryTone(GreenLight, GreenDark)
        "medical" -> DiaryTone(PrimaryLight, PrimaryDark)
        "error" -> DiaryTone(RedLight, RedDark)
        "neutral" -> DiaryTone(StatusReadBackground, TextSecondary)
        else -> DiaryTone(OrangeLight, OrangeDark)
    }
}

private fun String.initials(): String {
    return split(' ', '(', ')')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { word ->
            word.first().uppercase()
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DiaryScreenPreview() {
    CareConnectTheme {
        DiaryScreen(
            onNewNoteClick = { },
            onNotificationsClick = { }
        )
    }
}
