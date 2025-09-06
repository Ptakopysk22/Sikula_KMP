package cz.bosan.sikula_kmp.features.attendee_management.leader_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Occupation
import cz.bosan.sikula_kmp.managers.user_manager.NewUser
import cz.bosan.sikula_kmp.managers.user_manager.User
import cz.bosan.sikula_kmp.managers.user_manager.UserRepository
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

class LeaderDetailViewModel(
    private val leaderRepository: LeaderRepository,
    private val userRepository: UserRepository,
    private val campRepository: CampRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderDetailState())
    val state: StateFlow<LeaderDetailState> = _state

    fun onAction(action: LeaderDetailAction) {
        when (action) {
            is LeaderDetailAction.UpdateLeader -> {
                _state.update { it.copy(leader = it.leader?.let(action.update)) }
            }

            LeaderDetailAction.OnBackClick -> {}
            LeaderDetailAction.OnSetLeaderClick -> {
                validateValues()
            }

            is LeaderDetailAction.OnLoadLeader -> loadLeader(id = action.id)
            is LeaderDetailAction.OnLoadNewLeader -> loadNewLeader(leader = action.leader)
            is LeaderDetailAction.UpdateValidationState -> updateValidationState(action.changedValue)
            is LeaderDetailAction.OnActiveButtonIndexChange -> {
                _state.update {
                    it.copy(selectedActiveButtonIndex = action.index)
                }
            }
        }
    }

    private suspend fun loadCampAndGroups() {
        val campId = leaderRepository.getCurrentCampId()
        _state.update {
            it.copy(
                currentCampId = campId
            )
        }
        campRepository.getGroups(campId = campId!!)
            .onSuccess { result ->
                _state.update {
                    it.copy(
                        groups = result
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        groups = emptyList(),
                        errorMassage = error.toUiText(),
                        isLoading = false
                    )
                }
            }
    }

    private fun loadLeader(id: Int) {
        viewModelScope.launch {
            loadCampAndGroups()
            val campId = _state.value.currentCampId
            if (campId != null) {
                leaderRepository.getLeader(leaderId = id, campId = campId).onSuccess { leader ->
                    _state.update {
                        it.copy(
                            leader = leader,
                            selectedActiveButtonIndex = if (leader.isActive) {
                                0
                            } else {
                                1
                            }
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            leader = null,
                            errorMassage = error.toUiText(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadNewLeader(leader: Leader) {
        viewModelScope.launch {
            loadCampAndGroups()
            _state.update {
                it.copy(leader = leader)
            }
        }
    }

    private suspend fun userWithEmailExist(): Boolean {
        var exist = true
        userRepository.getUserByEmail(state.value.leader?.mail!!)
            .onSuccess { user ->
                if (user.id != state.value.leader?.id) {
                    exist = true
                } else {
                    exist = false
                }
            }
            .onError { error ->
                exist = error != DataError.Remote.NOT_FOUND
            }
        return exist
    }

    private fun validateValues() {
        viewModelScope.launch {
            if (_state.value.leader?.mail.isNullOrBlank() || userWithEmailExist()) {
                _state.update {
                    it.copy(isEmailValid = false)
                }
            } else {
                _state.update {
                    it.copy(isEmailValid = true)
                }
            }
            if (_state.value.leader?.name.isNullOrBlank()) {
                _state.update {
                    it.copy(isNameValid = false)
                }
            } else {
                _state.update {
                    it.copy(isNameValid = true)
                }
            }
            if (_state.value.leader?.nickName.isNullOrBlank()) {
                _state.update {
                    it.copy(isNicknameValid = false)
                }
            } else {
                _state.update {
                    it.copy(isNicknameValid = true)
                }
            }
            if (_state.value.leader?.role == null || _state.value.leader?.role == Role.NO_ROLE) {
                _state.update {
                    it.copy(isRoleValid = false)
                }
            } else {
                _state.update {
                    it.copy(isRoleValid = true)
                }
            }
            if ((_state.value.leader?.groupId == null && _state.value.leader?.role == Role.CHILD_LEADER) || (_state.value.leader?.groupId == null && _state.value.leader?.role == Role.HEAD_GROUP_LEADER)) {
                _state.update {
                    it.copy(isGroupValid = false)
                }
            } else {
                _state.update {
                    it.copy(isGroupValid = true)
                }
            }

            if (_state.value.isEmailValid && _state.value.isNameValid && _state.value.isNicknameValid && _state.value.isRoleValid && _state.value.isGroupValid) {
                if (_state.value.leader?.id == newItemID) {
                    createUser()
                } else {
                    updateUser()
                }
            }
        }
    }

    private suspend fun createUser() {
        state.value.leader?.let {
            userRepository.createUser(
                NewUser(
                    email = state.value.leader!!.mail,
                    name = state.value.leader!!.name,
                    nickName = state.value.leader?.nickName,
                    birthDate = state.value.leader?.birthDate?: LocalDate.now(),
                )
            ).onSuccess { userId ->
                assignAttendee(userId.value)
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMassage = error.toUiText(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun updateUser() {
        state.value.leader?.let {
            userRepository.updateUser(
                User(
                    id = state.value.leader!!.id,
                    email = state.value.leader?.mail,
                    name = state.value.leader?.name,
                    nickName = state.value.leader?.nickName,
                    birthDate = state.value.leader?.birthDate?: LocalDate.now(),
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
        assignAttendee(userId = state.value.leader!!.id)
    }

    private suspend fun assignAttendee(userId: Int) {
        val role = _state.value.leader?.role
        leaderRepository.assignAttendee(
            campId = state.value.currentCampId!!,
            userId = userId,
            occupation = Occupation(
                campId = state.value.currentCampId!!,
                role = state.value.leader?.role!!,
                isActive = state.value.leader?.isActive!!,
                groupId = if (role != Role.CHILD_LEADER && role != Role.HEAD_GROUP_LEADER) null else state.value.leader?.groupId,
                positions = if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
                    state.value.leader!!.positions - listOf(
                        Position.QUIZ_MASTER,
                        Position.NEGATIVE_POINTS_MASTER,
                        Position.MORSE_MASTER,
                        Position.BOAT_RACE_MASTER,
                        Position.UNKNOWN_POSITION
                    )
                } else {
                    state.value.leader!!.positions - Position.UNKNOWN_POSITION
                }
            )
        ).onSuccess {
            _state.update {
                it.copy(isLeaderAssignSuccessfully = true)
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
            "email" -> {
                _state.update {
                    it.copy(isEmailValid = true)
                }
            }

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

            "group" -> {
                _state.update {
                    it.copy(isGroupValid = true)
                }
            }
        }
    }

}

sealed interface LeaderDetailAction {
    data class UpdateLeader(val update: Leader.() -> Leader) : LeaderDetailAction
    data class UpdateValidationState(val changedValue: String) : LeaderDetailAction
    data class OnLoadLeader(val id: Int) : LeaderDetailAction
    data class OnLoadNewLeader(val leader: Leader) : LeaderDetailAction
    data object OnBackClick : LeaderDetailAction
    data object OnSetLeaderClick : LeaderDetailAction
    data class OnActiveButtonIndexChange(val index: Int) : LeaderDetailAction
}

data class LeaderDetailState(
    val isLoading: Boolean = true,
    val isLeaderAssignSuccessfully: Boolean = false,
    val errorMassage: UiText? = null,
    val leader: Leader? = null,
    val groups: List<Group> = emptyList(),
    val selectedActiveButtonIndex: Int = 0,
    val currentCampId: Int? = null,
    val isEmailValid: Boolean = true,
    val isNameValid: Boolean = true,
    val isNicknameValid: Boolean = true,
    val isRoleValid: Boolean = true,
    val isGroupValid: Boolean = true,
)
