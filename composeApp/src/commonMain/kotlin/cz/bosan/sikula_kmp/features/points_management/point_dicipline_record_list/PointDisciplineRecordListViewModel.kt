package cz.bosan.sikula_kmp.features.points_management.point_dicipline_record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.presentation.colorToHex
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.utils.now

class PointDisciplineRecordListViewModel(
    private val leaderRepository: LeaderRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val campRepository: CampRepository,
    initDiscipline: Discipline,
    campDay: Int?
) : ViewModel() {

    private val _state = MutableStateFlow(
        PointDisciplineRecordListState(
            discipline = initDiscipline
        )
    )
    val state: StateFlow<PointDisciplineRecordListState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val campDayValue =
                if (campDay == 0 || campDay == null) leaderRepository.getCurrentCampDay() else campDay
            val todayCampDay = leaderRepository.getCurrentCampDay()
            val campDuration = leaderRepository.getCampDuration()
            _state.value =
                _state.value.copy(
                    currentLeader = currentLeader,
                    campDay = campDayValue,
                    todayCampDay = todayCampDay,
                    campDuration = campDuration,
                    enabledUpdatingRecords = (currentLeader.leader.role == Role.GAME_MASTER)
                )
            loadCampLeaders()
            loadCrews()
            loadRecords()
            setDayRecordsState()
            enabledCreatingRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: PointDisciplineRecordListAction) {
        when (action) {
            is PointDisciplineRecordListAction.OnChangeCampDay -> updateCampDay(action.campDay)

            is PointDisciplineRecordListAction.OnRecordUpdate ->
                updateRecord(action.record, action.newValue, action.newComment)


            PointDisciplineRecordListAction.OnSubmitRecords -> submitRecords()
            PointDisciplineRecordListAction.OnCrewsWithoutRecords -> setCrewsWithoutRecords()
        }
    }

    private fun updateCampDay(campDay: Int) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            _state.update {
                it.copy(campDay = campDay)
            }
            loadRecords()
            setDayRecordsState()
            enabledCreatingRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun setDayRecordsState() {
        var dayRecordsState = _state.value.dayRecordsState
        reviewInfoRepository.getDayReviewInfo(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { dayReviewInfo ->
            if (dayReviewInfo?.reviewed == true) {
                dayRecordsState = DayRecordsState.CHECKED_BY_GAME_MASTER
            } else {
                if (dayReviewInfo?.readyForReview == true) {
                    dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                } else {
                    val discipline = _state.value.discipline
                    if (discipline == Discipline.Team.CORRECTIONS || discipline == Discipline.Team.BONUSES) {
                        dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                    } else {
                        val records = _state.value.records
                        val allCrewsHaveRecords =
                            _state.value.crews.all { crew ->
                                records.any { record -> record.crewId == crew.id }
                            }
                        if (allCrewsHaveRecords) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            dayRecordsState = DayRecordsState.IN_PROGRESS
                        }
                    }
                }
            }
        }.onError {
            dayRecordsState = DayRecordsState.WITHOUT_STATE
        }
        _state.update {
            it.copy(dayRecordsState = dayRecordsState)
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

    private suspend fun loadRecords() {
        val crewMap = _state.value.crews.associateBy { it.id }
        teamDisciplineRepository.getTeamDisciplineRecordsDay(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { records ->
            val warningMessage =
                if (records.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
            _state.update {
                it.copy(
                    records = (records).sortedWith(compareBy { record -> crewMap[record.crewId]?.groupId }),
                    warningMessage = warningMessage,
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    errorMessage = error.toUiText(),
                    records = emptyList(),
                )
            }
        }
    }

    private fun enabledCreatingRecords() {
        val records = _state.value.records
        val role = _state.value.currentLeader.leader.role
        val dayState = _state.value.dayRecordsState
        val allCrewsHaveRecords =
            _state.value.crews.all { crew ->
                records.any { record -> record.crewId == crew.id }
            }
        val enabledCreatingRecords: Boolean
        val campDay = _state.value.campDay
        val todayCampDay = _state.value.todayCampDay
        if (!allCrewsHaveRecords && todayCampDay >= campDay && role == Role.GAME_MASTER && dayState != DayRecordsState.CHECKED_BY_GAME_MASTER) {
            enabledCreatingRecords = true
        } else {
            enabledCreatingRecords = false
        }
        _state.update {
            it.copy(enabledCreatingRecords = enabledCreatingRecords)
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

    private fun updateRecord(
        previousRecord: TeamDisciplineRecord,
        newValue: String?,
        newComment: String
    ) {
        viewModelScope.launch {
            val updatedRecord = TeamDisciplineRecord(
                id = previousRecord.id,
                crewId = previousRecord.crewId,
                campDay = previousRecord.campDay,
                value = newValue,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = newComment,
            )
            teamDisciplineRepository.updateTeamDisciplineRecord(
                record = updatedRecord,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id
            ).onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        records = emptyList()
                    )
                }
            }
            val newFilterRecords = _state.value.records.map {
                if (it.id == updatedRecord.id) updatedRecord else it
            }
            _state.update {
                it.copy(records = newFilterRecords)
            }
        }
    }

    private fun submitRecords() {
        viewModelScope.launch {
            reviewInfoRepository.submitRecordsPositionMaster(
                submit = true,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
            ).onSuccess {
                setDayRecordsState()
                reviewInfoRepository.submitRecordsCamp(
                    discipline = _state.value.discipline,
                    campId = _state.value.currentLeader.camp.id,
                    campDay = _state.value.campDay
                ).onSuccess {
                    setDayRecordsState()
                    enabledCreatingRecords()
                }.onError { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.toUiText(),
                        )
                    }
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private fun setCrewsWithoutRecords() {
        val recordedCrewsIds = _state.value.records.map { it.crewId }.toSet()

        val crewsWithoutRecord = _state.value.crews.filter { crew ->
            !recordedCrewsIds.contains(crew.id)
        }.map { crew ->
            CrewDto(
                id = crew.id,
                groupId = crew.groupId,
                name = crew.name,
                color = colorToHex(crew.color)
            )
        }.sortedBy { it.groupId }
        val crewsJson = Json.encodeToString(crewsWithoutRecord)

        _state.update {
            it.copy(crewsWithoutRecord = crewsJson)
        }
    }

}

sealed interface PointDisciplineRecordListAction {
    data class OnChangeCampDay(val campDay: Int) : PointDisciplineRecordListAction
    data class OnRecordUpdate(
        val record: TeamDisciplineRecord,
        val newValue: String?,
        val newComment: String
    ) :
        PointDisciplineRecordListAction

    data object OnSubmitRecords : PointDisciplineRecordListAction
    data object OnCrewsWithoutRecords : PointDisciplineRecordListAction
}

data class PointDisciplineRecordListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int = 1,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val dayRecordsState: DayRecordsState = DayRecordsState.WITHOUT_STATE,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val crews: List<Crew> = emptyList(),
    val crewsWithoutRecord: String? = null,
    val leaders: List<Leader> = emptyList(),
    val records: List<TeamDisciplineRecord> = emptyList(),
    val enabledCreatingRecords: Boolean? = null,
    val enabledUpdatingRecords: Boolean? = null,
)