package cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Info
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

class CountRecordingViewModel(
    private val leaderRepository: LeaderRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val serverRepository: ServerRepository,
    initDiscipline: Discipline,
    children: List<Child>,
    campDay: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CountRecordingState(
            discipline = initDiscipline,
            children = children,
            campDay = campDay,
        )
    )
    val state: StateFlow<CountRecordingState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    isServerResponding = serverRepository.isServerResponding(),
                    isLoading = false,
                )
        }
    }

    fun onAction(action: CountRecordingAction) {
        when (action) {
            is CountRecordingAction.OnFillRecord -> createLastFillRecord(
                value = action.value,
                child = action.child,
                comment = action.comment
            )

            is CountRecordingAction.OnUpdateLastRecord -> updateLastFillRecord(action.value)
            CountRecordingAction.OnShowInfoChange -> {
                _state.update { it.copy(showInfo = !_state.value.showInfo) }
            }
        }
    }

    private fun createLastFillRecord(value: String?, child: Child, comment: String) {
        viewModelScope.launch {
            var idOnServer: Int? = null
            var successfullySavedLocally = false
            if (_state.value.lastFillRecord != null) {
                if (_state.value.isServerResponding == true) {
                    individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                        record = _state.value.lastFillRecord!!,
                        campId = _state.value.currentLeader.camp.id,
                        discipline = _state.value.discipline
                    ).onSuccess { record ->
                        idOnServer = record.id
                        _state.update {
                            it.copy(lastFillRecord = _state.value.lastFillRecord!!.copy(isUploaded = true))
                        }
                    }.onError {
                        _state.update {
                            it.copy(lastFillRecord = _state.value.lastFillRecord!!.copy(isUploaded = false))
                        }
                    }
                }

                individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                    idOnServer = idOnServer,
                    record = _state.value.lastFillRecord!!,
                    campId = _state.value.currentLeader.camp.id,
                    discipline = _state.value.discipline
                ).onSuccess {
                    successfullySavedLocally = true
                }
            }
            _state.update {
                val savedSomewhere = idOnServer != null || successfullySavedLocally
                if (savedSomewhere || _state.value.firstRecord) {
                    it.copy(
                        lastFillRecord = IndividualDisciplineRecord(
                            id = null,
                            competitorId = child.id,
                            campDay = _state.value.campDay,
                            value = value?.replace(",", "."),
                            timeStamp = LocalDateTime.now(),
                            refereeId = _state.value.currentLeader.leader.id,
                            comment = comment,
                            countsForImprovement = null,
                            isUploaded = false,
                            improvement = null,
                            isRecord = null,
                        ),
                        lastFillChild = child,
                        children = _state.value.children - child,
                    )
                } else {
                    it.copy(warningMessage = Warning.Common.RESULTS_NOT_SAVING.toUiText())
                }
            }
            if (_state.value.firstRecord) {
                _state.update {
                    it.copy(firstRecord = false)
                }
            }
        }
    }

    private fun updateLastFillRecord(value: String?) {
        _state.update {
            it.copy(
                lastFillRecord = IndividualDisciplineRecord(
                    id = null,
                    competitorId = _state.value.lastFillChild?.id!!,
                    campDay = _state.value.campDay,
                    value = value,
                    timeStamp = LocalDateTime.now(),
                    refereeId = _state.value.currentLeader.leader.id,
                    comment = "",
                    countsForImprovement = null,
                    isUploaded = false,
                    improvement = null,
                    isRecord = null,
                ),
            )
        }
    }
}

sealed interface CountRecordingAction {
    data class OnFillRecord(val value: String?, val child: Child, val comment: String) :
        CountRecordingAction

    data class OnUpdateLastRecord(val value: String?) : CountRecordingAction
    data object OnShowInfoChange : CountRecordingAction
}

data class CountRecordingState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int,
    val isServerResponding: Boolean? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val infoMessage: UiText? = Info.Common.COUNT_RECODING.toUiText(),
    val warningMessage: UiText? = null,
    val children: List<Child>,
    val lastFillRecord: IndividualDisciplineRecord? = null,
    val lastFillChild: Child? = null,
    val showInfo: Boolean = false,
    val firstRecord: Boolean = true
)