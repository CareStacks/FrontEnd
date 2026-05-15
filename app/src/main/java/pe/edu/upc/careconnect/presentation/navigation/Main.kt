package pe.edu.upc.careconnect.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.documents.DocumentsScreen
import pe.edu.upc.careconnect.presentation.notifications.NotificationsScreen
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted

private enum class MainOverlay {
    Notifications
}

@Composable
fun Main() {
    val selectedTab = rememberSaveable {
        mutableStateOf(MainTab.Home)
    }
    val overlay = rememberSaveable {
        mutableStateOf<MainOverlay?>(null)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Surface
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTab.value == tab

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedTab.value = tab
                        },
                        icon = {
                            AppIcon(
                                icon = if (selected) tab.filledIcon else tab.outlineIcon,
                                contentDescription = tab.label,
                                tint = if (selected) Primary else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (selected) Primary else TextMuted
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (overlay.value) {
                MainOverlay.Notifications -> {
                    NotificationsScreen(
                        onBackClick = { overlay.value = null }
                    )
                }

                null -> {
                    when (selectedTab.value) {
                        MainTab.Home -> {
                            // Luego aquí irá HomeScreen()
                            Text("Inicio", color = MaterialTheme.colorScheme.onBackground)
                        }

                        MainTab.Agenda -> {
                            // Luego aquí irá AgendaScreen()
                            Text("Agenda", color = MaterialTheme.colorScheme.onBackground)
                        }

                        MainTab.Documents -> {
                            DocumentsScreen(
                                onUploadClick = { },
                                onNotificationsClick = {
                                    overlay.value = MainOverlay.Notifications
                                }
                            )
                        }

                        MainTab.Diary -> {
                            // Luego aquí irá DocumentsScreen()
                            Text("Diario", color = MaterialTheme.colorScheme.onBackground)
                        }

                        MainTab.Profile -> {
                            // Luego aquí irá ProfileScreen()
                            Text("Perfil", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}
