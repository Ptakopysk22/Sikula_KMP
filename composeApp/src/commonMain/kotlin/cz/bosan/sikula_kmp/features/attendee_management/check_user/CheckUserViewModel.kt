package cz.bosan.sikula_kmp.features.attendee_management.check_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.managers.user_manager.User
import cz.bosan.sikula_kmp.managers.user_manager.UserRepository
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckUserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() { //nevyřazovat z hledaných userů ty co už jsou na táboře, je zde poteciální riziko, že by pak mohlo dojít někým k vytvoření dalšího setjného usera
    private val _state = MutableStateFlow(CheckUserState())
    val state: StateFlow<CheckUserState> = _state

    init {
        searchUsersContainingString("")
    }

    fun onAction(action: CheckUserAction) {
        when (action) {
            is CheckUserAction.OnUserSelected -> {}
            is CheckUserAction.OnParameterSelected -> {
                _state.update {
                    it.copy(
                        selectedParameterIndex = action.index,
                        showAssignButton = false,
                        warningMessage = null,
                    )
                }
                if (_state.value.selectedParameterIndex == 1) {
                    searchLeaderEmail(state.value.searchedQuery)
                } else {
                    searchUsersContainingString(state.value.searchedQuery)
                }
            }

            is CheckUserAction.OnSearchClick -> {
                if (_state.value.selectedParameterIndex == 1) {
                    searchLeaderEmail(action.query)
                } else {
                    searchUsersContainingString(action.query)
                }
            }

            is CheckUserAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchedQuery = action.query
                    )
                }
                if (_state.value.selectedParameterIndex == 1) {
                    searchLeaderEmail(action.query)
                } else {
                    searchUsersContainingString(action.query)
                }
            }

            is CheckUserAction.OnDecideCheckingUserStatus -> {
                _state.update {
                    it.copy(isCheckingLeader = action.isLeader)
                }
            }

            is CheckUserAction.OnSearchBarFocusedChange -> {
                _state.update{
                    it.copy(isSearchBarFocused = action.isFocused)
                }
            }
        }
    }

    private fun searchUsersContainingString(string: String) {
        viewModelScope.launch {
            userRepository.getUsersContainingString(searchedString = string)
                .onSuccess { searchedUsers ->
                    val filteredUsers =
                        if (_state.value.isCheckingLeader) {
                            searchedUsers
                                .sortedWith(compareBy { it.email == null })
                        } else {
                            searchedUsers
                                .filter { it.email == null }
                        }
                    _state.update {
                        it.copy(
                            searchedUsers = filteredUsers,
                            warningMessage = if (filteredUsers.isEmpty()) {
                                if (state.value.isCheckingLeader) {
                                    Warning.Common.NONEXISTENT_LEADER.toUiText()
                                } else {
                                    Warning.Common.NONEXISTENT_CHILD.toUiText()
                                }
                            } else {
                                null
                            },
                            showAssignButton = !state.value.isCheckingLeader
                        )
                    }
                }.onError { error ->
                    if (error == DataError.Remote.NOT_FOUND) {
                        _state.update {
                            it.copy(
                                searchedUsers = emptyList(),
                                warningMessage = if (state.value.isCheckingLeader) {
                                    Warning.Common.NONEXISTENT_LEADER.toUiText()
                                } else {
                                    Warning.Common.NONEXISTENT_CHILD.toUiText()
                                },
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                searchedUsers = emptyList(),
                                errorMassage = error.toUiText(),
                                isLoading = false,
                            )
                        }
                    }
                }
        }
    }

    private fun searchLeaderEmail(email: String) {
        viewModelScope.launch {
            userRepository.getUserByEmail(email).onSuccess { user ->
                _state.update {
                    it.copy(
                        searchedUsers = listOf(user), errorMassage = null,
                        isLoading = false,
                        warningMessage = null,
                        showAssignButton = false,
                    )
                }
            }.onError { error ->
                if (error == DataError.Remote.NOT_FOUND) {
                    _state.update {
                        it.copy(
                            searchedUsers = emptyList(),
                            showAssignButton = true,
                            warningMessage = Warning.Common.NONEXISTENT_EMAIL.toUiText()
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            searchedUsers = emptyList(),
                            errorMassage = error.toUiText(),
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }
}

sealed interface CheckUserAction {
    data class OnSearchQueryChange(val query: String) : CheckUserAction
    data class OnSearchClick(val query: String) : CheckUserAction
    data class OnUserSelected(val user: User) : CheckUserAction
    data class OnParameterSelected(val index: Int) : CheckUserAction
    data class OnDecideCheckingUserStatus(val isLeader: Boolean) : CheckUserAction
    data class OnSearchBarFocusedChange(val isFocused: Boolean) : CheckUserAction
}

data class CheckUserState(
    val isLoading: Boolean = true,
    val isSearchBarFocused: Boolean = false,
    val isCheckingLeader: Boolean = true,
    val searchedUsers: List<User> = emptyList(),
    val selectedParameterIndex: Int = 0,
    val errorMassage: UiText? = null,
    val showAssignButton: Boolean = false,
    val warningMessage: UiText? = null,
    val searchedQuery: String = "",
)