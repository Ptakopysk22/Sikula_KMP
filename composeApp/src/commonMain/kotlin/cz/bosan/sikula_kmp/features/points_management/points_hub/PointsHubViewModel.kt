package cz.bosan.sikula_kmp.features.points_management.points_hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DisciplineState
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PointsHubViewModel(
    private val leaderRepository: LeaderRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val serverRepository: ServerRepository,
    private val campRepository: CampRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        PointsHubState()
    )
    val state: StateFlow<PointsHubState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    campDay = leaderRepository.getCurrentCampDay(),
                )
            loadCrews()
            loadReviewInfos()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: PointsHubAction) {
        when (action) {
            PointsHubAction.OnLogoutClicked -> onLogoutClicked()
            PointsHubAction.ResetLogout -> resetLogoutSuccessful()
            is PointsHubAction.OnDisciplineClick -> {}
        }
    }

    private suspend fun loadReviewInfos() {
        val disciplines: List<Discipline> = _state.value.disciplines - Discipline.Team.ALL
        val disciplineStates: MutableList<DisciplineState> = mutableListOf()
        val isServerResponder = serverRepository.isServerResponding()
        if (!isServerResponder) {
            for (discipline in disciplines) {
                disciplineStates += DisciplineState(discipline, DayRecordsState.OFFLINE)
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
                                if(discipline == Discipline.Team.THEME_GAME){
                                    val allCrewsHaveRecords = haveAllCrewsData(campDay = info.campDay)
                                    if(allCrewsHaveRecords){
                                        disciplineStates += DisciplineState(
                                            discipline,
                                            DayRecordsState.CHECKED_BY_GROUP
                                        )
                                    } else {
                                        disciplineStates += DisciplineState(
                                            discipline,
                                            DayRecordsState.NON_CHECKED_BY_GROUP
                                        )
                                    }
                                } else {
                                    disciplineStates += DisciplineState(
                                        discipline,
                                        DayRecordsState.CHECKED_BY_GROUP
                                    )
                                }
                                return@onSuccess
                            } else if (info.readyForReview) {
                                disciplineStates += DisciplineState(
                                    discipline,
                                    DayRecordsState.CHECKED_BY_GROUP
                                )
                                return@onSuccess
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
                                DayRecordsState.NON_CHECKED_BY_GROUP
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

    private suspend fun haveAllCrewsData(campDay: Int): Boolean {
        var allCrewsHaveRecords: Boolean = false
        teamDisciplineRepository.getTeamDisciplineRecordsDay(
            discipline = Discipline.Team.THEME_GAME,
            campId = _state.value.currentLeader.camp.id,
            campDay = campDay
        ).onSuccess { records ->
            allCrewsHaveRecords = _state.value.crews.all { crew ->
                records.any { record -> record.crewId == crew.id }
            }
        }.onError { error ->
            allCrewsHaveRecords = false
        }
        return allCrewsHaveRecords
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
                        crews = emptyList()
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

sealed interface PointsHubAction {
    data object OnLogoutClicked : PointsHubAction
    data object ResetLogout : PointsHubAction
    data class OnDisciplineClick(val discipline: Discipline) : PointsHubAction
}

data class PointsHubState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int = 1,
    val disciplineStates: List<DisciplineState> = emptyList(),
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val crews: List<Crew> = emptyList(),
    val disciplines: List<Discipline> = listOf(
        Discipline.Team.ALL,
        Discipline.Team.THEME_GAME,
        Discipline.Team.BONUSES,
        Discipline.Team.CORRECTIONS,
    )
)