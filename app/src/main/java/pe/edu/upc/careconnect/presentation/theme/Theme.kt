package pe.edu.upc.careconnect.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,

    secondary = Secondary,
    onSecondary = TextPrimary,

    tertiary = Tertiary,
    onTertiary = TextPrimary,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,

    surfaceVariant = BackgroundSoft,
    onSurfaceVariant = TextSecondary,

    outline = Border,
    outlineVariant = Disabled,

    error = RedDark,
    onError = Color.White,

    errorContainer = RedLight,
    onErrorContainer = RedDark
)

@Composable
fun CareConnectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}