package cz.bosan.sikula_kmp.features.discipline_management.child_records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.data.LightChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data.BadgesRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChildRecordsViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val badgesRepository: BadgesRepository,
    initDiscipline: Discipline,
    child: LightChild,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChildRecordsState(
            discipline = initDiscipline,
            goBackDiscipline = initDiscipline,
            child = child,
        )
    )
    val state: StateFlow<ChildRecordsState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            _state.update {
                it.copy(currentLeader = currentLeader)
            }
            loadTrailCategories()
            loadCampLeaders()
            loadCampBadges()
            getIndividualDisciplineRecords(initDiscipline)
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ChildRecordsAction) {
        when (action) {
            is ChildRecordsAction.OnFilterItemSelected -> getIndividualDisciplineRecords(action.item)
        }
    }

    private fun loadTrailCategories() {
        viewModelScope.launch {
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
                            errorMessage = error.toUiText(),
                            trailCategories = emptyList(),
                        )
                    }
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

    private fun getIndividualDisciplineRecords(selectedDisciplineItem: SelectableItem) {
        viewModelScope.launch {
            var selectedDiscipline = getDisciplineById(selectedDisciplineItem.id.toString())
            if (selectedDiscipline == Discipline.Individual.TRIP || selectedDiscipline == Discipline.Individual.SWIMMING_RACE || selectedDiscipline == Discipline.Individual.NIGHT_GAME) {
                selectedDiscipline = Discipline.Badges.BADGES
            }
            _state.update {
                it.copy(discipline = selectedDiscipline)
            }
            if (selectedDiscipline == Discipline.Badges.BADGES) {
                badgesRepository.getCompetitorBadges(
                    campId = _state.value.currentLeader.camp.id,
                    competitorId = _state.value.child.id
                ).onSuccess { badges ->
                    _state.update {
                        it.copy(
                            badges = badges.sortedBy { it.campDay },
                            records = emptyList(),
                            warningMessage = if (badges.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            badges = emptyList(),
                            errorMessage = error.toUiText(),
                            warningMessage = null,
                        )
                    }
                }
            } else {
                individualDisciplineRecordRepository.getIndividualDisciplineRecordsCompetitor(
                    discipline = _state.value.discipline,
                    competitorId = _state.value.child.id,
                    campId = _state.value.currentLeader.camp.id
                ).onSuccess { records ->
                    _state.update {
                        it.copy(
                            records = records.sortedBy { it.campDay },
                            badges = emptyList(),
                            warningMessage = if (records.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            records = emptyList(),
                            errorMessage = error.toUiText(),
                            warningMessage = null,
                        )
                    }
                }
            }
        }
    }
}

sealed interface ChildRecordsAction {
    data class OnFilterItemSelected(val item: SelectableItem) : ChildRecordsAction
}

data class ChildRecordsState(
    val discipline: Discipline,
    val goBackDiscipline: Discipline,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val child: LightChild,
    val leaders: List<Leader> = emptyList(),
    val records: List<IndividualDisciplineRecord> = emptyList(),
    val badges: List<BadgeRecord> = emptyList(),
    val campBadges: List<Badge> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList(),
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val disciplines: List<SelectableItem> = listOf(
        Discipline.Individual.GRENADES,
        Discipline.Individual.ROPE_CLIMBING,
        Discipline.Individual.PULL_UPS,
        Discipline.Individual.TRAIL,
        Discipline.Individual.TIDYING,
        Discipline.Badges.BADGES,
        Discipline.Individual.NEGATIVE_POINTS,
        Discipline.Individual.AGILITY,
        Discipline.Individual.MORSE
    )
)