package cz.bosan.sikula_kmp.managers.children_manager.domain

import androidx.compose.ui.graphics.Color
import cz.bosan.sikula_kmp.core.domain.SelectableItem

data class TrailCategory(
    override val id: Int,
    override val name: String,
    val description: String,
    val baseTime: Int,
    val color: Color,
) : SelectableItem
