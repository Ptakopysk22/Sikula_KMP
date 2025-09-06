package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Info
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.ImprovementsAndRecords
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import kotlin.time.Duration

class BoatRaceRecordingViewModel(
    private val leaderRepository: LeaderRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    private val serverRepository: ServerRepository,
    crews: List<CrewDto>,
    campDay: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BoatRaceRecordingState(
            crews = crews.map { it.toCrew() },
            countsForImprovementMap = crews.associate { it.id to true },
            crewsLocks = crews.associate { it.id to false },
            campDay = campDay,
        )
    )
    val state: StateFlow<BoatRaceRecordingState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    isServerResponding = serverRepository.isServerResponding(),
                    isLoading = false,
                )
            if (_state.value.isServerResponding == true) {
                loadTargetImprovements()
            }
        }
    }

    fun onAction(action: BoatRaceRecordingAction) {
        when (action) {
            is BoatRaceRecordingAction.OnCrewDismiss -> onCrewDismiss(
                action.crew,
                action.index,
                action.comment
            )

            is BoatRaceRecordingAction.OnCrewRelocate -> relocateCrew(action.newList)
            BoatRaceRecordingAction.OnUpdateInitializingState -> updateInitializingState()
            is BoatRaceRecordingAction.OnStartRecording -> onStartCrewTimer(action.crew)
            is BoatRaceRecordingAction.OnStopRecording -> stopCrewTimer(action.crew)
            is BoatRaceRecordingAction.OnChangeCountsForImprovementCrew -> changeCountsForImprovementCrew(
                action.crewId,
                action.countsForImprovement
            )

            is BoatRaceRecordingAction.OnChangeCountsForImprovementCrews -> changeCountsForImprovementCrews(
                action.countsForImprovement
            )

            is BoatRaceRecordingAction.OnContinueCrewTimer -> continueCrewTimer(
                action.crew,
                action.record
            )

            is BoatRaceRecordingAction.OnRestartCrew -> restartCrew(action.crew)
            is BoatRaceRecordingAction.OnLockCrewChange -> changeLockCrew(action.crewId)
            BoatRaceRecordingAction.OnShowInfoChange -> {
                _state.update { it.copy(showInfo = !_state.value.showInfo) }
            }
        }
    }

    private suspend fun loadTargetImprovements() {
        teamDisciplineRepository.getTeamDisciplineTargetImprovements(
            discipline = Discipline.Team.BOAT_RACE,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { targetImprovements ->
            _state.update { it.copy(crewTargetImprovements = targetImprovements) }
        }
    }

    private fun createRecord(value: String?, crewTimer: CrewTimer) {
        viewModelScope.launch {
            var idOnServer: Int? = null
            var successfullySavedLocally = false
            var record = TeamDisciplineRecord(
                id = null,
                crewId = crewTimer.crew.id,
                campDay = _state.value.campDay,
                value = value,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = "",
                improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = _state.value.countsForImprovementMap[crewTimer.crew.id]),
                isUploaded = false,
            )
            if (_state.value.isServerResponding == true) {
                val isServerStillResponding: Boolean = serverRepository.isServerResponding()
                if (!isServerStillResponding) {
                    _state.update {
                        it.copy(isServerResponding = false)
                    }
                } else {
                    teamDisciplineRepository.createTeamDisciplineRecord(
                        record = record,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = Discipline.Team.BOAT_RACE
                    ).onSuccess { result ->
                        idOnServer = result.id
                        record = record.copy(
                            isUploaded = true,
                            improvementsAndRecords = ImprovementsAndRecords(
                                countsForImprovements = result.improvementsAndRecords?.countsForImprovements,
                                improvementString = result.improvementsAndRecords?.improvementString,
                                isRecord = result.improvementsAndRecords?.isRecord
                            )
                        )
                    }
                }
            }
            saveRecordLocally(
                idOnServer = idOnServer,
                record = record
            ).onSuccess { id ->
                record = record.copy(id = id)
                successfullySavedLocally = true
            }
            if (idOnServer != null || successfullySavedLocally) {
                _state.update {
                    it.copy(
                        finishedRecords = it.finishedRecords + (record to crewTimer.startTime),
                        finishedRecordsMemory = it.finishedRecordsMemory + FinishedRecord(
                            record = record,
                            serverIdRecord = idOnServer
                        )
                    )
                }
            } else {
                _state.update {
                    it.copy(warningMessage = Warning.Common.RESULTS_NOT_SAVING.toUiText())
                }
            }
        }
    }

    private fun updateRecord(
        value: String?,
        crewTimer: CrewTimer,
        finishedRecord: FinishedRecord
    ) {
        viewModelScope.launch {
            var successfullySavedLocally = false
            var successfullySavedRemotely = false
            var record = TeamDisciplineRecord(
                id = finishedRecord.record.id,
                crewId = finishedRecord.record.crewId,
                campDay = finishedRecord.record.campDay,
                value = value,
                timeStamp = LocalDateTime.now(),
                refereeId = finishedRecord.record.refereeId,
                comment = finishedRecord.record.comment,
                improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = finishedRecord.record.improvementsAndRecords?.countsForImprovements),
                isUploaded = finishedRecord.record.isUploaded
            )
            if (_state.value.isServerResponding == true) {
                val isServerStillResponding: Boolean = serverRepository.isServerResponding()
                if (!isServerStillResponding) {
                    _state.update {
                        it.copy(isServerResponding = false)
                    }
                } else {
                    record = record.copy(id = finishedRecord.serverIdRecord)
                    teamDisciplineRepository.updateTeamDisciplineRecord(
                        record = record,
                        discipline = Discipline.Team.BOAT_RACE,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        record = record.copy(
                            improvementsAndRecords = ImprovementsAndRecords(
                                improvementString = updatedRecord.improvementsAndRecords?.improvementString,
                                isRecord = updatedRecord.improvementsAndRecords?.isRecord
                            )
                        )
                        successfullySavedRemotely = true
                    }
                }
            }
            record = record.copy(id = finishedRecord.record.id)
            saveRecordLocally(
                idOnServer = finishedRecord.serverIdRecord,
                record = record
            ).onSuccess {
                successfullySavedLocally = true
            }
            if (successfullySavedRemotely || successfullySavedLocally) {
                val updatedFinishedRecordMemory = _state.value.finishedRecordsMemory.map {
                    if (it.record.id == record.id) FinishedRecord(
                        record = record,
                        serverIdRecord = finishedRecord.serverIdRecord
                    ) else it
                }
                _state.update {
                    it.copy(
                        finishedRecords = it.finishedRecords + (record to crewTimer.startTime),
                        finishedRecordsMemory = updatedFinishedRecordMemory
                    )
                }
            } else {
                _state.update {
                    it.copy(warningMessage = Warning.Common.RESULTS_NOT_SAVING.toUiText())
                }
            }
        }
    }

    private suspend fun saveRecordLocally(
        idOnServer: Int?,
        record: TeamDisciplineRecord
    ): Result<Int, DataError.Local> {
        return teamDisciplineRepository.insertOrUpdateTeamRecordLocally(
            idOnServer = idOnServer,
            record = record,
            campId = _state.value.currentLeader.camp.id,
            discipline = Discipline.Team.BOAT_RACE
        )
    }

    private fun onStartCrewTimer(crew: Crew) {
        val now = Clock.System.now()
        if (!_state.value.isCentralTimerRunning) {
            startCentralTimer(now)
        }
        _state.update {
            it.copy(
                crewsInRace = it.crewsInRace + CrewTimer(crew, now)
            )
        }
    }

    private fun startCentralTimer(now: Instant? = null) {
        _state.update {
            it.copy(
                isCentralTimerRunning = true,
                centralTimerStart = if (_state.value.centralTimerStart == null) now else _state.value.centralTimerStart
            )
        }
    }

    private fun stopCentralTimer() {
        _state.update {
            it.copy(
                isCentralTimerRunning = false,
                centralTimerDuration = Clock.System.now() - state.value.centralTimerStart!!
            )
        }
    }

    private fun stopCrewTimer(crew: Crew) {
        val now = Clock.System.now()
        _state.update { state ->
            val timer = state.crewsInRace.find { it.crew == crew }
            if (timer != null) {
                val elapsed = (now - timer.startTime).inWholeSeconds
                val crewRecord =
                    _state.value.finishedRecordsMemory.find { it.record.crewId == crew.id }
                if (crewRecord == null) {
                    createRecord(value = elapsed.toInt().toString(), crewTimer = timer)
                } else {
                    updateRecord(
                        value = elapsed.toInt().toString(),
                        crewTimer = timer,
                        finishedRecord = crewRecord
                    )
                }
                state.copy(crewsInRace = state.crewsInRace - timer)
            } else state
        }
        if (_state.value.isCentralTimerRunning && _state.value.crewsInRace.isEmpty()) {
            stopCentralTimer()
            _state.update {
                it.copy(isCentralTimerRunning = false)
            }
        }
    }

    private fun continueCrewTimer(crew: Crew, record: TeamDisciplineRecord) {
        val startTime = _state.value.finishedRecords[record]
        if (!_state.value.isCentralTimerRunning) {
            startCentralTimer()
        }
        _state.update {
            it.copy(
                finishedRecords = it.finishedRecords - (record),
                crewsInRace = listOf(CrewTimer(crew, startTime!!)) + it.crewsInRace
            )
        }
    }

    private fun restartCrew(crew: Crew) {
        _state.update {
            it.copy(
                crewsInRace = it.crewsInRace.filterNot { it.crew.id == crew.id },
            )
        }
        if (_state.value.isCentralTimerRunning && _state.value.crewsInRace.isEmpty()) {
            stopCentralTimer()
            _state.update {
                it.copy(isCentralTimerRunning = false)
            }
        }
    }

    private fun onCrewDismiss(crew: Crew, index: Int, comment: String) {
        viewModelScope.launch {
            var idOnServer: Int? = null
            var successfullySavedLocally = false
            var record = TeamDisciplineRecord(
                id = null,
                crewId = crew.id,
                campDay = _state.value.campDay,
                value = null,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = comment,
                improvementsAndRecords = ImprovementsAndRecords(countsForImprovements = true),
                isUploaded = false
            )
            if (_state.value.isServerResponding == true) {
                val isServerStillResponding: Boolean = serverRepository.isServerResponding()
                if (!isServerStillResponding) {
                    _state.update {
                        it.copy(isServerResponding = false)
                    }
                } else {
                    teamDisciplineRepository.createTeamDisciplineRecord(
                        record = record,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = Discipline.Team.BOAT_RACE
                    ).onSuccess { result ->
                        idOnServer = result.id
                        record = record.copy(
                            isUploaded = true,
                            improvementsAndRecords = ImprovementsAndRecords(improvementString = result.improvementsAndRecords?.improvementString)
                        )
                    }
                }
            }
            saveRecordLocally(
                idOnServer = idOnServer,
                record = record
            ).onSuccess {
                successfullySavedLocally = true
            }
            if (idOnServer != null && successfullySavedLocally) {
                _state.update {
                    it.copy(
                        crews = _state.value.crews.toMutableList()
                            .apply { removeAt(index) })
                }
            } else {
                _state.update {
                    it.copy(warningMessage = Warning.Common.RESULTS_NOT_SAVING.toUiText())
                }
            }

        }
    }

    private fun relocateCrew(newList: List<Crew>) {
        _state.update {
            it.copy(crews = newList)
        }
    }

    private fun changeLockCrew(crewId: Int) {
        _state.update { currentState ->
            val updatedLocks = currentState.crewsLocks.toMutableMap().apply {
                val current = this[crewId] ?: false
                this[crewId] = !current
            }
            currentState.copy(crewsLocks = updatedLocks)
        }
    }

    private fun changeCountsForImprovementCrew(crewId: Int, countsForImprovement: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                countsForImprovementMap = currentState.countsForImprovementMap.toMutableMap()
                    .apply { put(crewId, countsForImprovement) }
            )
        }
    }

    private fun changeCountsForImprovementCrews(countsForImprovement: Boolean) {
        _state.update {
            it.copy(countsForImprovementMap = if (countsForImprovement) it.crews.associate { it.id to true } else it.crews.associate { it.id to false })
        }
    }

    private fun updateInitializingState() {
        _state.update {
            it.copy(isCrewsInitialization = false)
        }
    }
}

