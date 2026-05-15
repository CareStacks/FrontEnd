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
import pe.edu.upc.careconnect.presentation.diary.DiaryScreen
import pe.edu.upc.careconnect.presentation.diary.NewDiaryNoteScreen
import pe.edu.upc.careconnect.presentation.documents.DocumentsScreen
import pe.edu.upc.careconnect.presentation.documents.UploadDocumentScreen
import pe.edu.upc.careconnect.presentation.notifications.NotificationsScreen
import pe.edu.upc.careconnect.presentation.profile.ProfileScreen
import pe.edu.upc.careconnect.presentation.profile.ShareProfileScreen
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextMuted

private enum class MainOverlay {
    Notifications,
    UploadDocument,
    NewDiaryNote,
    ShareProfile
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
            val showBottomBar = overlay.value != MainOverlay.UploadDocument &&
                overlay.value != MainOverlay.NewDiaryNote &&
                overlay.value != MainOverlay.ShareProfile

            if (showBottomBar) {
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

                MainOverlay.UploadDocument -> {
                    UploadDocumentScreen(
                        onBackClick = { overlay.value = null },
                        onDocumentSaved = { overlay.value = null }
                    )
                }

                MainOverlay.NewDiaryNote -> {
                    NewDiaryNoteScreen(
                        onBackClick = { overlay.value = null },
                        onNoteSaved = { overlay.value = null }
                    )
                }

                MainOverlay.ShareProfile -> {
                    ShareProfileScreen(
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
                                onUploadClick = {
                                    overlay.value = MainOverlay.UploadDocument
                                },
                                onNotificationsClick = {
                                    overlay.value = MainOverlay.Notifications
                                }
                            )
                        }

                        MainTab.Diary -> {
                            DiaryScreen(
                                onNewNoteClick = {
                                    overlay.value = MainOverlay.NewDiaryNote
                                },
                                onNotificationsClick = {
                                    overlay.value = MainOverlay.Notifications
                                }
                            )
                        }

                        MainTab.Profile -> {
                            ProfileScreen(
                                onManageAccessClick = {
                                    overlay.value = MainOverlay.ShareProfile
                                },
                                onNotificationsClick = {
                                    overlay.value = MainOverlay.Notifications
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
