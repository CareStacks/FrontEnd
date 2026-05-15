package pe.edu.upc.careconnect.presentation.navigation

import androidx.annotation.DrawableRes
import pe.edu.upc.careconnect.R

enum class MainTab(
    val label: String,
    @DrawableRes val filledIcon: Int,
    @DrawableRes val outlineIcon: Int
) {
    Home(
        label = "Inicio",
        filledIcon = R.drawable.ic_home,
        outlineIcon = R.drawable.ic_home
    ),

    Agenda(
        label = "Agenda",
        filledIcon = R.drawable.ic_calendar,
        outlineIcon = R.drawable.ic_calendar
    ),

    Documents(
        label = "Documentos",
        filledIcon = R.drawable.ic_documents,
        outlineIcon = R.drawable.ic_documents
    ),

    Diary(
        label = "Diario",
        filledIcon = R.drawable.ic_diary,
        outlineIcon = R.drawable.ic_diary
    ),

    Profile(
        label = "Perfil",
        filledIcon = R.drawable.ic_profile,
        outlineIcon = R.drawable.ic_profile
    )
}