package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_record_list

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
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.ImprovementsAndRecords
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.utils.now

class BoatRaceRecordListViewModel(
    private val leaderRepository: LeaderRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    private val serverRepository: ServerRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val campRepository: CampRepository,
    campDay: Int?,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BoatRaceRecordListState()
    )
    val state: StateFlow<BoatRaceRecordListState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val isBoatRaceMaster =
                currentLeader.leader.positions.any { it == Position.BOAT_RACE_MASTER }

            _state.value =
                _state.value.copy(
                    dayRecordsState = if (serverRepository.isServerResponding()) DayRecordsState.WITHOUT_STATE else DayRecordsState.OFFLINE,
                    isCurrentLeaderBoatRaceMaster = isBoatRaceMaster,
                    currentLeader = currentLeader,
                    campDay = if (campDay == 0 || campDay == null) leaderRepository.getCurrentCampDay() else campDay,
                    todayCampDay = leaderRepository.getCurrentCampDay(),
                    campDuration = leaderRepository.getCampDuration(),
                )
            viewModelScope.launch {
                state.map { it.currentLeader }
                    .distinctUntilChanged()
                    .collectLatest {
                        loadCrews()
                        loadCampLeaders()
                        setDayRecordsState()
                        enabledCreatingRecords()
                        enabledUpdateRecords()
                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
            }
        }
    }

    fun onAction(action: BoatRaceRecordListAction) {
        when (action) {
            is BoatRaceRecordListAction.OnChangeCampDay -> updateCampDay(action.campDay)
            BoatRaceRecordListAction.OnCrewsWithoutRecords -> setCrewsWithoutRecords()
            is BoatRaceRecordListAction.OnRecordUpdate ->
                if (_state.value.enabledUpdatingRecords == true) {
                    updateRecord(action.record, action.newValue, action.newComment)
                }

            BoatRaceRecordListAction.OnSubmitRecords -> submitRecordsByBoatMaster()
            BoatRaceRecordListAction.OnSubmitRecordsByGameMaster -> submitRecordsByGameMaster()
            is BoatRaceRecordListAction.OnUpdateCountsForImprovement ->
                if (_state.value.enabledUpdatingRecords == true) {
                    updateCountsForImprovement(
                        action.record,
                        action.newValue
                    )
                }
        }
    }


    private fun updateCampDay(campDay: Int) {
        viewModelScope.launch {
            _state.update {
                it.copy(campDay = campDay, errorMessage = null)
            }
            setDayRecordsState()
            enabledCreatingRecords()
            enabledUpdateRecords()
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private suspend fun setDayRecordsState() {
        var dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        val crews = _state.value.crews
        val isServerStillResponding: Boolean
        if (dayRecordsState == DayRecordsState.OFFLINE) {
            isServerStillResponding = false
        } else {
            isServerStillResponding = serverRepository.isServerResponding()
        }
        if (dayRecordsState == DayRecordsState.OFFLINE || !isServerStillResponding) {
            dayRecordsState = DayRecordsState.OFFLINE
            _state.update {
                it.copy(dayRecordsState = dayRecordsState)
            }
            loadDayRecords()
        } else {
            reviewInfoRepository.getDayReviewInfo(
                discipline = Discipline.Team.BOAT_RACE,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
            ).onSuccess { dayReviewInfo ->
                if (dayReviewInfo?.reviewed == true) {
                    dayRecordsState = DayRecordsState.CHECKED_BY_GAME_MASTER
                    loadDayRecords()
                } else {
                    loadDayRecords()
                    if (isBoatMaster) {
                        if (dayReviewInfo?.readyForReview == true) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            val records = _state.value.records
                            if (dayReviewInfo?.readyForReview == null && records.isEmpty()) {
                                dayRecordsState = DayRecordsState.WITHOUT_STATE
                            } else {
                                val isSubmitByPositionMaster =
                                    dayReviewInfo?.groupReviewInfos?.all { it.readyForReview } == true
                                if (isSubmitByPositionMaster) {
                                    dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                                } else {
                                    val nonSynchronizeRecords =
                                        records.filter { it.isUploaded == false }
                                    val allCrewsHaveRecords =
                                        crews.all { crew ->
                                            records.any { record -> record.crewId == crew.id }
                                        }
                                    if (allCrewsHaveRecords) {
                                        if (nonSynchronizeRecords.isNotEmpty()) {
                                            dayRecordsState = DayRecordsState.NON_SYNCHRONIZE
                                        } else {
                                            dayRecordsState = DayRecordsState.NON_CHECKED_BY_GROUP
                                        }
                                    } else {
                                        dayRecordsState = DayRecordsState.IN_PROGRESS
                                    }
                                }
                            }
                        }
                    } else if (role == Role.DIRECTOR || role == Role.GAME_MASTER || role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) {
                        if (dayReviewInfo?.readyForReview == true) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            dayRecordsState = DayRecordsState.IN_PROGRESS
                        }
                    }
                }
            }.onError {
                if (isBoatMaster) {
                    loadDayRecords()
                    val records = _state.value.records
                    val nonSynchronizeRecords =
                        records.filter { it.isUploaded == false }
                    val allCrewsHaveRecords =
                        crews.all { crew ->
                            records.any { record -> record.crewId == crew.id }
                        }
                    if (records.isEmpty()) {
                        dayRecordsState = DayRecordsState.WITHOUT_STATE
                    } else {
                        if (allCrewsHaveRecords) {
                            if (nonSynchronizeRecords.isNotEmpty()) {
                                dayRecordsState = DayRecordsState.NON_SYNCHRONIZE
                            } else {
                                dayRecordsState = DayRecordsState.NON_CHECKED_BY_GROUP
                            }
                        } else {
                            dayRecordsState = DayRecordsState.IN_PROGRESS
                        }
                    }
                } else {
                    loadDayRecords()
                    dayRecordsState = DayRecordsState.WITHOUT_STATE
                }
            }
        }
        _state.update {
            it.copy(dayRecordsState = dayRecordsState)
        }
    }

    private fun enabledCreatingRecords() {
        val dayRecordsState = _state.value.dayRecordsState
        val records = _state.value.records
        val allCrewsHaveRecords =
            _state.value.crews.all { crew ->
                records.any { record -> record.crewId == crew.id }
            }
        val enabledCreatingRecords: Boolean
        val campDay = _state.value.campDay
        val todayCampDay = _state.value.todayCampDay
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        if (isBoatMaster) {
            if (dayRecordsState == DayRecordsState.IN_PROGRESS) {
                enabledCreatingRecords = true
            } else if (dayRecordsState == DayRecordsState.WITHOUT_STATE && todayCampDay == campDay
            ) {
                enabledCreatingRecords = true
            } else if (dayRecordsState == DayRecordsState.OFFLINE && campDay == todayCampDay
            ) {
                if (allCrewsHaveRecords) {
                    enabledCreatingRecords = false
                } else {
                    enabledCreatingRecords = true
                }
            } else {
                enabledCreatingRecords = false
            }
        } else {
            enabledCreatingRecords = false
        }
        _state.update {
            it.copy(enabledCreatingRecords = enabledCreatingRecords)
        }
    }

    private fun enabledUpdateRecords() {
        val enabledUpdatingRecords: Boolean
        val dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        if (role == Role.GAME_MASTER) {
            enabledUpdatingRecords = true
        } else if (isBoatMaster) {
            if (dayRecordsState == DayRecordsState.OFFLINE || dayRecordsState == DayRecordsState.IN_PROGRESS || dayRecordsState == DayRecordsState.NON_SYNCHRONIZE || dayRecordsState == DayRecordsState.NON_CHECKED_BY_GROUP) {
                enabledUpdatingRecords = true
            } else {
                enabledUpdatingRecords = false
            }
        } else {
            enabledUpdatingRecords = false
        }
        _state.update {
            it.copy(enabledUpdatingRecords = enabledUpdatingRecords)
        }
    }

    private suspend fun loadCampLeaders() {
        val dayRecordsState = _state.value.dayRecordsState
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        if (dayRecordsState == DayRecordsState.OFFLINE && isBoatMaster) {
            val localLeaders = leaderRepository.getCampsLeadersLocally(
                campId = _state.value.currentLeader.camp.id,
            )
            _state.update {
                it.copy(
                    leaders = localLeaders
                )
            }
        } else {
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
    }

    private suspend fun loadDayRecords() {
        val dayRecordsState = _state.value.dayRecordsState
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        val crewMap = _state.value.crews.associateBy { it.id }
        if (dayRecordsState == DayRecordsState.OFFLINE && isBoatMaster) {
            val localRecords = getDayRecordsLocally()
            val warningMessage =
                if (localRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
            _state.update {
                it.copy(
                    records = localRecords.sortedWith(compareBy { record -> crewMap[record.crewId]?.groupId }),
                    warningMessage = warningMessage,
                )
            }
        } else {
            val localRecords = getDayRecordsLocally()
                .filter { it.isUploaded == false }
            teamDisciplineRepository.getTeamDisciplineRecordsDay(
                discipline = Discipline.Team.BOAT_RACE,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
            ).onSuccess { records ->
                val filteredLocalRecords = localRecords.filterNot { localRecord ->
                    records.any { it.crewId == localRecord.crewId }
                }
                val warningMessage =
                    if ((records + filteredLocalRecords).isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
                _state.update {
                    it.copy(
                        records = (records + filteredLocalRecords).sortedWith(compareBy { record -> crewMap[record.crewId]?.groupId }),
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
    }

    private suspend fun getDayRecordsLocally(): List<TeamDisciplineRecord> {
        var localRecords: List<TeamDisciplineRecord> = emptyList()
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        if (isBoatMaster) {
            teamDisciplineRepository.getTeamRecordsLocally(
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay,
                discipline = Discipline.Team.BOAT_RACE,
            ).onSuccess { result ->
                localRecords = result
            }.onError { }
        }

        return localRecords
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

    private suspend fun loadCrews() {
        val dayRecordsState = _state.value.dayRecordsState
        val isBoatMaster = _state.value.isCurrentLeaderBoatRaceMaster
        if (dayRecordsState == DayRecordsState.OFFLINE && isBoatMaster) {
            campRepository.getCrewsLocal(
                campId = _state.value.currentLeader.camp.id,
            ).onSuccess { localCrews ->
                _state.update {
                    it.copy(
                        crews = localCrews,
                    )
                }
            }
        } else {
            campRepository.getCrews(
                campId = _state.value.currentLeader.camp.id,
                groupId = null,
                isBoatRaceMaster = _state.value.isCurrentLeaderBoatRaceMaster
            )
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
    }

    private fun updateRecord(
        previousRecord: TeamDisciplineRecord,
        newValue: String?,
        newComment: String
    ) {
        viewModelScope.launch {
            val isStillResponding = serverRepository.isServerResponding()
            val dayRecordsState: DayRecordsState
            if (!isStillResponding) {
                dayRecordsState = DayRecordsState.OFFLINE
            } else {
                dayRecordsState = _state.value.dayRecordsState
            }
            var updatedRecord = TeamDisciplineRecord.EMPTY
            if (previousRecord.isUploaded == false || (previousRecord.isUploaded == true && dayRecordsState == DayRecordsState.OFFLINE)) {
                updatedRecord = TeamDisciplineRecord(
                    id = previousRecord.id,
                    crewId = previousRecord.crewId,
                    campDay = previousRecord.campDay,
                    value = newValue,
                    timeStamp = LocalDateTime.now(),
                    refereeId = _state.value.currentLeader.leader.id,
                    comment = newComment,
                    improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = previousRecord.improvementsAndRecords?.countsForImprovements),
                    isUploaded = false,
                )
                teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
                    record = updatedRecord,
                    discipline = Discipline.Team.BOAT_RACE,
                    campId = _state.value.currentLeader.camp.id,
                    idOnServer = null
                )
            } else {
                if (dayRecordsState != DayRecordsState.OFFLINE) {
                    var isUploaded: Boolean? = null
                    var improvement: String? = null
                    var isRecord: Boolean? = null
                    updatedRecord = TeamDisciplineRecord(
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
                        discipline = Discipline.Team.BOAT_RACE,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        improvement = updatedRecord.improvementsAndRecords?.improvementString
                        isRecord = updatedRecord.improvementsAndRecords?.isRecord
                        isUploaded = true
                    }.onError { error ->
                        isUploaded = false
                        _state.update {
                            it.copy(
                                errorMessage = error.toUiText(),
                                records = emptyList()
                            )
                        }
                    }
                    updatedRecord = TeamDisciplineRecord(
                        id = previousRecord.id,
                        crewId = previousRecord.crewId,
                        campDay = previousRecord.campDay,
                        value = newValue,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = newComment,
                        improvementsAndRecords = ImprovementsAndRecords(
                            countsForImprovements = previousRecord.improvementsAndRecords?.countsForImprovements,
                            improvementString = improvement,
                            isRecord = isRecord
                        ),
                        isUploaded = isUploaded,
                    )
                    teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
                        record = updatedRecord,
                        discipline = Discipline.Team.BOAT_RACE,
                        campId = _state.value.currentLeader.camp.id,
                        idOnServer = updatedRecord.id
                    )
                }
            }
            val newRecords = _state.value.records.map {
                if (it.id == updatedRecord.id) updatedRecord else it
            }
            _state.update {
                it.copy(records = newRecords)
            }
        }
    }

    private fun updateCountsForImprovement(
        previousRecord: TeamDisciplineRecord,
        newValue: Boolean
    ) {
        viewModelScope.launch {
            val isStillResponding = serverRepository.isServerResponding()
            val dayRecordsState: DayRecordsState
            if (!isStillResponding) {
                dayRecordsState = DayRecordsState.OFFLINE
            } else {
                dayRecordsState = _state.value.dayRecordsState
            }
            var updatedRecord = TeamDisciplineRecord.EMPTY
            if (previousRecord.isUploaded == false || (previousRecord.isUploaded == true && dayRecordsState == DayRecordsState.OFFLINE)) {
                updatedRecord = TeamDisciplineRecord(
                    id = previousRecord.id,
                    crewId = previousRecord.crewId,
                    campDay = previousRecord.campDay,
                    value = previousRecord.value,
                    timeStamp = LocalDateTime.now(),
                    refereeId = _state.value.currentLeader.leader.id,
                    comment = previousRecord.comment,
                    improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = newValue),
                    isUploaded = false,
                )
                teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
                    record = updatedRecord,
                    discipline = Discipline.Team.BOAT_RACE,
                    campId = _state.value.currentLeader.camp.id,
                    idOnServer = null
                )
            } else {
                if (dayRecordsState != DayRecordsState.OFFLINE) {
                    var isUploaded: Boolean? = null
                    var improvement: String? = null
                    var isRecord: Boolean? = null
                    updatedRecord = TeamDisciplineRecord(
                        id = previousRecord.id,
                        crewId = previousRecord.crewId,
                        campDay = previousRecord.campDay,
                        value = previousRecord.value,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = previousRecord.comment,
                        improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = newValue),
                    )
                    teamDisciplineRepository.updateTeamRecordCountsForImprovement(
                        record = updatedRecord,
                        discipline = Discipline.Team.BOAT_RACE,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        improvement = updatedRecord.improvementsAndRecords?.improvementString
                        isRecord = updatedRecord.improvementsAndRecords?.isRecord
                        isUploaded = true
                    }.onError { error ->
                        isUploaded = false
                        _state.update {
                            it.copy(
                                errorMessage = error.toUiText(),
                                records = emptyList()
                            )
                        }
                    }
                    updatedRecord = TeamDisciplineRecord(
                        id = previousRecord.id,
                        crewId = previousRecord.crewId,
                        campDay = previousRecord.campDay,
                        value = previousRecord.value,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = previousRecord.comment,
                        improvementsAndRecords = ImprovementsAndRecords(
                            countsForImprovements = newValue,
                            improvementString = improvement,
                            isRecord = isRecord
                        ),
                        isUploaded = isUploaded,
                    )
                    teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
                        record = updatedRecord,
                        discipline = Discipline.Team.BOAT_RACE,
                        campId = _state.value.currentLeader.camp.id,
                        idOnServer = updatedRecord.id
                    )
                }
            }
            val newRecords = _state.value.records.map {
                if (it.id == updatedRecord.id) updatedRecord else it
            }
            _state.update {
                it.copy(records = newRecords)
            }
        }
    }

    private suspend fun synchronizeRemainingRecords(): Boolean {
        val nonSynchronizeRecords = _state.value.records.filter { it.isUploaded == false }
        var successFullySynchronize = true
        for (record in nonSynchronizeRecords) {
            teamDisciplineRepository.createTeamDisciplineRecord(
                record = record,
                discipline = Discipline.Team.BOAT_RACE,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess { newRecord ->
                teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
                    idOnServer = newRecord.id,
                    record = TeamDisciplineRecord(
                        id = record.id,
                        crewId = record.crewId,
                        campDay = record.campDay,
                        value = record.value,
                        timeStamp = record.timeStamp,
                        refereeId = record.refereeId,
                        comment = record.comment,
                        improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = record.improvementsAndRecords?.countsForImprovements),
                        isUploaded = true,
                    ),
                    campId = _state.value.currentLeader.camp.id,
                    discipline = Discipline.Team.BOAT_RACE
                )
                loadDayRecords()
            }.onError { successFullySynchronize = false }
        }
        return successFullySynchronize
    }

    private fun submitRecordsByBoatMaster() {
        viewModelScope.launch {
            var successFullySynchronize = true
            if (state.value.dayRecordsState == DayRecordsState.NON_SYNCHRONIZE) {
                successFullySynchronize = synchronizeRemainingRecords()
            }
            if (successFullySynchronize) {
                reviewInfoRepository.submitTeamRecordsByPositionMaster(
                    discipline = Discipline.Team.BOAT_RACE,
                    campId = _state.value.currentLeader.camp.id,
                    campDay = _state.value.campDay,
                    submit = true
                ).onSuccess {
                    setDayRecordsState()
                    enabledUpdateRecords()
                }.onError { error ->
                    _state.update {
                        it.copy(errorMessage = error.toUiText())
                    }
                }
            }
        }
    }

    private fun submitRecordsByGameMaster() {
        viewModelScope.launch {
            reviewInfoRepository.submitRecordsCamp(
                discipline = Discipline.Team.BOAT_RACE,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
            ).onSuccess {
                setDayRecordsState()
            }.onError { error ->
                _state.update {
                    it.copy(errorMessage = error.toUiText())
                }
            }
        }
    }
}

sealed interface BoatRaceRecordListAction {
    data object OnCrewsWithoutRecords : BoatRaceRecordListAction
    data class OnChangeCampDay(val campDay: Int) : BoatRaceRecordListAction
    data class OnRecordUpdate(
        val record: TeamDisciplineRecord,
        val newValue: String?,
        val newComment: String
    ) : BoatRaceRecordListAction

    data class OnUpdateCountsForImprovement(
        val record: TeamDisciplineRecord,
        val newValue: Boolean
    ) : BoatRaceRecordListAction

    data object OnSubmitRecords : BoatRaceRecordListAction
    data object OnSubmitRecordsByGameMaster : BoatRaceRecordListAction
}

data class BoatRaceRecordListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val isCurrentLeaderBoatRaceMaster: Boolean = false,
    val campDay: Int = 1,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val dayRecordsState: DayRecordsState = DayRecordsState.WITHOUT_STATE,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val leaders: List<Leader> = emptyList(),
    val crewsWithoutRecord: String? = null,
    val records: List<TeamDisciplineRecord> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val enabledCreatingRecords: Boolean? = null,
    val enabledUpdatingRecords: Boolean? = null,
)