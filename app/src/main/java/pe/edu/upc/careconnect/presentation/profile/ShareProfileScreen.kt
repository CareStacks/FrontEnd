package pe.edu.upc.careconnect.presentation.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import pe.edu.upc.careconnect.data.repository.ConsentRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.RedDark
import pe.edu.upc.careconnect.presentation.theme.RedLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun ShareProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val consentRepository = remember(context) { ConsentRepository.getInstance(context) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var caregiverEmail by remember { mutableStateOf("") }
    var agendaAccess by remember { mutableStateOf(true) }
    var documentsAccess by remember { mutableStateOf(true) }
    var diaryAccess by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<ShareFeedback?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CareScreenHeader(
            title = "Compartir perfil",
            navigationIcon = R.drawable.ic_arrow_back,
            navigationContentDescription = "Volver al perfil",
            onNavigationClick = onBackClick,
            actionIcon = R.drawable.ic_shield,
            actionContentDescription = "Seguridad del perfil"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShareHero()
            ShareForm(
                caregiverEmail = caregiverEmail,
                onCaregiverEmailChange = {
                    caregiverEmail = it
                    feedback = null
                },
                agendaAccess = agendaAccess,
                onAgendaAccessChange = { agendaAccess = it },
                documentsAccess = documentsAccess,
                onDocumentsAccessChange = { documentsAccess = it },
                diaryAccess = diaryAccess,
                onDiaryAccessChange = { diaryAccess = it }
            )
            feedback?.let { ShareFeedbackCard(feedback = it) }
            ShareProfileAction(
                isSubmitting = isSubmitting,
                enabled = !isSubmitting && caregiverEmail.isNotBlank(),
                onShareClick = {
                    val trimmedEmail = caregiverEmail.trim()
                    if (!trimmedEmail.isValidEmail()) {
                        feedback = ShareFeedback.Error("Ingresá un correo de cuidador válido.")
                        return@ShareProfileAction
                    }

                    isSubmitting = true
                    feedback = null
                    coroutineScope.launch {
                        runCatching {
                            consentRepository.shareProfileWithCaregiver(
                                caregiverEmail = trimmedEmail,
                                allowedViews = buildConsentViews(
                                    agendaAccess = agendaAccess,
                                    documentsAccess = documentsAccess,
                                    diaryAccess = diaryAccess
                                )
                            )
                        }.onSuccess { consent ->
                            feedback = ShareFeedback.Success(
                                "Perfil compartido con ${consent.caregiverFullName ?: trimmedEmail}."
                            )
                        }.onFailure { throwable ->
                            feedback = ShareFeedback.Error(
                                throwable.toUserMessage("No se pudo compartir el perfil")
                            )
                        }
                        isSubmitting = false
                    }
                }
            )
        }
    }
}

@Composable
private fun ShareHero() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(192.dp)
                .background(PrimaryLight.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = R.drawable.ic_medical,
                contentDescription = null,
                tint = Primary,
                size = 82.dp
            )
        }

        Text(
            text = "Permite que un cuidador acceda a tu información de cuidado.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun ShareForm(
    caregiverEmail: String,
    onCaregiverEmailChange: (String) -> Unit,
    agendaAccess: Boolean,
    onAgendaAccessChange: (Boolean) -> Unit,
    documentsAccess: Boolean,
    onDocumentsAccessChange: (Boolean) -> Unit,
    diaryAccess: Boolean,
    onDiaryAccessChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShareSectionLabel(text = "CORREO DEL CUIDADOR")
            OutlinedTextField(
                value = caregiverEmail,
                onValueChange = onCaregiverEmailChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "ejemplo@correo.com", color = TextMuted)
                },
                trailingIcon = {
                    AppIcon(
                        icon = R.drawable.ic_email,
                        contentDescription = "Correo del cuidador",
                        tint = TextSecondary,
                        size = 20.dp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ShareSectionLabel(text = "PERMISOS DE ACCESO")
            PermissionOption(
                icon = R.drawable.ic_calendar,
                title = "Agenda",
                subtitle = "Citas y recordatorios",
                checked = agendaAccess,
                onCheckedChange = onAgendaAccessChange
            )
            PermissionOption(
                icon = R.drawable.ic_documents,
                title = "Documentos",
                subtitle = "Informes y recetas",
                checked = documentsAccess,
                onCheckedChange = onDocumentsAccessChange
            )
            PermissionOption(
                icon = R.drawable.ic_diary,
                title = "Diario",
                subtitle = "Notas de salud diaria",
                checked = diaryAccess,
                onCheckedChange = onDiaryAccessChange
            )
        }
    }
}

@Composable
private fun ShareSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PermissionOption(
    icon: Int,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryLight.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        icon = icon,
                        contentDescription = null,
                        tint = Primary,
                        size = 22.dp
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = Border,
                    checkmarkColor = Surface
                )
            )
        }
    }
}

@Composable
private fun ShareFeedbackCard(feedback: ShareFeedback) {
    val isSuccess = feedback is ShareFeedback.Success

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isSuccess) GreenLight else RedLight),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) GreenLight.copy(alpha = 0.55f) else RedLight.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                icon = if (isSuccess) R.drawable.ic_check_circle else R.drawable.ic_alert,
                contentDescription = null,
                tint = if (isSuccess) GreenDark else RedDark,
                size = 22.dp
            )
            Text(
                text = feedback.message,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSuccess) GreenDark else RedDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShareProfileAction(
    isSubmitting: Boolean,
    enabled: Boolean,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onShareClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = PrimaryLight,
                disabledContainerColor = PrimaryLight,
                disabledContentColor = TextSecondary
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Primary
                )
            } else {
                Text(
                    text = "Compartir perfil",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) PrimaryLight else TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppIcon(
                    icon = R.drawable.ic_upload,
                    contentDescription = null,
                    tint = if (enabled) PrimaryLight else TextSecondary,
                    size = 18.dp
                )
            }
        }

        Text(
            text = "Podrás revocar este acceso en cualquier momento desde tu configuración.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

private fun buildConsentViews(
    agendaAccess: Boolean,
    documentsAccess: Boolean,
    diaryAccess: Boolean
): List<String> {
    return buildList {
        add("PROFILE")
        if (agendaAccess) add("AGENDA")
        if (documentsAccess) add("DOCUMENTS")
        if (diaryAccess) add("DIARY")
    }
}

private fun String.isValidEmail(): Boolean {
    return matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
}

private sealed class ShareFeedback(open val message: String) {
    data class Success(override val message: String) : ShareFeedback(message)
    data class Error(override val message: String) : ShareFeedback(message)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ShareProfileScreenPreview() {
    CareConnectTheme {
        ShareProfileScreen(onBackClick = { })
    }
}
