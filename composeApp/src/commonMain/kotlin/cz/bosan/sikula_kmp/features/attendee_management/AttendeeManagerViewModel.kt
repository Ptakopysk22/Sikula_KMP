package cz.bosan.sikula_kmp.features.attendee_management

import androidx.lifecycle.ViewModel
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.user_manager.User

class AttendeeManagerViewModel : ViewModel() {
    private var selectedChild: Child? = null
    private var selectedLeader: Leader? = null

    fun setSelectedUser(user: User, isLeader: Boolean) {
        if (isLeader) {
            selectedLeader = Leader(
                id = user.id,
                name = user.name?:"",
                nickName = user.nickName?:"",
                mail = user.email?:"",
                role = Role.NO_ROLE,
                positions = emptyList(),
                birthDate = user.birthDate,
                isActive = true,
                groupId = null,
                bankAccount = null,
                occupations = emptyList()
            )

        } else {
            selectedChild = Child(
                id = user.id,
                name = user.name ?: "",
                nickName = user.nickName ?: "",
                birthDate = user.birthDate,
                role = null,
                isActive = true,
                groupId = null,
                crewId = null,
                trailCategoryId = null
            )
        }
    }

    fun getSelectedChild(): Child? {
        return selectedChild
    }

    fun getSelectedLeader(): Leader? {
        return selectedLeader
    }

    fun deleteSelectedUser() {
        selectedChild = null
        selectedLeader = null
    }
}