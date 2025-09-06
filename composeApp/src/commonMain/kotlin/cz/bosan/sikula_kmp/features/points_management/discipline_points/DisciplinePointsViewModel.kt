package cz.bosan.sikula_kmp.features.points_management.discipline_points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.pointDisciplines
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsRepository
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisciplinePointsViewModel(
    private val leaderRepository: LeaderRepository,
    private val campRepository: CampRepository,
    private val pointsRepository: PointsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DisciplinePointsState())
    val state: StateFlow<DisciplinePointsState> = _state

    private var allDayRecords: List<PointRecord> = emptyList()

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val campDay = leaderRepository.getCurrentCampDay()
            val todayCampDay = leaderRepository.getCurrentCampDay()
            val campDuration = leaderRepository.getCampDuration()
            _state.update{
                it.copy(
                    currentLeader = currentLeader,
                    campDay = campDay,
                    todayCampDay = todayCampDay,
                    campDuration = campDuration
                )
            }
            loadGroups()
            loadCrews()
            loadRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: DisciplinePointsAction) {
        when (action) {
            is DisciplinePointsAction.OnChangeCampDay -> updateCampDay(action.campDay)
            is DisciplinePointsAction.OnFilterItemSelected -> {
                _state.update {
                    it.copy(
                        selectedDiscipline = if (action.item is Discipline) action.item else _state.value.selectedDiscipline,
                    )
                }
                filterRecords()
            }
            DisciplinePointsAction.OnLogoutClicked -> onLogoutClicked()
            DisciplinePointsAction.ResetLogout -> resetLogoutSuccessful()
        }
    }

    private fun updateCampDay(campDay: Int) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            _state.update {
                it.copy(campDay = campDay)
            }
            loadRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadRecords() {
        pointsRepository.getAllPoints(
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay,
            crewId = null
        ).onSuccess { records ->
            allDayRecords = records
            _state.update {
                it.copy(records = records)
            }
            filterRecords()
        }.onError { error ->
            _state.update {
                it.copy(
                    records = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun filterRecords() {
        val disciplineId = _state.value.selectedDiscipline.id
        var filteredRecords = allDayRecords.filter { it.disciplineId == disciplineId }
        val warningMessage: UiText?

        if (filteredRecords.isEmpty()) {
            warningMessage = Warning.Common.EMPTY_LIST.toUiText()
        } else {
            warningMessage = null

            val allCrews = _state.value.crews
            val crewIdsWithRecord = filteredRecords.map { it.crewId }.toSet()

            val missingCrews = allCrews.filter { it.id !in crewIdsWithRecord }

            val missingRecords = missingCrews.map { crew ->
                PointRecord(
                    crewId = crew.id,
                    disciplineId = disciplineId,
                    description = "",
                    campDay = _state.value.campDay,
                    value = null
                )
            }

            filteredRecords = filteredRecords + missingRecords

        }
        _state.update {
            it.copy(
                records = filteredRecords.sortedBy { it.crewId },
                warningMessage = warningMessage,
            )
        }
    }

    private suspend fun loadGroups() {
        campRepository.getGroups(campId = _state.value.currentLeader.camp.id)
            .onSuccess { groups ->
                _state.update {
                    it.copy(
                        groups = groups
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                        groups = emptyList()
                    )
                }
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

sealed interface DisciplinePointsAction {
    data class OnChangeCampDay(val campDay: Int) : DisciplinePointsAction
    data class OnFilterItemSelected(val item: SelectableItem?) : DisciplinePointsAction
    data object OnLogoutClicked : DisciplinePointsAction
    data object ResetLogout : DisciplinePointsAction
}

data class DisciplinePointsState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val logoutSuccessful: Boolean = false,
    val campDay: Int = 1,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val records: List<PointRecord> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val groups: List<Group> = emptyList(),
    val selectedDiscipline: Discipline = Discipline.Team.ALL,
    val disciplines: List<Discipline> = pointDisciplines
)
