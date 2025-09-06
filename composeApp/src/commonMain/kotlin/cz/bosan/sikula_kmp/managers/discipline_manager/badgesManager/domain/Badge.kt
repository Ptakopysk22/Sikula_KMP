package cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain

import androidx.compose.ui.graphics.Color

data class Badge(
    val id: Int,
    val name: String,
    val disciplineId: Int,
    val color: Color
)
