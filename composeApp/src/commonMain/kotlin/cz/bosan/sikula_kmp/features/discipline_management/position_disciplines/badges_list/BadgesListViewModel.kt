package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data.BadgesRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BadgesListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val campRepository: CampRepository,
    private val badgesRepository: BadgesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BadgesListState(
        )
    )
    val state: StateFlow<BadgesListState> = _state

    private var campsDayRecords: List<BadgeRecord> = emptyList()

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val campDay = leaderRepository.getCurrentCampDay()
            val todayCampDay = leaderRepository.getCurrentCampDay()
            val campDuration = leaderRepository.getCampDuration()
            _state.value =
                _state.value.copy(
                    currentLeader = currentLeader,
                    campDay = campDay,
                    todayCampDay = todayCampDay,
                    campDuration = campDuration
                )
            loadChildren()
            loadTrailCategories()
            loadCampLeaders()
            loadGroups()
            loadCrews()
            loadCampBadges()
            enabledUpdatingRecords()
            loadRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: BadgesListAction) {
        when (action) {
            is BadgesListAction.OnChangeCampDay -> updateCampDay(action.campDay)
            is BadgesListAction.OnCriteriaSelected -> {
                _state.update {
                    it.copy(
                        selectedFilterCriteria = action.filterCriteria,
                        filteredRecords = campsDayRecords,
                        selectedGroup = null,
                        selectedTrailCategory = null,
                    )
                }
            }

            is BadgesListAction.OnFilterItemSelected -> {
                _state.update {
                    it.copy(
                        selectedGroup = if (action.item is Group) action.item else if (action.item is Discipline) _state.value.selectedGroup else null,
                        selectedTrailCategory = if (action.item is TrailCategory) action.item else if (action.item is Discipline) _state.value.selectedTrailCategory else null,
                        selectedDiscipline = if (action.item is Discipline) action.item else _state.value.selectedDiscipline,
                    )
                }
                filterRecords()
            }

            is BadgesListAction.OnRecordUpdate ->
                if (_state.value.enabledUpdatingRecords == true) {
                    setRecordToBeGranted(action.record)
                }

        }
    }


    private fun updateCampDay(campDay: Int) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            _state.update {
                it.copy(campDay = campDay)
            }
            loadRecords()
            enabledUpdatingRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun enabledUpdatingRecords() {
        val enabledUpdatingRecords: Boolean
        val isBadgesMaster =
            (_state.value.currentLeader.leader.positions.contains(Position.BADGES_MASTER))
        if (isBadgesMaster) {
            enabledUpdatingRecords = true
        } else {
            enabledUpdatingRecords = false
        }
        _state.update {
            it.copy(enabledUpdatingRecords = enabledUpdatingRecords)
        }
    }

    private fun filterRecords() {
        val children = _state.value.children
        val records = campsDayRecords

        var filteredRecords = if (_state.value.selectedGroup != null) {
            val groupChildIds =
                children.filter { it.groupId == _state.value.selectedGroup?.id }.map { it.id }
            records.filter { it.competitorId in groupChildIds }
        } else if (_state.value.selectedTrailCategory != null) {
            val trailCategoryChildIds =
                children.filter { it.trailCategoryId == _state.value.selectedTrailCategory?.id }
                    .map { it.id }
            records.filter { it.competitorId in trailCategoryChildIds }
        } else {
            records
        }

        if (_state.value.selectedDiscipline != Discipline.Badges.BADGES) {
            val matchedBadges =
                _state.value.campBadges.filter { it.disciplineId == _state.value.selectedDiscipline.id }
            filteredRecords = filteredRecords.filter { record ->
                matchedBadges.any { badge -> badge.id == record.badgeId }
            }
        }

        val warningMessage =
            if (filteredRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
        _state.update {
            it.copy(
                filteredRecords = filteredRecords,
                warningMessage = warningMessage,
            )
        }
    }

    private suspend fun loadChildren() {
        childRepository.getCampsChildren(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role,
        ).onSuccess { children ->
            _state.update {
                it.copy(
                    children = children,
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
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
        ).onSuccess { categories ->
            _state.update {
                it.copy(
                    trailCategories = categories,
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    trailCategories = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadCampLeaders() {
        leaderRepository.getCampsLeaders(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        ).onSuccess { leaders ->
            _state.update {
                it.copy(
                    leaders = leaders
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    leaders = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadCampBadges() {
        badgesRepository.getCampBadges(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { campBadges ->
            _state.update {
                it.copy(
                    campBadges = campBadges
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    campBadges = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadRecords() {
        badgesRepository.getBadgeResults(
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }
            campsDayRecords = records.sortedWith(compareBy { record ->
                childMap[record.competitorId]?.nickName ?: ""
            })
            _state.update {
                it.copy(
                    warningMessage = if (campsDayRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null,
                    filteredRecords = campsDayRecords
                )
            }
            filterRecords()
        }.onError { error ->
            campsDayRecords = emptyList()
            _state.update {
                it.copy(
                    filteredRecords = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun setRecordToBeGranted(record: BadgeRecord) {
        viewModelScope.launch {
            badgesRepository.markBadgeToBeGranted(
                record = record,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess {
                _state.update {
                    it.copy(filteredRecords = it.filteredRecords.map { r ->
                        if (r.id == record.id) {
                            if (record.isAwarded) record.copy(
                                toBeAwarded = true,
                                isAwarded = false
                            ) else {
                                record.copy(toBeRemoved = true, isRemoved = false)
                            }
                        } else r
                    })
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        filteredRecords = emptyList()
                    )
                }
            }
        }
    }


    private suspend fun loadGroups() {
        campRepository.getGroups(campId = _state.value.currentLeader.camp.id).onSuccess { groups ->
            _state.update {
                it.copy(
                    groups = groups
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    errorMessage = error.toUiText(),
                    groups = emptyList()
                )
            }
        }
    }

    private suspend fun loadCrews() {
        campRepository.getCrews(campId = _state.value.currentLeader.camp.id, groupId = null)
            .onSuccess { crews ->
                _state.update {
                    it.copy(
                        crews = crews
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        crews = emptyList()
                    )
                }
            }
    }

}

sealed interface BadgesListAction {
    data class OnChangeCampDay(val campDay: Int) : BadgesListAction
    data class OnCriteriaSelected(val filterCriteria: FilterCriteria) :
        BadgesListAction

    data class OnFilterItemSelected(val item: SelectableItem?) : BadgesListAction
    data class OnRecordUpdate(val record: BadgeRecord) : BadgesListAction
}

data class BadgesListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline = Discipline.Badges.BADGES,
    val campDay: Int = 1,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val children: List<Child> = emptyList(),
    val leaders: List<Leader> = emptyList(),
    val filteredRecords: List<BadgeRecord> = emptyList(),
    val campBadges: List<Badge> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList(),
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val enabledUpdatingRecords: Boolean? = null,
    val selectedFilterCriteria: FilterCriteria = FilterCriteria.GROUPS,
    val selectedGroup: Group? = null,
    val selectedTrailCategory: TrailCategory? = null,
    val selectedDiscipline: Discipline = Discipline.Badges.BADGES,
)

