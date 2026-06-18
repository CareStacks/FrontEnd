package pe.edu.upc.careconnect.presentation.login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.data.remote.toUserMessage
import pe.edu.upc.careconnect.data.repository.AuthRepository
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.FilledButton
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

private enum class RegisterRole {
    Patient,
    Caregiver
}

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onCreateAccountSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authRepository = remember(context) { AuthRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val selectedRole = remember { mutableStateOf(RegisterRole.Patient) }
    val acceptedTerms = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                AppIcon(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = "Volver",
                    tint = Primary,
                    size = 28.dp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "CareConnect",
                style = MaterialTheme.typography.titleLarge,
                color = Primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Crear cuenta",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Únete a nuestra comunidad de cuidado mutuo.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        RegisterLabel(text = "NOMBRES Y APELLIDOS")

        Spacer(modifier = Modifier.height(8.dp))

        RegisterTextField(
            value = fullName.value,
            onValueChange = { fullName.value = it },
            placeholder = "Ej: María García"
        )

        Spacer(modifier = Modifier.height(24.dp))

        RegisterLabel(text = "CORREO ELECTRÓNICO")

        Spacer(modifier = Modifier.height(8.dp))

        RegisterTextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = "nombre@ejemplo.com",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(24.dp))

        RegisterLabel(text = "CONTRASEÑA")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "••••••••",
                    color = TextMuted
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }
                ) {
                    AppIcon(
                        icon = R.drawable.ic_visibility,
                        contentDescription = "Mostrar contraseña",
                        tint = TextSecondary,
                        size = 24.dp
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(12.dp),
            colors = registerTextFieldColors()
        )

        Spacer(modifier = Modifier.height(24.dp))

        RegisterLabel(text = "¿CUÁL ES TU ROL?")

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoleCard(
                text = "Paciente",
                icon = R.drawable.ic_profile,
                selected = selectedRole.value == RegisterRole.Patient,
                onClick = {
                    selectedRole.value = RegisterRole.Patient
                },
                modifier = Modifier.weight(1f)
            )

            RoleCard(
                text = "Cuidador",
                icon = R.drawable.ic_medical,
                selected = selectedRole.value == RegisterRole.Caregiver,
                onClick = {
                    selectedRole.value = RegisterRole.Caregiver
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = acceptedTerms.value,
                onCheckedChange = {
                    acceptedTerms.value = it
                },
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = Border,
                    checkmarkColor = Surface
                )
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Acepto los Términos de Servicio y la Política de Privacidad de CareConnect.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        FilledButton(
            text = if (isLoading) "Creando cuenta..." else "Crear cuenta  →",
            onClick = {
                errorMessage = null

                when {
                    fullName.value.isBlank() -> errorMessage = "Ingresá tu nombre completo."
                    !fullName.value.isValidFullName() -> errorMessage = "Revisa el nombre completo ingresado."
                    email.value.isBlank() -> errorMessage = "Ingresá tu correo electrónico."
                    !email.value.isValidEmailAddress() -> errorMessage = "Ingresa un correo electrónico válido."
                    password.value.length < 8 -> errorMessage = "La contraseña debe tener al menos 8 caracteres."
                    !password.value.hasRequiredPasswordStrength() -> errorMessage = "La contraseña debe incluir al menos una mayúscula y un número."
                    !acceptedTerms.value -> errorMessage = "Tenés que aceptar los términos para continuar."
                    else -> {
                        scope.launch {
                            isLoading = true
                            runCatching {
                                authRepository.register(
                                    fullName = fullName.value.trim(),
                                    email = email.value.trim(),
                                    password = password.value,
                                    role = if (selectedRole.value == RegisterRole.Patient) "PATIENT" else "CAREGIVER"
                                )
                            }.onSuccess {
                                onCreateAccountSuccess()
                            }.onFailure { throwable ->
                                errorMessage = throwable.toUserMessage("No se pudo crear la cuenta")
                            }
                            isLoading = false
                        }
                    }
                }
            },
            enabled = true
        )

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

        Image(
            painter = painterResource(id = R.drawable.img_register_footer),
            contentDescription = "Imagen de comunidad de cuidado",
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes una cuenta?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Inicia sesión",
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun RegisterLabel(
    text: String
) {
    Text(
        text = text,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextMuted
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(12.dp),
        colors = registerTextFieldColors()
    )
}

@Composable
private fun RoleCard(
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .height(82.dp)
            .clip(shape)
            .background(
                color = if (selected) PrimaryLight else Surface,
                shape = shape
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Primary else Border,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon(
                icon = icon,
                contentDescription = text,
                tint = Primary,
                size = 24.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = Border,
    cursorColor = Primary,
    focusedContainerColor = Surface,
    unfocusedContainerColor = Surface,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    CareConnectTheme {
        RegisterScreen(
            onBackClick = { },
            onCreateAccountSuccess = { },
            onLoginClick = { }
        )
    }
}
