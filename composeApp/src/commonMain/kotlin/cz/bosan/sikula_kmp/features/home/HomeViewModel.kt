package cz.bosan.sikula_kmp.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list.BadgesListAction
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DisciplineState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.morningExerciseDisciplines
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsRepository
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import cz.bosan.sikula_kmp.managers.user_manager.AttendeesCount
import cz.bosan.sikula_kmp.managers.user_manager.BirthdayUser
import cz.bosan.sikula_kmp.managers.user_manager.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val leaderRepository: LeaderRepository,
    private val campRepository: CampRepository,
    private val pointsRepository: PointsRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val userRepository: UserRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState()
    )
    val state: StateFlow<HomeState> = _state

    init {
        viewModelScope.launch {
            val campDay = leaderRepository.getCurrentCampDay()
            val selectedPointDay =
                if (campDay == 1) 1 else leaderRepository.getCurrentCampDay() - 1 //points from previous day
            _state.update {
                it.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    campDay = campDay,
                    selectedPointDay = selectedPointDay,
                    campDuration = leaderRepository.getCampDuration()
                )
            }
            loadCrews()
            loadPointRecords()
            loadDisciplines()
            loadReviewInfos()
            getBirthdayUsers()
            getAttendeesCount()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnLogoutClicked -> onLogoutClicked()
            HomeAction.ResetLogout -> resetLogoutSuccessful()
            is HomeAction.OnChangePointDay -> updatePointDay(action.pointDay)
        }
    }

    private fun updatePointDay(pointDay: Int) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            _state.update {
                it.copy(selectedPointDay = pointDay)
            }
            loadPointRecords()
            _state.update { it.copy(isLoading = false) }
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

    private suspend fun loadPointRecords() {
        pointsRepository.getTotalPoints(
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.selectedPointDay
        ).onSuccess { records ->
            _state.update {
                it.copy(
                    pointRecords = records
                )
            }
        }.onError { error ->
            if (error == DataError.Remote.SERVER) {
                _state.update {
                    it.copy(
                        warningMessage = Warning.Common.EMPTY_LIST.toUiText(),
                        errorMessage = null,
                        pointRecords = emptyList()
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        warningMessage = null,
                        pointRecords = emptyList()
                    )
                }
            }
        }
    }

    private fun loadDisciplines() {
        val role = _state.value.currentLeader.leader.role
        val positions = _state.value.currentLeader.leader.positions
        var disciplines: List<Discipline> = emptyList()
        if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
            disciplines = disciplines + listOf(
                Discipline.Team.QUIZ,
                Discipline.Individual.NEGATIVE_POINTS,
                Discipline.Individual.MORSE,
                Discipline.Team.BOAT_RACE,
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
        if (positions.contains(Position.BOAT_RACE_MASTER)) {
            disciplines = (disciplines + Discipline.Team.BOAT_RACE).distinct()
        }
        _state.update {
            it.copy(positionDisciplines = disciplines)
        }
    }

    private suspend fun loadReviewInfos() {
        val role = _state.value.currentLeader.leader.role
        val disciplineStates: MutableMap<DisciplineState, Int> = mutableMapOf()
        if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
            disciplineStates += loadGroupReviewInfosMorningExerciseDisciplines()
        } else if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
            disciplineStates += loadCampReviewInfosMorningExerciseDisciplines()
            disciplineStates += loadReviewInfosPointsDisciplines()
        }
        disciplineStates += loadReviewInfosPositionDisciplines()
        val filteredDisciplineStates = if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
            disciplineStates.filter { record -> (record.key.dayRecordsState == DayRecordsState.NON_CHECKED_BY_GROUP) || (record.key.dayRecordsState == DayRecordsState.CHECKED_BY_GROUP) }
        } else {
            disciplineStates.filter { it.key.dayRecordsState == DayRecordsState.NON_CHECKED_BY_GROUP }
        }
        _state.update {
            it.copy(
                disciplineStates = filteredDisciplineStates,
            )
        }
    }

    private suspend fun loadGroupReviewInfosMorningExerciseDisciplines(): Map<DisciplineState, Int> {
        val disciplines = morningExerciseDisciplines
        val disciplineStates: MutableMap<DisciplineState, Int> = mutableMapOf()
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
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.NON_CHECKED_BY_GROUP
                        )
                        disciplineStates[state] = info.campDay
                        return@onSuccess
                    }
                }
                val todayGroupReviewInfo =
                    infos.find { it.campDay == _state.value.campDay }?.groupReviewInfos?.find { it.groupId == _state.value.currentLeader.leader.groupId }
                if (todayGroupReviewInfo?.readyForReview == true) {
                    val state = DisciplineState(
                        discipline,
                        DayRecordsState.CHECKED_BY_GROUP
                    )
                    disciplineStates[state] = _state.value.campDay
                } else if (todayGroupReviewInfo == null) {
                    val state = DisciplineState(
                        discipline,
                        DayRecordsState.WITHOUT_STATE
                    )
                    disciplineStates[state] = _state.value.campDay
                }
            }
        }
        return disciplineStates
    }

    private suspend fun loadCampReviewInfosMorningExerciseDisciplines(): Map<DisciplineState, Int> {
        val disciplines = morningExerciseDisciplines
        val disciplineStates: MutableMap<DisciplineState, Int> = mutableMapOf()
        for (discipline in disciplines) {
            reviewInfoRepository.getCampReviewInfos(
                discipline = discipline,
                campId = _state.value.currentLeader.camp.id,
            ).onSuccess { infos ->
                val infosUntilToday = infos.filter { it.campDay <= _state.value.campDay }
                for (info in infosUntilToday) {
                    if (!info.reviewed) {
                        if (!info.readyForReview) {
                            val state = DisciplineState(
                                discipline,
                                DayRecordsState.NON_CHECKED_BY_GROUP
                            )
                            disciplineStates[state] = info.campDay
                            return@onSuccess
                        } else if (info.readyForReview) {
                            val state = DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GROUP
                            )
                            disciplineStates[state] = info.campDay
                            return@onSuccess
                        }
                    }
                }
                val todayReviewInfos = infos.find { it.campDay == _state.value.campDay }
                if (todayReviewInfos == null) {
                    val state = DisciplineState(
                        discipline,
                        DayRecordsState.WITHOUT_STATE
                    )
                    disciplineStates[state] = _state.value.campDay
                } else {
                    if (!todayReviewInfos.reviewed) {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GROUP
                        )
                        disciplineStates[state] = _state.value.campDay
                    } else {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GAME_MASTER
                        )
                        disciplineStates[state] = _state.value.campDay
                    }
                }
            }
        }
        return disciplineStates
    }


    private suspend fun loadReviewInfosPositionDisciplines(): Map<DisciplineState, Int> {
        val disciplines: List<Discipline> = _state.value.positionDisciplines
        val disciplineStates: MutableMap<DisciplineState, Int> = mutableMapOf()
        val role = _state.value.currentLeader.leader.role
        val campDuration = leaderRepository.getCampDuration()
        for (discipline in disciplines) {
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
                                            val state = DisciplineState(
                                                discipline,
                                                DayRecordsState.CHECKED_BY_GROUP
                                            )
                                            disciplineStates[state] = info.campDay
                                            return@onSuccess
                                        } else {
                                            val state = DisciplineState(
                                                discipline,
                                                DayRecordsState.NON_CHECKED_BY_GROUP
                                            )
                                            disciplineStates[state] = info.campDay
                                            return@onSuccess
                                        }
                                    }
                                }
                            } else {
                                if (discipline == Discipline.Individual.NEGATIVE_POINTS && _state.value.campDay == info.campDay && _state.value.campDay != campDuration) {
                                    val state = DisciplineState(
                                        discipline,
                                        DayRecordsState.WITHOUT_STATE
                                    )
                                    disciplineStates[state] = info.campDay
                                    return@onSuccess
                                } else {
                                    val state = DisciplineState(
                                        discipline,
                                        DayRecordsState.NON_CHECKED_BY_GROUP
                                    )
                                    disciplineStates[state] = info.campDay
                                    return@onSuccess
                                }
                            }
                        } else if (info.readyForReview) {
                            if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
                                val state = DisciplineState(
                                    discipline,
                                    DayRecordsState.CHECKED_BY_GROUP
                                )
                                disciplineStates[state] = info.campDay
                                return@onSuccess
                            }
                        }
                    }
                }
                val todayReviewInfos = infos.find { it.campDay == _state.value.campDay }
                if (todayReviewInfos == null) {
                    val state = DisciplineState(
                        discipline,
                        DayRecordsState.WITHOUT_STATE
                    )
                    disciplineStates[state] = _state.value.campDay
                } else {
                    if (!todayReviewInfos.reviewed) {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GROUP
                        )
                        disciplineStates[state] = _state.value.campDay
                    } else {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GAME_MASTER
                        )
                        disciplineStates[state] = _state.value.campDay
                    }
                }
            }
        }
        return disciplineStates
    }

    private suspend fun getBirthdayUsers() {
        val birthdayUsers: MutableList<BirthdayUser> = mutableListOf()
        for (i in 0..2) {
            userRepository.getBirthdays(
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay + i
            ).onSuccess { users ->
                users.forEach {
                    birthdayUsers += BirthdayUser(
                        user = it,
                        campDay = _state.value.campDay + i
                    )
                }
                _state.update {
                    it.copy(
                        birthdayErrorMessage = null,
                        birthDayUsers = birthdayUsers
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        birthdayErrorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private suspend fun getAttendeesCount() {
        userRepository.getAttendeesCount(
            campId = _state.value.currentLeader.camp.id
        ).onSuccess { result ->
            _state.update {
                it.copy(
                    attendeesCount = result,
                    attendeesCountErrorMessage = null
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    attendeesCount = null,
                    attendeesCountErrorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadReviewInfosPointsDisciplines(): Map<DisciplineState, Int> {
        val disciplines: List<Discipline> =
            listOf(Discipline.Team.THEME_GAME, Discipline.Team.BONUSES, Discipline.Team.CORRECTIONS)
        val disciplineStates: MutableMap<DisciplineState, Int> = mutableMapOf()
        for (discipline in disciplines) {
            reviewInfoRepository.getCampReviewInfos(
                discipline = discipline,
                campId = _state.value.currentLeader.camp.id,
            ).onSuccess { infos ->
                val infosUntilToday = infos.filter { it.campDay <= _state.value.campDay }
                for (info in infosUntilToday) {
                    if (!info.reviewed) {
                        if (!info.readyForReview) {
                            if (discipline == Discipline.Team.THEME_GAME) {
                                val allCrewsHaveRecords = haveAllCrewsData(campDay = info.campDay)
                                if (allCrewsHaveRecords) {
                                    val state = DisciplineState(
                                        discipline,
                                        DayRecordsState.CHECKED_BY_GROUP
                                    )
                                    disciplineStates[state] = info.campDay
                                } else {
                                    val state = DisciplineState(
                                        discipline,
                                        DayRecordsState.NON_CHECKED_BY_GROUP
                                    )
                                    disciplineStates[state] = info.campDay
                                }
                            } else {
                                val state = DisciplineState(
                                    discipline,
                                    DayRecordsState.CHECKED_BY_GROUP
                                )
                                disciplineStates[state] = info.campDay
                            }
                            return@onSuccess
                        } else if (info.readyForReview) {
                            val state = DisciplineState(
                                discipline,
                                DayRecordsState.CHECKED_BY_GROUP
                            )
                            disciplineStates[state] = info.campDay
                            return@onSuccess
                        }
                    }
                }
                val todayReviewInfos = infos.find { it.campDay == _state.value.campDay }
                if (todayReviewInfos == null) {
                    val state = DisciplineState(
                        discipline,
                        DayRecordsState.WITHOUT_STATE
                    )
                    disciplineStates[state] = _state.value.campDay
                } else {
                    if (!todayReviewInfos.reviewed) {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GROUP
                        )
                        disciplineStates[state] = _state.value.campDay
                    } else {
                        val state = DisciplineState(
                            discipline,
                            DayRecordsState.CHECKED_BY_GAME_MASTER
                        )
                        disciplineStates[state] = _state.value.campDay
                    }
                }
            }
        }
        return disciplineStates
    }

    private suspend fun haveAllCrewsData(campDay: Int): Boolean {
        var allCrewsHaveRecords = false
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

sealed interface HomeAction {
    data object OnLogoutClicked : HomeAction
    data object ResetLogout : HomeAction
    data class OnChangePointDay(val pointDay: Int) : HomeAction
}

data class HomeState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int = 1,
    val selectedPointDay: Int = 1,
    val campDuration: Int = 21,
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val birthdayErrorMessage: UiText? = null,
    val attendeesCountErrorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val crews: List<Crew> = emptyList(),
    val pointRecords: List<PointRecord> = emptyList(),
    val positionDisciplines: List<Discipline> = emptyList(),
    val disciplineStates: Map<DisciplineState, Int> = emptyMap(),
    val birthDayUsers: List<BirthdayUser> = emptyList(),
    val attendeesCount: AttendeesCount? = null
)