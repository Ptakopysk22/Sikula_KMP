package cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Info
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

class CountRecordingTeamDisciplineViewModel(
    private val leaderRepository: LeaderRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    initDiscipline: Discipline,
    crews: List<CrewDto>,
    campDay: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CountRecordingTeamDisciplineState(
            discipline = initDiscipline,
            crews = crews.map { it.toCrew() },
            campDay = campDay,
        )
    )
    val state: StateFlow<CountRecordingTeamDisciplineState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    isLoading = false,
                )
        }
    }

    fun onAction(action: CountRecordingTeamDisciplineAction) {
        when (action) {
            is CountRecordingTeamDisciplineAction.OnFillRecord -> createLastFillRecord(
                value = action.value,
                crew = action.crew,
                comment = action.comment
            )

            is CountRecordingTeamDisciplineAction.OnUpdateLastRecord -> updateLastFillRecord(action.value)
            CountRecordingTeamDisciplineAction.OnShowInfoChange -> {
                _state.update { it.copy(showInfo = !_state.value.showInfo) }
            }
        }
    }

    private fun createLastFillRecord(value: String?, crew: Crew, comment: String) {
        viewModelScope.launch {
            if (_state.value.lastFillRecord != null) {
                    teamDisciplineRepository.createTeamDisciplineRecord(
                        record = _state.value.lastFillRecord!!,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = _state.value.discipline
                    ).onSuccess { record ->
                        _state.update {
                                it.copy(
                                    lastFillRecord = TeamDisciplineRecord(
                                        id = record.id,
                                        crewId = crew.id,
                                        campDay = _state.value.campDay,
                                        value = value,
                                        timeStamp = LocalDateTime.now(),
                                        refereeId = _state.value.currentLeader.leader.id,
                                        comment = comment,
                                        improvementsAndRecords = null,
                                        isUploaded = false,
                                    ),
                                    lastFillCrew = crew,
                                    crews = _state.value.crews - crew,
                                )

                        }
                    }.onError { error ->
                        _state.update {
                            it.copy(errorMessage = error.toUiText())
                        }
                    }
            }
            _state.update {
                it.copy(
                    lastFillRecord = TeamDisciplineRecord(
                        id = null,
                        crewId = crew.id,
                        campDay = _state.value.campDay,
                        value = value,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = comment,
                        improvementsAndRecords = null,
                        isUploaded = false,
                    ),
                    lastFillCrew = crew,
                    crews = _state.value.crews - crew,
                )

            }
        }
    }

    private fun updateLastFillRecord(value: String?) {
        _state.update {
            it.copy(
                lastFillRecord = _state.value.lastFillRecord?.copy(
                    value = value,
                    timeStamp = LocalDateTime.now(),
                    improvementsAndRecords = null,
                )
            )
        }
    }
}

sealed interface CountRecordingTeamDisciplineAction {
    data class OnFillRecord(val value: String?, val crew: Crew, val comment: String) :
        CountRecordingTeamDisciplineAction

    data class OnUpdateLastRecord(val value: String?) : CountRecordingTeamDisciplineAction
    data object OnShowInfoChange : CountRecordingTeamDisciplineAction
}

data class CountRecordingTeamDisciplineState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val infoMessage: UiText? = Info.Common.COUNT_RECODING_TEAM.toUiText(),
    val warningMessage: UiText? = null,
    val crews: List<Crew>,
    val lastFillRecord: TeamDisciplineRecord? = null,
    val lastFillCrew: Crew? = null,
    val showInfo: Boolean = false,
)