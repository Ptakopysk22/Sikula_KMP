package cz.bosan.sikula_kmp.managers.children_manager.domain

import cz.bosan.sikula_kmp.core.domain.SelectableItem
import kotlinx.datetime.LocalDate

data class Child(
    val id: Int,
    val name: String,
    val nickName: String,
    val birthDate: LocalDate?,
    val role: ChildRole?,
    val isActive: Boolean,
    val groupId: Int?,
    val crewId: Int?,
    val trailCategoryId: Int?,
){
    companion object {
        val EMPTY = Child(
            id = 0,
            name = "",
            nickName = "",
            birthDate = null,
            role = ChildRole.MEMBER,
            isActive = false,
            groupId = null,
            crewId = null,
            trailCategoryId = null
        )
    }
}

data class SelectableChild(
   override val id: Int,
    override val name: String,
): SelectableItem

enum class ChildRole(val index: Int) {
    CREW_MASTER(997),
    DEPUTY_CREW_MASTER(998),
    MEMBER(999),
}