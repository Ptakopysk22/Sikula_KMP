package cz.bosan.sikula_kmp.features.attendee_management.children_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChildrenListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val campRepository: CampRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChildrenListState()
    )
    val state: StateFlow<ChildrenListState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                )
            loadGroupChildren()
            loadTrailCategories()
            loadCrews()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ChildrenListAction) {
        when (action) {
            ChildrenListAction.OnLogoutClicked -> onLogoutClicked()
            ChildrenListAction.ResetLogout -> resetLogoutSuccessful()
            is ChildrenListAction.OnChildSelected -> {}
        }
    }

    private suspend fun loadCrews() {
        campRepository.getCrews(
            campId = state.value.currentLeader.camp.id,
            groupId = state.value.currentLeader.leader.groupId
        ).onSuccess { crews ->
            _state.update {
                it.copy(crews = crews)
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    crews = emptyList(),
                    errorMassage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadGroupChildren() {
        childRepository.getCampsChildren(
            campId = _state.value.currentLeader.camp.id,
            groupId = _state.value.currentLeader.leader.groupId,
            role = _state.value.currentLeader.leader.role
        )
            .onSuccess { children ->
                _state.update {
                    it.copy(children = children.sortedBy { it.nickName })
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        children = emptyList(),
                        errorMassage = error.toUiText(),
                    )
                }
            }
    }

    private suspend fun loadTrailCategories() {
        childRepository.getTrailCategories(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        )
            .onSuccess { result ->
                _state.update {
                    it.copy(
                        trailCategories = result
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        trailCategories = emptyList(),
                        errorMassage = error.toUiText(),
                    )
                }
            }
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            leaderRepository.deleteCurrentLeader()
            _state.update {
                it.copy(logoutSuccessful = true)
            }
        }
    }

    private fun resetLogoutSuccessful() {
        _state.update {
            it.copy(logoutSuccessful = false)
        }
    }

}

sealed interface ChildrenListAction {
    data object OnLogoutClicked : ChildrenListAction
    data object ResetLogout : ChildrenListAction
    data class OnChildSelected(val child: Child) : ChildrenListAction
}

data class ChildrenListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val errorMassage: UiText? = null,
    val children: List<Child> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList()
)