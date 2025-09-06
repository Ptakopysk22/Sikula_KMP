package cz.bosan.sikula_kmp.features.points_management.crew_discipline_points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsRepository
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrewDisciplinePointsViewModel(
    private val leaderRepository: LeaderRepository,
    private val pointsRepository: PointsRepository,
    initDiscipline: Discipline,
    crewDto: CrewDto,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CrewDisciplinePointsState(
            discipline = initDiscipline,
            crew = crewDto.toCrew(),
        )
    )
    val state: StateFlow<CrewDisciplinePointsState> = _state

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
                        loadRecords(it.camp.id)
                    }
            }
        }
    }

    fun onAction(action: CrewDisciplinePointsAction) {
        when (action) {
            else -> {}
        }
    }

    private suspend fun loadRecords(campId: Int) {
        val records: MutableList<PointRecord> = mutableListOf()

        if (_state.value.discipline == Discipline.Team.ALL) {
            pointsRepository.getAllPoints(
                campId = campId,
                campDay = null,
                crewId = _state.value.crew.id
            ).onSuccess { result ->
                records += result.filter { it.disciplineId == Discipline.Team.ALL.getId().toInt() }
            }.onError { error ->
                _state.update {
                    it.copy(
                        records = emptyList(),
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
        else if (_state.value.discipline == Discipline.Team.MORNING_EXERCISE) {
            pointsRepository.getMorningExercisePoints(
                campId = campId,
                campDay = null,
                crewId = _state.value.crew.id
            ).onSuccess { result ->
                records += result
            }.onError { error ->
                _state.update {
                    it.copy(
                        records = emptyList(),
                        errorMessage = error.toUiText(),
                    )
                }
            }
        } else if (_state.value.discipline == Discipline.Badges.BADGES) {
            pointsRepository.getBadgesPoints(
                campId = campId,
                campDay = null,
                crewId = _state.value.crew.id
            ).onSuccess { result ->
                records += result
            }.onError { error ->
                _state.update {
                    it.copy(
                        records = emptyList(),
                        errorMessage = error.toUiText(),
                    )
                }
            }
        } else {
            pointsRepository.getDisciplinePoints(
                disciplineId = _state.value.discipline.id,
                campId = campId,
                campDay = null,
                crewId = _state.value.crew.id
            ).onSuccess { result ->
                records += result
            }.onError { error ->
                _state.update {
                    it.copy(
                        records = emptyList(),
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }

        val warningMessage: UiText?
        if (records.isEmpty()) {
            warningMessage = Warning.Common.EMPTY_LIST.toUiText()
        } else {
            warningMessage = null
        }

        _state.update {
            it.copy(
                records = records,
                warningMessage = warningMessage,
                isLoading = false
            )
        }
    }

}

sealed interface CrewDisciplinePointsAction {
}

data class CrewDisciplinePointsState(
    val discipline: Discipline,
    val crew: Crew,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val records: List<PointRecord> = emptyList(),
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
)