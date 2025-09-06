package cz.bosan.sikula_kmp.features.attendee_management.child_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.user_manager.NewUser
import cz.bosan.sikula_kmp.managers.user_manager.User
import cz.bosan.sikula_kmp.managers.user_manager.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

class ChildDetailViewModel(
    private val leaderRepository: LeaderRepository,
    private val userRepository: UserRepository,
    private val campRepository: CampRepository,
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChildDetailState())
    val state: StateFlow<ChildDetailState> = _state

    fun onAction(action: ChildDetailAction) {
        when (action) {
            is ChildDetailAction.UpdateChild -> {
                _state.update { it.copy(child = it.child?.let(action.update)) }
            }

            ChildDetailAction.OnBackClick -> {}
            ChildDetailAction.OnSetChildClick -> {
                validateValues()
            }

            is ChildDetailAction.OnLoadChild -> loadChild(id = action.id)
            is ChildDetailAction.OnLoadNewChild -> loadNewChild(child = action.child)
            is ChildDetailAction.UpdateValidationState -> updateValidationState(action.changedValue)
            is ChildDetailAction.OnActiveButtonIndexChange -> {
                _state.update {
                    it.copy(selectedActiveButtonIndex = action.index)
                }
            }
        }
    }

    private suspend fun loadCurrentLeader() {
        val currentLeader = leaderRepository.getCurrentLeaderLocal()
        _state.update {
            it.copy(
                currentLeader = currentLeader
            )
        }
        if (currentLeader.leader.role == Role.DIRECTOR) {
            campRepository.getCrews(campId = currentLeader.camp.id, groupId = null)
                .onSuccess { crews ->
                    _state.update {
                        it.copy(crews = crews)
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            crews = emptyList(),
                            errorMassage = error.toUiText(),
                            isLoading = false
                        )
                    }
                }
        } else {
            campRepository.getCrews(campId = currentLeader.camp.id, groupId = currentLeader.leader.groupId)
                .onSuccess { crews ->
                    _state.update {
                        it.copy(crews = crews)
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            crews = emptyList(),
                            errorMassage = error.toUiText(),
                            isLoading = false
                        )
                    }
                }
        }
        childRepository.getTrailCategories(
            campId = currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        ).onSuccess { categories ->
            _state.update {
                it.copy(categories = categories)
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    categories = emptyList(),
                    errorMassage = error.toUiText(),
                    isLoading = false
                )
            }
        }
    }

    private fun loadChild(id: Int) {
        viewModelScope.launch {
            loadCurrentLeader()
            childRepository.getChild(
                campId = _state.value.currentLeader.camp.id,
                childId = id
            )
                .onSuccess { child ->
                    _state.update {
                        it.copy(
                            child = child,
                            selectedActiveButtonIndex = if (child.isActive) {
                                0
                            } else {
                                1
                            }
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            child = null,
                            errorMassage = error.toUiText(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadNewChild(child: Child) {
        viewModelScope.launch {
            loadCurrentLeader()
            _state.update {
                it.copy(child = child)
            }
        }
    }

    private fun validateValues() {
        if (_state.value.child?.name.isNullOrBlank()) {
            _state.update {
                it.copy(isNameValid = false)
            }
        } else {
            _state.update {
                it.copy(isNameValid = true)
            }
        }
        if (_state.value.child?.nickName.isNullOrBlank()) {
            _state.update {
                it.copy(isNicknameValid = false)
            }
        } else {
            _state.update {
                it.copy(isNicknameValid = true)
            }
        }
        if (_state.value.child?.crewId == null) {
            _state.update {
                it.copy(isCrewValid = false)
            }
        } else {
            _state.update {
                it.copy(isCrewValid = true)
            }
        }
        if (_state.value.child?.role == null) {
            _state.update {
                it.copy(isRoleValid = false)
            }
        } else {
            _state.update {
                it.copy(isRoleValid = true)
            }
        }
        if (_state.value.child?.trailCategoryId == null) {
            _state.update {
                it.copy(isCategoryValid = false)
            }
        } else {
            _state.update {
                it.copy(isCategoryValid = true)
            }
        }
        if (_state.value.isNameValid && _state.value.isNicknameValid && _state.value.isRoleValid && _state.value.isCrewValid && _state.value.isCategoryValid) {
            if (_state.value.child?.id == newItemID) {
                createUser()
            } else {
                updateUser()
            }
        }
    }

    private fun createUser() {
        viewModelScope.launch {
            state.value.child?.let {
                userRepository.createUser(
                    NewUser(
                        email = null,
                        name = state.value.child!!.name,
                        nickName = state.value.child?.nickName,
                        birthDate = if (state.value.child?.birthDate == null) LocalDate.now() else state.value.child?.birthDate,
                    )
                ).onSuccess { userId -> assignAttendee(userId.value) }
                    .onError { error ->
                        _state.update {
                            it.copy(
                                errorMassage = error.toUiText(),
                                isLoading = false
                            )
                        }
                    }
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            state.value.child?.let {
                userRepository.updateUser(
                    User(
                        id = state.value.child!!.id,
                        email = null,
                        name = state.value.child?.name,
                        nickName = state.value.child?.nickName,
                        birthDate = if (state.value.child?.birthDate == null) LocalDate.now() else state.value.child?.birthDate,
                    )
                ).onSuccess { result -> println(result) }
                    .onError { error ->
                        _state.update {
                            it.copy(
                                errorMassage = error.toUiText(),
                                isLoading = false
                            )
                        }
                    }
            }
            assignAttendee(userId = state.value.child!!.id)
        }
    }

    private suspend fun assignAttendee(userId: Int) {
        childRepository.assignAttendee(
            campId = state.value.currentLeader.camp.id,
            userId = userId,
            child = state.value.child!!,
        ).onSuccess {
            _state.update {
                it.copy(isChildAssignSuccessfully = true)
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    errorMassage = error.toUiText(),
                    isLoading = false
                )
            }
        }

    }

    private fun updateValidationState(changedValue: String) {
        when (changedValue) {
            "name" -> {
                _state.update {
                    it.copy(isNameValid = true)
                }
            }

            "nickname" -> {
                _state.update {
                    it.copy(isNicknameValid = true)
                }
            }

            "role" -> {
                _state.update {
                    it.copy(isRoleValid = true)
                }
            }

            "crew" -> {
                _state.update {
                    it.copy(isCrewValid = true)
                }
            }

            "category" -> {
                _state.update {
                    it.copy(isCategoryValid = true)
                }
            }
        }
    }

}

sealed interface ChildDetailAction {
    data class UpdateChild(val update: Child.() -> Child) : ChildDetailAction
    data class UpdateValidationState(val changedValue: String) : ChildDetailAction
    data class OnLoadNewChild(val child: Child) : ChildDetailAction
    data class OnLoadChild(val id: Int) : ChildDetailAction
    data object OnBackClick : ChildDetailAction
    data object OnSetChildClick : ChildDetailAction
    data class OnActiveButtonIndexChange(val index: Int) : ChildDetailAction
}

data class ChildDetailState(
    val isLoading: Boolean = true,
    val isChildAssignSuccessfully: Boolean = false,
    val errorMassage: UiText? = null,
    val child: Child? = null,
    val crews: List<Crew> = emptyList(),
    val categories: List<TrailCategory> = emptyList(),
    val selectedActiveButtonIndex: Int = 0,
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val isNameValid: Boolean = true,
    val isNicknameValid: Boolean = true,
    val isCrewValid: Boolean = true,
    val isCategoryValid: Boolean = true,
    val isRoleValid: Boolean = true,
)