sealed interface BoatRaceRecordingAction {
    data class OnContinueCrewTimer(val crew: Crew, val record: TeamDisciplineRecord) :
        BoatRaceRecordingAction

    data class OnCrewDismiss(val crew: Crew, val index: Int, val comment: String) :
        BoatRaceRecordingAction

    data class OnCrewRelocate(val newList: List<Crew>) : BoatRaceRecordingAction
    data object OnUpdateInitializingState : BoatRaceRecordingAction
    data class OnStartRecording(val crew: Crew) : BoatRaceRecordingAction
    data class OnStopRecording(val crew: Crew) : BoatRaceRecordingAction
    data class OnChangeCountsForImprovementCrew(
        val crewId: Int,
        val countsForImprovement: Boolean
    ) : BoatRaceRecordingAction

    data class OnChangeCountsForImprovementCrews(val countsForImprovement: Boolean) :
        BoatRaceRecordingAction

    data class OnRestartCrew(val crew: Crew) : BoatRaceRecordingAction
    data class OnLockCrewChange(val crewId: Int) : BoatRaceRecordingAction
    data object OnShowInfoChange : BoatRaceRecordingAction
}

data class BoatRaceRecordingState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int,
    val isServerResponding: Boolean? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val infoMessage: UiText? = Info.Common.INITIALIZE_BOAT_RACE_RECORDING.toUiText(),
    val crews: List<Crew>,
    val crewsLocks: Map<Int, Boolean>,
    val countsForImprovementMap: Map<Int, Boolean> = emptyMap(),
    val crewsInRace: List<CrewTimer> = emptyList(),
    val centralTime: Int = 0,
    val isCentralTimerRunning: Boolean = false,
    val finishedRecords: Map<TeamDisciplineRecord, Instant> = emptyMap(),
    val finishedRecordsMemory: List<FinishedRecord> = emptyList(),
    val crewTargetImprovements: List<TargetImprovement> = emptyList(),
    val isCrewsInitialization: Boolean = true,
    val showInfo: Boolean = false,
    val centralTimerStart: Instant? = null,
    val centralTimerDuration: Duration? = null
)

data class CrewTimer(
    val crew: Crew,
    val startTime: Instant
)

data class FinishedRecord(
    val record: TeamDisciplineRecord,
    val serverIdRecord: Int?,
)