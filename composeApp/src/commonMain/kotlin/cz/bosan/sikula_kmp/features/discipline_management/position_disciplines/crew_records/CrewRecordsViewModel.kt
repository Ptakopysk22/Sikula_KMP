package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.crew_records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrewRecordsViewModel(
    private val leaderRepository: LeaderRepository,
    private val teamDisciplineRecordRepository: TeamDisciplineRepository,
    initDiscipline: Discipline,
    crewDto: CrewDto,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CrewRecordsState(
            discipline = initDiscipline,
            crew = crewDto.toCrew(),
        )
    )
    val state: StateFlow<CrewRecordsState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                )
            viewModelScope.launch {
                state.map { it.currentLeader }
                    .distinctUntilChanged()
                    .collectLatest {
                        loadCampLeaders()
                        getTeamDisciplineRecords(initDiscipline)
                    }
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun onAction(action: CrewRecordsAction) {
        when (action) {
            is CrewRecordsAction.OnFilterItemSelected -> getTeamDisciplineRecords(action.item)
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

    private fun getTeamDisciplineRecords(selectedDiscipline: SelectableItem) {
        viewModelScope.launch {
            _state.update {
                it.copy(discipline = getDisciplineById(selectedDiscipline.id.toString()))
            }
            teamDisciplineRecordRepository.getTeamDisciplineRecordsCrew(
                discipline = _state.value.discipline,
                crewId = _state.value.crew.id,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess { records ->
                _state.update {
                    it.copy(
                        records = records.sortedBy { it.campDay },
                        warningMessage = if (records.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null,
                        isLoading = false
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        records = emptyList(),
                        errorMessage = error.toUiText(),
                        warningMessage = null,
                        isLoading = false
                    )
                }
            }
        }
    }

}

sealed interface CrewRecordsAction {
    data class OnFilterItemSelected(val item: SelectableItem) : CrewRecordsAction
}

data class CrewRecordsState(
    val discipline: Discipline,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val crew: Crew,
    val leaders: List<Leader> = emptyList(),
    val records: List<TeamDisciplineRecord> = emptyList(),
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val disciplines: List<SelectableItem> = listOf(
        Discipline.Team.BOAT_RACE,
        Discipline.Team.QUIZ
    ),
)