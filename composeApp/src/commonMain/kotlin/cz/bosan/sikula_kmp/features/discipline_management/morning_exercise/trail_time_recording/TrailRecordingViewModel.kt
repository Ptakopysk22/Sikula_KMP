package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Info
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
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

class TrailRecordingViewModel(
    private val leaderRepository: LeaderRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val serverRepository: ServerRepository,
    children: List<Child>,
    campDay: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        TrailRecordingState(
            childrenOnStart = children,
            allChildren = children,
            countsForImprovementMap = children.associate { it.id to true },
            campDay = campDay,
        )
    )
    val state: StateFlow<TrailRecordingState> = _state

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

    fun onAction(action: TrailRecordingAction) {
        when (action) {
            is TrailRecordingAction.OnChildDismiss -> onChildDismiss(
                action.child,
                action.index,
                action.comment
            )

            is TrailRecordingAction.OnChildRelocate -> onChildRelocate(action.from, action.to)
            TrailRecordingAction.OnUpdateInitializingState -> updateInitializingState()
            is TrailRecordingAction.OnStartRecording -> onStartChildTimer(action.child)
            is TrailRecordingAction.OnStopRecording -> stopChildTimer(action.child)
            is TrailRecordingAction.OnChangeCountsForImprovementChild -> changeCountsForImprovementChild(
                action.childId,
                action.countsForImprovement
            )

            is TrailRecordingAction.OnChangeCountsForImprovementChildren -> changeCountsForImprovementChildren(
                action.countsForImprovement
            )

            is TrailRecordingAction.OnContinueChildTimer -> continueChildTimer(
                action.child,
                action.record
            )

            is TrailRecordingAction.OnRestartChild -> restartChild(action.child)
            TrailRecordingAction.OnShowInfoChange -> {
                _state.update { it.copy(showInfo = !_state.value.showInfo) }
            }
        }
    }

    private suspend fun loadTargetImprovements() {
        individualDisciplineRecordRepository.getIndividualDisciplineTargetImprovements(
            discipline = Discipline.Individual.TRAIL,
            campId = _state.value.currentLeader.camp.id,
            groupId = _state.value.currentLeader.leader.groupId!!,
            campDay = _state.value.campDay
        ).onSuccess { targetImprovements ->
            _state.update { it.copy(childTargetImprovements = targetImprovements) }
        }
    }

    private fun createRecord(value: String?, childTimer: ChildTimer) {
        viewModelScope.launch {
            var idOnServer: Int? = null
            var successfullySavedLocally = false
            var record = IndividualDisciplineRecord(
                id = null,
                competitorId = childTimer.child.id,
                campDay = _state.value.campDay,
                value = value,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = "",
                countsForImprovement = _state.value.countsForImprovementMap[childTimer.child.id],
                isUploaded = false,
            )
            if (_state.value.isServerResponding == true) {
                val isServerStillResponding: Boolean = serverRepository.isServerResponding()
                if (!isServerStillResponding) {
                    _state.update {
                        it.copy(isServerResponding = false)
                    }
                } else {
                    individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                        record = record,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = Discipline.Individual.TRAIL
                    ).onSuccess { result ->
                        idOnServer = result.id
                        record = record.copy(isUploaded = true, improvement = result.improvement)
                    }
                }
            }
            saveRecordLocally(
                idOnServer = idOnServer,
                record = record
            ).onSuccess { localId ->
                record = record.copy(id = localId)
                successfullySavedLocally = true
            }
            if (idOnServer != null || successfullySavedLocally) {
                _state.update {
                    it.copy(
                        finishedRecords = it.finishedRecords + (record to childTimer.startTime),
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
        childTimer: ChildTimer,
        finishedRecord: FinishedRecord
    ) {
        viewModelScope.launch {
            var successfullySavedLocally = false
            var successfullySavedRemotely = false
            var record = IndividualDisciplineRecord(
                id = finishedRecord.record.id,
                competitorId = finishedRecord.record.competitorId,
                campDay = finishedRecord.record.campDay,
                value = value,
                timeStamp = LocalDateTime.now(),
                refereeId = finishedRecord.record.refereeId,
                comment = finishedRecord.record.comment,
                countsForImprovement = finishedRecord.record.countsForImprovement,
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
                    individualDisciplineRecordRepository.updateIndividualRecord(
                        record = record,
                        discipline = Discipline.Individual.TRAIL,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        record = record.copy(improvement = updatedRecord.improvement)
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
                        finishedRecords = it.finishedRecords + (record to childTimer.startTime),
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
        record: IndividualDisciplineRecord
    ): Result<Int, DataError.Local> {
        return individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
            idOnServer = idOnServer,
            record = record,
            campId = _state.value.currentLeader.camp.id,
            discipline = Discipline.Individual.TRAIL
        )
    }

    private fun onStartChildTimer(child: Child) {
        val now = Clock.System.now()
        if (!_state.value.isCentralTimerRunning) {
            startCentralTimer(now)
        }
        _state.update {
            it.copy(
                childrenOnStart = it.childrenOnStart - child,
                childrenOnTrail = it.childrenOnTrail + ChildTimer(child, now)
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

    private fun stopChildTimer(child: Child) {
        val now = Clock.System.now()
        _state.update { state ->
            val timer = state.childrenOnTrail.find { it.child == child }
            if (timer != null) {
                val elapsed = (now - timer.startTime).inWholeSeconds
                val childRecord =
                    _state.value.finishedRecordsMemory.find { it.record.competitorId == child.id }
                if (childRecord == null) {
                    createRecord(value = elapsed.toInt().toString(), childTimer = timer)
                } else {
                    updateRecord(
                        value = elapsed.toInt().toString(),
                        childTimer = timer,
                        finishedRecord = childRecord
                    )
                }
                state.copy(
                    childrenOnTrail = state.childrenOnTrail - timer,
                )
            } else state
        }
        if (_state.value.isCentralTimerRunning && _state.value.childrenOnTrail.isEmpty()) {
            stopCentralTimer()
            _state.update {
                it.copy(isCentralTimerRunning = false)
            }
        }
    }

    private fun continueChildTimer(child: Child, record: IndividualDisciplineRecord) {
        val startTime = _state.value.finishedRecords[record]
        if (!_state.value.isCentralTimerRunning) {
            startCentralTimer()
        }
        _state.update {
            it.copy(
                finishedRecords = it.finishedRecords - (record),
                childrenOnTrail = listOf(ChildTimer(child, startTime!!)) + it.childrenOnTrail
            )
        }
    }

    private fun restartChild(child: Child) {
        _state.update {
            it.copy(
                childrenOnTrail = it.childrenOnTrail.filterNot { it.child.id == child.id },
                childrenOnStart = listOf(child) + it.childrenOnStart
            )
        }
        if (_state.value.isCentralTimerRunning && _state.value.childrenOnTrail.isEmpty()) {
            stopCentralTimer()
            _state.update {
                it.copy(isCentralTimerRunning = false)
            }
        }
    }

    private fun onChildDismiss(child: Child, index: Int, comment: String) {
        viewModelScope.launch {
            var idOnServer: Int? = null
            var successfullySavedLocally = false
            var record = IndividualDisciplineRecord(
                id = null,
                competitorId = child.id,
                campDay = _state.value.campDay,
                value = null,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = comment,
                countsForImprovement = true,
                isUploaded = false
            )
            if (_state.value.isServerResponding == true) {
                val isServerStillResponding: Boolean = serverRepository.isServerResponding()
                if (!isServerStillResponding) {
                    _state.update {
                        it.copy(isServerResponding = false)
                    }
                } else {
                    individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                        record = record,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = Discipline.Individual.TRAIL
                    ).onSuccess { result ->
                        idOnServer = result.id
                        record = record.copy(isUploaded = true, improvement = result.improvement)
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
                        childrenOnStart = _state.value.childrenOnStart.toMutableList()
                            .apply { removeAt(index) })
                }
            } else {
                _state.update {
                    it.copy(warningMessage = Warning.Common.RESULTS_NOT_SAVING.toUiText())
                }
            }
        }
    }

    private fun onChildRelocate(from: Int, to: Int) {
        _state.update {
            it.copy(childrenOnStart = _state.value.childrenOnStart.toMutableList().apply {
                add(to, removeAt(from))
            })
        }
    }

    private fun changeCountsForImprovementChild(childId: Int, countsForImprovement: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                countsForImprovementMap = currentState.countsForImprovementMap.toMutableMap()
                    .apply { put(childId, countsForImprovement) }
            )
        }
    }

    private fun changeCountsForImprovementChildren(countsForImprovement: Boolean) {
        _state.update {
            it.copy(countsForImprovementMap = if (countsForImprovement) it.childrenOnStart.associate { it.id to true } else it.childrenOnStart.associate { it.id to false })
        }
    }

    private fun updateInitializingState() {
        _state.update {
            it.copy(isChildrenInitialization = false)
        }
    }
}

sealed interface TrailRecordingAction {
    data class OnContinueChildTimer(val child: Child, val record: IndividualDisciplineRecord) :
        TrailRecordingAction

    data class OnChildDismiss(val child: Child, val index: Int, val comment: String) :
        TrailRecordingAction

    data class OnChildRelocate(val from: Int, val to: Int) : TrailRecordingAction
    data object OnUpdateInitializingState : TrailRecordingAction
    data class OnStartRecording(val child: Child) : TrailRecordingAction
    data class OnStopRecording(val child: Child) : TrailRecordingAction
    data class OnChangeCountsForImprovementChild(
        val childId: Int,
        val countsForImprovement: Boolean
    ) : TrailRecordingAction

    data class OnChangeCountsForImprovementChildren(val countsForImprovement: Boolean) :
        TrailRecordingAction

    data class OnRestartChild(val child: Child) : TrailRecordingAction
    data object OnShowInfoChange : TrailRecordingAction
}

data class TrailRecordingState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int,
    val isServerResponding: Boolean? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val infoMessage: UiText? = Info.Common.INITIALIZE_TRAIL_RECORDING.toUiText(),
    val childrenOnStart: List<Child>,
    val allChildren: List<Child>,
    val countsForImprovementMap: Map<Int, Boolean> = emptyMap(),
    val childrenOnTrail: List<ChildTimer> = emptyList(),
    val centralTime: Int = 0,
    val isCentralTimerRunning: Boolean = false,
    val finishedRecords: Map<IndividualDisciplineRecord, Instant> = emptyMap(),
    val finishedRecordsMemory: List<FinishedRecord> = emptyList(),
    val childTargetImprovements: List<TargetImprovement> = emptyList(),
    val isChildrenInitialization: Boolean = true,
    val showInfo: Boolean = false,
    val centralTimerStart: Instant? = null,
    val centralTimerDuration: Duration? = null
)

data class ChildTimer(
    val child: Child,
    val startTime: Instant
)

data class FinishedRecord(
    val record: IndividualDisciplineRecord,
    val serverIdRecord: Int?,
)