package pe.edu.upc.careconnect.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import pe.edu.upc.careconnect.presentation.theme.Primary

@Composable
fun AppIcon(
    @DrawableRes icon: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Primary,
    size: Dp = 24.dp
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}