package pe.edu.upc.careconnect.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.RedDark
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Contraseña",
    modifier: Modifier = Modifier,
    placeholder: String = "Ingresa tu contraseña",
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {
    val passwordVisible = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeholder)
            },
            singleLine = true,
            isError = isError,
            visualTransformation = if (passwordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                TextButton(
                    onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }
                ) {
                    Text(
                        text = if (passwordVisible.value) "Ocultar" else "Ver",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedLabelColor = Primary,
                unfocusedLabelColor = TextSecondary,
                cursorColor = Primary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                errorBorderColor = RedDark,
                errorLabelColor = RedDark,
                errorCursorColor = RedDark
            )
        )

        if (supportingText != null) {
            Text(
                text = supportingText,
                color = if (isError) RedDark else TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AppPasswordFieldPreview() {
    CareConnectTheme {
        val password = remember { mutableStateOf("") }

        AppPasswordField(
            value = password.value,
            onValueChange = { password.value = it },
            modifier = Modifier.padding(24.dp)
        )
    }
}