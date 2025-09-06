package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_discipline_list.DailyDisciplineListAction
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

class NegativePointsAllRecordsViewModel(
    private val leaderRepository: LeaderRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val campRepository: CampRepository,
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        NegativePointsAllRecordsState()
    )
    val state: StateFlow<NegativePointsAllRecordsState> = _state

    private var campRecords: List<IndividualDisciplineRecord> = emptyList()

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            _state.update {
                it.copy(
                    currentLeader = currentLeader,
                    isPositionMaster = currentLeader.leader.positions.contains(Position.NEGATIVE_POINTS_MASTER)
                )
            }
            loadCampLeaders()
            loadCrews()
            loadChildren()
            loadRecords()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: NegativePointsAllRecordsAction) {
        when (action) {
            is NegativePointsAllRecordsAction.OnFilterItemSelected -> filterRecords(action.index)
            is NegativePointsAllRecordsAction.OnUpdateWorkedOff -> updateRecordWorkedOff(
                action.record,
                action.newWorkedOff
            )
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
        individualDisciplineRecordRepository.getIndividualDisciplineAllRecords(
            discipline = Discipline.Individual.NEGATIVE_POINTS,
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }
            campRecords = records.sortedWith(compareBy { record ->
                childMap[record.competitorId]?.nickName ?: ""
            })
            _state.update {
                it.copy(
                    warningMessage = if (campRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null,
                    filteredRecords = campRecords
                )
            }
        }.onError { error ->
            campRecords = emptyList()
            _state.update {
                it.copy(
                    filteredRecords = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun filterRecords(selectedButtonIndex: Int) {
        val filteredRecords = when (selectedButtonIndex) {
            0 -> campRecords
            1 -> campRecords.filter { it.workedOff == true }
            2 -> campRecords.filter { it.workedOff == false }
            else -> campRecords
        }
        val warningMessage =
            if (filteredRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
        _state.update {
            it.copy(
                selectedButtonIndex = selectedButtonIndex,
                filteredRecords = filteredRecords,
                warningMessage = warningMessage
            )
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

    private suspend fun loadChildren() {
        childRepository.getCampsChildren(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role,
        ).onSuccess { children ->
            _state.update {
                it.copy(
                    children = children,
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    children = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun updateRecordWorkedOff(
        previousRecord: IndividualDisciplineRecord,
        newWorkedOff: Boolean,
    ) {
        viewModelScope.launch {
            val updatedRecord = IndividualDisciplineRecord(
                id = previousRecord.id,
                competitorId = previousRecord.competitorId,
                campDay = previousRecord.campDay,
                value = previousRecord.value,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = previousRecord.comment,
                workedOff = newWorkedOff,
                isUploaded = true
            )
            individualDisciplineRecordRepository.updateIndividualRecordWorkedOff(
                record = updatedRecord,
                discipline = Discipline.Individual.NEGATIVE_POINTS,
                campId = _state.value.currentLeader.camp.id
            ).onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        filteredRecords = emptyList()
                    )
                }
            }
            val newFilterRecords = _state.value.filteredRecords.map {
                if (it.id == updatedRecord.id) updatedRecord else it
            }

            campRecords = campRecords.map {
                if (it.id == updatedRecord.id) updatedRecord else it
            }
            _state.update {
                it.copy(filteredRecords = newFilterRecords)
            }
        }
    }
}

sealed interface NegativePointsAllRecordsAction {
    data class OnFilterItemSelected(val index: Int) : NegativePointsAllRecordsAction
    data class OnUpdateWorkedOff(
        val record: IndividualDisciplineRecord,
        val newWorkedOff: Boolean,
    ) : NegativePointsAllRecordsAction
}

data class NegativePointsAllRecordsState(
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val selectedButtonIndex: Int = 0,
    val leaders: List<Leader> = emptyList(),
    val children: List<Child> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val records: List<IndividualDisciplineRecord> = emptyList(),
    val filteredRecords: List<IndividualDisciplineRecord> = emptyList(),
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val isPositionMaster: Boolean = false
)