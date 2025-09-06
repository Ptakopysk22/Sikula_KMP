package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.position_discipline_hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DisciplineState
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PositionDisciplinesHubViewModel(
    private val leaderRepository: LeaderRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        PositionDisciplineHubState()
    )
    val state: StateFlow<PositionDisciplineHubState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    campDay = leaderRepository.getCurrentCampDay()
                )
            }
            loadDisciplines()
            val disciplineStates = loadReviewInfos()
            _state.update {
                it.copy(
                    disciplineStates = disciplineStates,
                    isLoading = false
                )
            }

        }
    }

    fun onAction(action: PositionDisciplinesHubAction) {
        when (action) {
            PositionDisciplinesHubAction.OnLogoutClicked -> onLogoutClicked()
            PositionDisciplinesHubAction.ResetLogout -> resetLogoutSuccessful()
            is PositionDisciplinesHubAction.OnDisciplineClick -> {}
        }
    }

    private fun loadDisciplines() {
        val role = _state.value.currentLeader.leader.role
        val positions = _state.value.currentLeader.leader.positions
        var disciplines: List<Discipline> = emptyList()
        disciplines = (disciplines + Discipline.Team.BOAT_RACE)
        if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
            disciplines = disciplines + listOf(
                Discipline.Team.QUIZ,
                Discipline.Individual.NEGATIVE_POINTS,
                Discipline.Individual.MORSE,
                Discipline.Badges.BADGES,
            )
        }
        if (positions.contains(Position.QUIZ_MASTER)) {
            disciplines = (disciplines + Discipline.Team.QUIZ).distinct()
        }
        if (positions.contains(Position.NEGATIVE_POINTS_MASTER)) {
            disciplines = (disciplines + Discipline.Individual.NEGATIVE_POINTS).distinct()
        }
        if (positions.contains(Position.MORSE_MASTER)) {
            disciplines = (disciplines + Discipline.Individual.MORSE).distinct()
        }
        if (positions.contains(Position.BADGES_MASTER)) {
            disciplines = (disciplines + Discipline.Badges.BADGES).distinct()
        }
        if (role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER || role == Role.DIRECTOR || role == Role.GAME_MASTER) {
            disciplines = disciplines + listOf(
                Discipline.Individual.AGILITY,
                Discipline.Individual.NIGHT_GAME,
                Discipline.Individual.SWIMMING_RACE,
                Discipline.Individual.TRIP,
            )
        }
        _state.update {
            it.copy(disciplines = disciplines)
        }
    }

    private suspend fun loadReviewInfos(): List<DisciplineState> {
        val disciplinesWithoutState = listOf(
            Discipline.Badges.BADGES,
            Discipline.Individual.SWIMMING_RACE,
            Discipline.Individual.TRIP,
            Discipline.Individual.NIGHT_GAME,
            Discipline.Individual.AGILITY
        )
        var disciplinesWithState = listOf(
            Discipline.Team.QUIZ,
            Discipline.Individual.NEGATIVE_POINTS,
            Discipline.Individual.MORSE
        )
        val role = _state.value.currentLeader.leader.role
        val campDuration = leaderRepository.getCampDuration()
        if (role == Role.GAME_MASTER || role == Role.DIRECTOR || _state.value.currentLeader.leader.positions.contains(
                Position.BOAT_RACE_MASTER
            )
        ) {
            disciplinesWithState = disciplinesWithState + Discipline.Team.BOAT_RACE
        }

        val filteredDisciplines = disciplinesWithState.filter { it in _state.value.disciplines }
        var disciplineStates = mutableListOf<DisciplineState>()
        for (discipline in disciplinesWithoutState) {
            disciplineStates += DisciplineState(discipline, null)
        }

        val isServerResponder = serverRepository.isServerResponding()
        if (!isServerResponder) {
            for (discipline in filteredDisciplines) {
                disciplineStates += DisciplineState(discipline, DayRecordsState.OFFLINE)
            }
        } else {
            for (discipline in filteredDisciplines) {
                reviewInfoRepository.getCampReviewInfos(
                    discipline = discipline,
                    campId = _state.value.currentLeader.camp.id,
                ).onSuccess { infos ->
                    val infosUntilToday = infos.filter { it.campDay <= _state.value.campDay }
                    for (info in infosUntilToday) {
                        if (!info.reviewed) {
                            if (!info.readyForReview) {
                                if (role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) {
                                    for (groupReviewInfo in info.groupReviewInfos) {
                                        if (!groupReviewInfo.readyForReview) {
                                            if (discipline == Discipline.Individual.NEGATIVE_POINTS && _state.value.campDay == info.campDay && _state.value.campDay != campDuration) {
                                                disciplineStates += DisciplineState(
                                                    discipline,
                                                    DayRecordsState.CHECKED_BY_GROUP
                                                )
                                                return@onSuccess
                                            } else {
                                                disciplineStates += DisciplineState(
                                                    discipline,
                                                    DayRecordsState.NON_CHECKED_BY_GROUP
                                                )
                                                return@onSuccess
                                            }
                                        }
                                    }
                                } else {
                                    if (discipline == Discipline.Individual.NEGATIVE_POINTS && _state.value.campDay == info.campDay && _state.value.campDay != campDuration) {
                                        disciplineStates += DisciplineState(
                                            discipline,
                                            DayRecordsState.WITHOUT_STATE
                                        )
                                        return@onSuccess
                                    } else {
                                        disciplineStates += DisciplineState(
                                            discipline,
                                            DayRecordsState.NON_CHECKED_BY_GROUP
                                        )
                                        return@onSuccess
                                    }
                                }
                            } else if (info.readyForReview) {
                                if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
                                    disciplineStates += DisciplineState(
                                        discipline,
                                        DayRecordsState.CHECKED_BY_GROUP
                                    )
                                    return@onSuccess
                                }
                            }
                        }
                    }
                    val todayReviewInfos = infos.find { it.campDay == _state.value.campDay }
                    if (todayReviewInfos == null) {
                        disciplineStates += DisciplineState(
                            discipline,
                            DayRecordsState.WITHOUT_STATE
                        )
                    } else {
                        if (!todayReviewInfos.reviewed) {
                            disciplineStates += DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GROUP
                            )
                        } else {
                            disciplineStates += DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GAME_MASTER
                            )
                        }
                    }
                }
            }
        }
        return disciplineStates
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

sealed interface PositionDisciplinesHubAction {
    data object OnLogoutClicked : PositionDisciplinesHubAction
    data object ResetLogout : PositionDisciplinesHubAction
    data class OnDisciplineClick(val discipline: Discipline) : PositionDisciplinesHubAction
}

data class PositionDisciplineHubState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val disciplines: List<Discipline> = emptyList(),
    val campDay: Int = 1,
    val disciplineStates: List<DisciplineState> = emptyList(),
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
)
