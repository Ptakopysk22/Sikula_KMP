package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.SelectableChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.AgilityQuest
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.AgilityQuests
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getMatchDiscipline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

class DualOptionListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val campRepository: CampRepository,
    initDiscipline: Discipline,
) : ViewModel() {

    private val _state = MutableStateFlow(
        DualOptionListState(
            discipline = initDiscipline,
        )
    )
    val state: StateFlow<DualOptionListState> = _state

    private var nonFilterRecords: List<IndividualDisciplineRecord> = emptyList()

    init {
        viewModelScope.launch {
            val leader = leaderRepository.getCurrentLeaderLocal()
            val campDay = leaderRepository.getCurrentCampDay()
            _state.update {
                it.copy(currentLeader = leader, campDay = campDay)
            }

            loadChildren()
            loadTrailCategories()
            loadCampLeaders()
            val role = leader.leader.role
            if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
                loadGroups()
                loadCrews()
            }
            loadRecordsCamp()
            enabledUpdateRecords()

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: DualOptionListAction) {
        when (action) {
            is DualOptionListAction.OnCriteriaSelected -> {
                _state.update {
                    it.copy(
                        selectedFilterCriteria = action.filterCriteria,
                        filteredRecords = nonFilterRecords,
                        selectedGroup = null,
                        selectedTrailCategory = null,
                    )
                }
                filterRecords(
                    selectedItem = null,
                    selectedAgilityItem = _state.value.selectedAgilityItem,
                    records = nonFilterRecords
                )
            }

            is DualOptionListAction.OnFilterItemSelected -> {
                filterRecords(
                    selectedItem = action.item,
                    selectedAgilityItem = _state.value.selectedAgilityItem,
                    records = nonFilterRecords
                )
            }

            is DualOptionListAction.OnRecordUpdate ->
                if (_state.value.enabledUpdatingRecords == true) {
                    updateRecord(action.record, action.newValue, action.newComment)
                }

            is DualOptionListAction.OnRecordCreate -> {
                createRecord(
                    value = action.value,
                    competitorId = action.competitorId,
                    quest = action.quest
                )
            }

            is DualOptionListAction.OnChangeAgilityFilterCriteria -> {
                _state.update { it.copy(selectedAgilityFilterCriteria = action.newCriteria) }
            }

            is DualOptionListAction.OnChangeAgilitySelectedItem -> {
                filterRecords(
                    selectedItem = _state.value.selectedGroup ?: _state.value.selectedTrailCategory,
                    selectedAgilityItem = action.newItem,
                    records = nonFilterRecords
                )
                _state.update {
                    it.copy(selectedAgilityItem = action.newItem)
                }
            }
        }
    }

    private fun enabledUpdateRecords() {
        val enabledUpdatingRecords: Boolean
        val role = _state.value.currentLeader.leader.role
        val discipline = _state.value.discipline
        var matchedDisciplineByPosition = emptyList<Discipline>()
        for (position in _state.value.currentLeader.leader.positions) {
            matchedDisciplineByPosition = matchedDisciplineByPosition + getMatchDiscipline(position)
        }
        if (matchedDisciplineByPosition.contains(_state.value.discipline)) {
            enabledUpdatingRecords = true
        } else if ((role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) && (discipline == Discipline.Individual.SWIMMING_RACE || discipline == Discipline.Individual.TRIP || discipline == Discipline.Individual.NIGHT_GAME || discipline == Discipline.Individual.AGILITY)) {
            enabledUpdatingRecords = true

        } else {
            enabledUpdatingRecords = false
        }

        _state.update {
            it.copy(enabledUpdatingRecords = enabledUpdatingRecords)
        }
    }

    private fun filterRecords(
        selectedItem: SelectableItem?,
        selectedAgilityItem: SelectableItem,
        records: List<IndividualDisciplineRecord>
    ) {
        _state.update {
            it.copy(isLoading = true)
        }
        val children = _state.value.children

        var filteredRecords = when (selectedItem) {
            is Group -> {
                val groupChildIds = children.filter { it.groupId == selectedItem.id }.map { it.id }
                records.filter { it.competitorId in groupChildIds }
            }

            is TrailCategory -> {
                val trailCategoryChildIds =
                    children.filter { it.trailCategoryId == selectedItem.id }.map { it.id }
                records.filter { it.competitorId in trailCategoryChildIds }
            }

            null -> records
            else -> emptyList()
        }
        val warningMessage =
            if (filteredRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
        if (_state.value.discipline == Discipline.Individual.AGILITY) {
            filteredRecords = filterAgilityRecords(
                preFilterRecords = filteredRecords,
                selectedAgilityItem = selectedAgilityItem
            )
        }
        _state.update {
            it.copy(
                selectedGroup = if (selectedItem is Group) selectedItem else null,
                selectedTrailCategory = if (selectedItem is TrailCategory) selectedItem else null,
                filteredRecords = filteredRecords,
                warningMessage = warningMessage,
                isLoading = false
            )
        }
    }

    private fun filterAgilityRecords(
        preFilterRecords: List<IndividualDisciplineRecord>,
        selectedAgilityItem: SelectableItem,
    ): List<IndividualDisciplineRecord> {
        val filteredRecords = when (selectedAgilityItem) {
            is AgilityQuest -> {
                if (selectedAgilityItem.id != 0) {
                    preFilterRecords.filter { it.quest == selectedAgilityItem.id }
                } else {
                    preFilterRecords
                }
            }

            is SelectableChild -> {
                if (selectedAgilityItem.id != 0) {
                    preFilterRecords.filter { it.competitorId == selectedAgilityItem.id }
                } else {
                    preFilterRecords
                }
            }

            else -> emptyList()
        }
        return filteredRecords
    }

    private suspend fun loadChildren() {
        childRepository.getCampsChildren(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role,
            groupId = _state.value.currentLeader.leader.groupId,
            groupIdForSavingLocally = _state.value.currentLeader.leader.groupId
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
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadTrailCategories() {
        childRepository.getTrailCategories(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        ).onSuccess { categories ->
            _state.update {
                it.copy(
                    trailCategories = categories,
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    trailCategories = emptyList(),
                    errorMessage = error.toUiText(),
                    isLoading = false
                )
            }
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
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadRecordsCamp() {
        individualDisciplineRecordRepository.getIndividualDisciplineAllRecords(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }
            if (_state.value.discipline == Discipline.Individual.AGILITY) {
                val recordMap = records.associateBy { it.competitorId to it.quest }

                nonFilterRecords = _state.value.children.flatMap { child ->
                    (1..18).map { quest ->
                        recordMap[child.id to quest]
                            ?: IndividualDisciplineRecord.EMPTY.copy(
                                competitorId = child.id,
                                quest = quest
                            )
                    }
                }.sortedBy { childMap[it.competitorId]?.nickName?.lowercase() ?: "" }
            } else {
                val recordMap = records.associateBy { it.competitorId }

                nonFilterRecords = _state.value.children.map { child ->
                    recordMap[child.id]
                        ?: IndividualDisciplineRecord.EMPTY.copy(competitorId = child.id)
                }.sortedBy { childMap[it.competitorId]?.nickName ?: "" }
            }
            filterRecords(
                selectedItem = _state.value.selectedGroup,
                selectedAgilityItem = _state.value.selectedAgilityItem,
                records = nonFilterRecords
            )
        }.onError { error ->
            nonFilterRecords = emptyList()
            _state.update {
                it.copy(
                    filteredRecords = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadGroups() {
        campRepository.getGroups(campId = _state.value.currentLeader.camp.id).onSuccess { groups ->
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

    private fun createRecord(
        value: String?,
        competitorId: Int,
        quest: Int?
    ) {
        viewModelScope.launch {
            val newRecord = IndividualDisciplineRecord(
                id = null,
                competitorId = competitorId,
                campDay = _state.value.campDay,
                value = value,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = "",
                quest = quest
            )
            individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                record = newRecord,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess { createdRecord ->
                val newFilterRecords = _state.value.filteredRecords.map {
                    if (it.competitorId == createdRecord.competitorId && it.quest == createdRecord.quest) createdRecord else it
                }
                _state.update {
                    it.copy(filteredRecords = newFilterRecords)
                }
            }.onError { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.toUiText(),
                            filteredRecords = emptyList()
                        )
                    }
                }
        }
    }

    private fun updateRecord(
        previousRecord: IndividualDisciplineRecord,
        newValue: String?,
        newComment: String
    ) {
        viewModelScope.launch {
            val updatedRecord = IndividualDisciplineRecord(
                id = previousRecord.id,
                competitorId = previousRecord.competitorId,
                campDay = previousRecord.campDay,
                value = newValue,
                timeStamp = LocalDateTime.now(),
                refereeId = _state.value.currentLeader.leader.id,
                comment = newComment,
                countsForImprovement = previousRecord.countsForImprovement,
                improvement = previousRecord.improvement,
                isRecord = previousRecord.isRecord,
                quest = previousRecord.quest
            )
            individualDisciplineRecordRepository.updateIndividualRecord(
                record = updatedRecord,
                discipline = _state.value.discipline,
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
            _state.update {
                it.copy(filteredRecords = newFilterRecords)
            }
        }
    }


}

sealed interface DualOptionListAction {
    data class OnCriteriaSelected(val filterCriteria: FilterCriteria) : DualOptionListAction
    data class OnFilterItemSelected(val item: SelectableItem?) : DualOptionListAction
    data class OnRecordUpdate(
        val record: IndividualDisciplineRecord,
        val newValue: String?,
        val newComment: String,
    ) : DualOptionListAction

    data class OnRecordCreate(
        val value: String?,
        val competitorId: Int,
        val quest: Int?
    ) : DualOptionListAction

    data class OnChangeAgilityFilterCriteria(val newCriteria: AgilityFilterCriteria) :
        DualOptionListAction

    data class OnChangeAgilitySelectedItem(val newItem: SelectableItem) :
        DualOptionListAction
}

data class DualOptionListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val children: List<Child> = emptyList(),
    val leaders: List<Leader> = emptyList(),
    val filteredRecords: List<IndividualDisciplineRecord> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList(),
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val enabledUpdatingRecords: Boolean? = null,
    val selectedFilterCriteria: FilterCriteria = FilterCriteria.GROUPS,
    val selectedGroup: Group? = null,
    val selectedTrailCategory: TrailCategory? = null,
    val selectedAgilityFilterCriteria: AgilityFilterCriteria = AgilityFilterCriteria.QUESTS,
    val selectedAgilityItem: SelectableItem = AgilityQuests[0],
)