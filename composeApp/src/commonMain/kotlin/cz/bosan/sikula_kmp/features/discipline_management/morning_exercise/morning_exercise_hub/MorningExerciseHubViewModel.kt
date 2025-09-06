package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.morning_exercise_hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DisciplineState
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MorningExerciseHubViewModel(
    private val leaderRepository: LeaderRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        MorningExerciseHubState()
    )
    val state: StateFlow<MorningExerciseHubState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    campDay = leaderRepository.getCurrentCampDay()
                )
            loadReviewInfos()
        }
    }

    fun onAction(action: MorningExerciseHubAction) {
        when (action) {
            MorningExerciseHubAction.OnLogoutClicked -> onLogoutClicked()
            MorningExerciseHubAction.ResetLogout -> resetLogoutSuccessful()
            is MorningExerciseHubAction.OnDisciplineClick -> {}
        }
    }

    private suspend fun loadReviewInfos() {
        val role = _state.value.currentLeader.leader.role
        if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
            loadGroupReviewInfos()
        } else if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
            loadCampReviewInfos()
        }
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun loadGroupReviewInfos() {
        val disciplines = _state.value.disciplines
        var disciplineStates: List<DisciplineState> = emptyList()
        val isServerResponder = serverRepository.isServerResponding()
        if (!isServerResponder) {
            for (discipline in disciplines) {
                disciplineStates =
                    disciplineStates + DisciplineState(discipline, DayRecordsState.OFFLINE)
            }
        } else {
            for (discipline in disciplines) {
                reviewInfoRepository.getCampReviewInfos(
                    discipline = discipline,
                    campId = _state.value.currentLeader.camp.id,
                ).onSuccess { infos ->
                    val infosUntilToday = infos.filter { it.campDay <= _state.value.campDay }
                    for (info in infosUntilToday) {
                        val groupReviewInfo =
                            info.groupReviewInfos.find { it.groupId == _state.value.currentLeader.leader.groupId }
                        if (groupReviewInfo?.readyForReview == false) {
                            disciplineStates = disciplineStates + DisciplineState(
                                discipline,
                                DayRecordsState.NON_CHECKED_BY_GROUP
                            )
                            return@onSuccess
                        }
                    }
                    val todayGroupReviewInfo =
                        infos.find { it.campDay == _state.value.campDay }?.groupReviewInfos?.find { it.groupId == _state.value.currentLeader.leader.groupId }
                    if (todayGroupReviewInfo?.readyForReview == true) {
                        disciplineStates = disciplineStates + DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GROUP
                        )
                    } else if (todayGroupReviewInfo == null) {
                        disciplineStates = disciplineStates + DisciplineState(
                            discipline,
                            DayRecordsState.WITHOUT_STATE
                        )
                    }
                }
            }
        }
        _state.update {
            it.copy(disciplineStates = disciplineStates)
        }
    }

    private suspend fun loadCampReviewInfos() {
        val disciplines = _state.value.disciplines
        var disciplineStates: List<DisciplineState> = emptyList()
        val isServerResponder = serverRepository.isServerResponding()
        if (!isServerResponder) {
            for (discipline in disciplines) {
                disciplineStates =
                    disciplineStates + DisciplineState(discipline, DayRecordsState.OFFLINE)
            }
        } else {
            for (discipline in disciplines) {
                reviewInfoRepository.getCampReviewInfos(
                    discipline = discipline,
                    campId = _state.value.currentLeader.camp.id,
                ).onSuccess { infos ->
                    val infosUntilToday = infos.filter { it.campDay <= _state.value.campDay }
                    for (info in infosUntilToday) {
                        if (!info.reviewed) {
                            if (!info.readyForReview) {
                                disciplineStates = disciplineStates + DisciplineState(
                                    discipline,
                                    DayRecordsState.NON_CHECKED_BY_GROUP
                                )
                                return@onSuccess
                            } else if(info.readyForReview){
                                disciplineStates = disciplineStates + DisciplineState(
                                    discipline,
                                    DayRecordsState.CHECKED_BY_GROUP
                                )
                                return@onSuccess
                            }
                        }
                    }
                    val todayReviewInfos = infos.find { it.campDay == _state.value.campDay }
                    if (todayReviewInfos == null) {
                        disciplineStates = disciplineStates + DisciplineState(
                            discipline,
                            DayRecordsState.WITHOUT_STATE
                        )
                    } else {
                        if (!todayReviewInfos.reviewed) {
                            disciplineStates = disciplineStates + DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GROUP
                            )
                        } else {
                            disciplineStates = disciplineStates + DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GAME_MASTER
                            )
                        }
                    }
                }
            }
        }
        _state.update {
            it.copy(disciplineStates = disciplineStates)
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

sealed interface MorningExerciseHubAction {
    data object OnLogoutClicked : MorningExerciseHubAction
    data object ResetLogout : MorningExerciseHubAction
    data class OnDisciplineClick(val discipline: Discipline) : MorningExerciseHubAction
}

data class MorningExerciseHubState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int = 1,
    val disciplineStates: List<DisciplineState> = emptyList(),
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val disciplines: List<Discipline> = listOf(
        Discipline.Individual.GRENADES,
        Discipline.Individual.ROPE_CLIMBING,
        Discipline.Individual.PULL_UPS,
        Discipline.Individual.TRAIL,
        Discipline.Individual.TIDYING,
    )
)