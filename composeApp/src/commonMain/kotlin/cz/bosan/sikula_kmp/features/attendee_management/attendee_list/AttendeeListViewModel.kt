package cz.bosan.sikula_kmp.features.attendee_management.attendee_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttendeeListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val campRepository: CampRepository,
    selectedTabIndex: Int,
) : ViewModel() {

    private var allChildren: List<Child> = emptyList()

    private val _state = MutableStateFlow(
        AttendeeListState(selectedTabIndex = selectedTabIndex)
    )
    val state: StateFlow<AttendeeListState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                )
            loadCampLeaders()
            loadCampChildren()
            loadGroups()
            loadCrews()
            loadTrailCategories()
            _state.update{
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: AttendeeListAction) {
        when (action) {
            AttendeeListAction.OnLogoutClicked -> onLogoutClicked()
            AttendeeListAction.ResetLogout -> resetLogoutSuccessful()
            is AttendeeListAction.OnLeaderSelected -> {}
            is AttendeeListAction.OnChildSelected -> {}
            is AttendeeListAction.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index, isLoading = true)
                }
                loadDataOnTab(tabIndex = action.index)
                _state.update{
                    it.copy(isLoading = false)
                }
            }

            is AttendeeListAction.OnFilterItemSelected -> {
                _state.update {
                    it.copy(isLoading = true)
                    when (val selectedItem = action.item) {
                        is Group -> it.copy(
                            selectedGroup = selectedItem,
                            children = allChildren.filter { child -> child.groupId == selectedItem.id }
                        )

                        is TrailCategory -> it.copy(
                            selectedTrailCategory = selectedItem,
                            children = allChildren.filter { child -> child.trailCategoryId == selectedItem.id }
                        )

                        null -> it.copy(
                            selectedGroup = null,
                            selectedTrailCategory = null,
                            children = allChildren
                        )

                        else -> it
                    }
                }
                _state.update{
                    it.copy(isLoading = false)
                }
            }

            is AttendeeListAction.OnCriteriaSelected -> {
                _state.update{
                    it.copy(isLoading = true)
                }
                _state.update {
                    it.copy(
                        selectedFilterCriteria = action.filterCriteria,
                        selectedGroup = null,
                        selectedTrailCategory = null,
                        children = allChildren,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun loadDataOnTab(tabIndex: Int){
        viewModelScope.launch {
            if (tabIndex == 0) {
                _state.update {
                    it.copy(
                        selectedFilterCriteria = FilterCriteria.GROUPS,
                        selectedGroup = null
                    )
                }
                loadCampChildren()
                loadGroups()
                loadCrews()
                loadTrailCategories()
            } else {
                loadCampLeaders()
            }
        }
    }

    private suspend fun loadCampLeaders() {
            leaderRepository.getCampsLeaders(
                campId = state.value.currentLeader.camp.id,
                role = _state.value.currentLeader.leader.role
            )
                .onSuccess { leaders ->
                    _state.value =
                        _state.value.copy(
                            leaders = leaders,
                            errorMessage = null,
                        )
                }.onError { error ->
                    _state.value =
                        _state.value.copy(
                            leaders = emptyList(),
                            errorMessage = error.toUiText(),
                        )
                }
    }

    private suspend fun loadCampChildren() {
            childRepository.getCampsChildren(
                campId = _state.value.currentLeader.camp.id,
                role = _state.value.currentLeader.leader.role
            )
                .onSuccess { children ->
                    _state.update {
                        if (children.isEmpty()) {
                            it.copy(
                                warningMessage = Warning.Common.EMPTY_LIST.toUiText(),
                                children = children
                            )
                        } else {
                            it.copy(
                                children = children.sortedWith(
                                    compareBy<Child> { it.groupId }
                                        .thenBy { it.crewId }
                                        .thenBy { it.role }
                                ),
                                warningMessage = null,
                            )
                        }
                    }
                }.onError { error ->
                    _state.value =
                        _state.value.copy(
                            children = emptyList(),
                            errorMessage = error.toUiText(),
                        )
        }
        allChildren = _state.value.children

    }

    private suspend fun loadGroups() {
            campRepository.getGroups(campId = _state.value.currentLeader.camp.id)
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
                            children = emptyList(),
                            leaders = emptyList(),
                            errorMessage = error.toUiText(),
                        )
                    }
                }
    }

    private suspend fun loadCrews() {
            campRepository.getCrews(campId = _state.value.currentLeader.camp.id, groupId = null)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            crews = result
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            crews = emptyList(),
                            children = emptyList(),
                            errorMessage = error.toUiText(),
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
                            children = emptyList(),
                            errorMessage = error.toUiText(),
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

sealed interface AttendeeListAction {
    data object OnLogoutClicked : AttendeeListAction
    data object ResetLogout : AttendeeListAction
    data class OnLeaderSelected(val leader: Leader) : AttendeeListAction
    data class OnChildSelected(val child: Child) : AttendeeListAction
    data class OnTabSelected(val index: Int) : AttendeeListAction
    data class OnCriteriaSelected(val filterCriteria: FilterCriteria) : AttendeeListAction
    data class OnFilterItemSelected(val item: SelectableItem?) : AttendeeListAction
}

data class AttendeeListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val leaders: List<Leader> = emptyList(),
    val children: List<Child> = emptyList(),
    val selectedTabIndex: Int,
    val selectedFilterCriteria: FilterCriteria = FilterCriteria.GROUPS,
    val selectedGroup: Group? = null,
    val selectedTrailCategory: TrailCategory? = null,
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList()
)