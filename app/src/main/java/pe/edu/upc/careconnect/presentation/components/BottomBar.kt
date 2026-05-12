package pe.edu.upc.careconnect.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.presentation.theme.IconMuted
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted

data class BottomBarItem(
    val label: String,
    @DrawableRes val icon: Int
)

@Composable
fun BottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(72.dp),
        containerColor = Surface,
        tonalElevation = 4.dp
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex

            NavigationBarItem(
                selected = selected,
                onClick = {
                    onItemClick(index)
                },
                icon = {
                    AppIcon(
                        icon = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Primary else IconMuted
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Primary else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = IconMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = PrimaryLight
                )
            )
        }
    }
}