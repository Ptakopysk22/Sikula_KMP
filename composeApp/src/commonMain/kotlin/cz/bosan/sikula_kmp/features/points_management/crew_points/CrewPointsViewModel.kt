package cz.bosan.sikula_kmp.features.points_management.crew_points

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
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.pointDisciplines
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsRepository
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrewPointsViewModel(
    private val leaderRepository: LeaderRepository,
    private val pointsRepository: PointsRepository,
    crewDto: CrewDto,
    campDay: Int,
) : ViewModel() {

    private val _state =
        MutableStateFlow(CrewPointsState(crew = crewDto.toCrew(), campDay = campDay))
    val state: StateFlow<CrewPointsState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val todayCampDay = leaderRepository.getCurrentCampDay()
            val campDuration = leaderRepository.getCampDuration()

            _state.update {
                it.copy(
                    currentLeader = currentLeader,
                    todayCampDay = todayCampDay,
                    campDuration = campDuration,
                    isInitialized = true,
                    isLoading = false,
                )
            }
            loadRecords(campId = currentLeader.camp.id)
        }
    }

    fun onAction(action: CrewPointsAction) {
        when (action) {
            is CrewPointsAction.OnChangeCampDay ->
                viewModelScope.launch {
                    updateCampDay(action.campDay)
                }
        }
    }

    private suspend fun updateCampDay(campDay: Int) {
        _state.update { it.copy(isLoading = true) }
        _state.update {
            it.copy(campDay = campDay)
        }
        loadRecords(campId = _state.value.currentLeader.camp.id)
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun loadRecords(campId: Int) {
        pointsRepository.getAllPoints(
            campId = campId,
            campDay = _state.value.campDay,
            crewId = _state.value.crew.id
        ).onSuccess { records ->
            var warningMessage: UiText? = null
            if (records.isEmpty()) {
                warningMessage = Warning.Common.EMPTY_LIST.toUiText()
            } else {
                val sortedAndCompletedRecords = _state.value.disciplines.map { discipline ->
                    records.find { it.disciplineId == discipline.id }
                        ?: PointRecord(
                            crewId = _state.value.crew.id,
                            disciplineId = discipline.id,
                            description = "",
                            campDay = _state.value.campDay,
                            value = null
                        )
                }
                _state.update {
                    it.copy(
                        records = sortedAndCompletedRecords,
                        warningMessage = warningMessage
                    )
                }
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    records = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }
}

sealed interface CrewPointsAction {
    data class OnChangeCampDay(val campDay: Int) : CrewPointsAction
}

data class CrewPointsState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val crew: Crew,
    val campDay: Int,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val records: List<PointRecord> = emptyList(),
    val isInitialized: Boolean = false,
    val disciplines: List<Discipline> = pointDisciplines
)