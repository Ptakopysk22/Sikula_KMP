package cz.bosan.sikula_kmp.managers.camp_manager.domain

import androidx.compose.ui.graphics.Color
import cz.bosan.sikula_kmp.core.domain.SelectableItem

data class Group(
    override val id: Int,
    override val name: String,
    val color: Color,
    val crews: List<Crew>
) : SelectableItem

data class Crew(
    val id: Int,
    val groupId: Int,
    val name: String,
    val color: Color,
) {
    companion object {
        val EMPTY = Crew(
            id = 0,
            groupId = 0,
            name = "",
            color = Color.Black
        )
    }
}
