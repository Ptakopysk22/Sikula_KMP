package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_discipline_list

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
import cz.bosan.sikula_kmp.managers.children_manager.data.LightChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.utils.now

class DailyDisciplineListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val campRepository: CampRepository,
    initDiscipline: Discipline,
    campDay: Int?
) : ViewModel() {

    private val _state = MutableStateFlow(
        DailyDisciplineListState(
            discipline = initDiscipline
        )
    )
    val state: StateFlow<DailyDisciplineListState> = _state

    private var campsDayRecords: List<IndividualDisciplineRecord> = emptyList()

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            val position =
                if (_state.value.discipline == Discipline.Individual.NEGATIVE_POINTS) Position.NEGATIVE_POINTS_MASTER else if (_state.value.discipline == Discipline.Individual.MORSE) Position.MORSE_MASTER else Position.UNKNOWN_POSITION
            val isPositionMaster = currentLeader.leader.positions.contains(position)
            val campDayValue =
                if (campDay == 0 || campDay == null) leaderRepository.getCurrentCampDay() else campDay
            val todayCampDay = leaderRepository.getCurrentCampDay()
            val campDuration = leaderRepository.getCampDuration()
            _state.value =
                _state.value.copy(
                    currentLeader = currentLeader,
                    isPositionMaster = isPositionMaster,
                    campDay = campDayValue,
                    todayCampDay = todayCampDay,
                    campDuration = campDuration
                )
            loadChildren()
            loadTrailCategories()
            loadCampLeaders()
            loadGroups()
            loadCrews()
            loadRecords()
            setDayRecordsState()
            enabledCreatingRecords()
            enabledUpdateRecords()
            findFilterCriteriaAndFilterRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: DailyDisciplineListAction) {
        when (action) {
            is DailyDisciplineListAction.OnChangeCampDay -> updateCampDay(action.campDay)
            is DailyDisciplineListAction.OnCriteriaSelected -> {
                _state.update {
                    it.copy(
                        selectedFilterCriteria = action.filterCriteria,
                        filteredRecords = campsDayRecords,
                        selectedGroup = null,
                        selectedTrailCategory = null,
                    )
                }
            }

            is DailyDisciplineListAction.OnFilterItemSelected -> {
                filterRecords(
                    selectedItem = action.item,
                    records = campsDayRecords
                )
            }

            is DailyDisciplineListAction.OnRecordUpdate ->
                if (_state.value.enabledUpdatingRecords == true) {
                    updateRecord(action.record, action.newValue, action.newComment)
                }

            DailyDisciplineListAction.OnSubmitRecords -> submitRecordsByDisciplineMaser()
            DailyDisciplineListAction.OnSubmitRecordsByGameMaster -> submitRecordsByGameMaster()
            DailyDisciplineListAction.OnChildrenWithoutRecords -> setChildrenWithoutRecords()
            is DailyDisciplineListAction.OnUpdateWorkedOff -> {
                updateRecordWorkedOff(action.record, action.newWorkedOff)
            }
        }
    }


    private fun updateCampDay(campDay: Int) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            _state.update {
                it.copy(campDay = campDay)
            }
            loadRecords()
            findFilterCriteriaAndFilterRecords()
            setDayRecordsState()
            enabledCreatingRecords()
            enabledUpdateRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun setDayRecordsState() {
        var dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        reviewInfoRepository.getDayReviewInfo(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { dayReviewInfo ->
            if (dayReviewInfo?.reviewed == true) {
                dayRecordsState = DayRecordsState.CHECKED_BY_GAME_MASTER
            } else {
                if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
                    if (dayReviewInfo?.readyForReview == true) {
                        dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                    } else {
                        dayRecordsState = DayRecordsState.IN_PROGRESS
                    }
                } else if (_state.value.isPositionMaster) {
                    if (dayReviewInfo?.readyForReview == null) {
                        dayRecordsState = DayRecordsState.WITHOUT_STATE
                    } else {
                        var isSubmitByDisciplineMaster = true
                        for (group in dayReviewInfo.groupReviewInfos) {
                            if (!group.readyForReview) {
                                isSubmitByDisciplineMaster = false
                                break
                            }
                        }
                        if (isSubmitByDisciplineMaster) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            if (_state.value.discipline == Discipline.Individual.NEGATIVE_POINTS) {
                                if (_state.value.campDay < _state.value.todayCampDay || _state.value.campDay == _state.value.campDuration && _state.value.campDay == _state.value.todayCampDay) {
                                    dayRecordsState = DayRecordsState.NON_CHECKED_BY_GROUP
                                } else {
                                    dayRecordsState = DayRecordsState.IN_PROGRESS
                                }
                            } else {
                                val activeChildren = _state.value.children.filter { it.isActive }
                                val allChildrenHaveRecords =
                                    activeChildren.all { child ->
                                        campsDayRecords.any { record -> record.competitorId == child.id }
                                    }
                                if (allChildrenHaveRecords) {
                                    dayRecordsState = DayRecordsState.NON_CHECKED_BY_GROUP
                                } else {
                                    dayRecordsState = DayRecordsState.IN_PROGRESS
                                }
                            }
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

    private fun enabledCreatingRecords() {
        val dayRecordsState = _state.value.dayRecordsState
        val enabledCreatingRecords: Boolean
        val campDay = _state.value.campDay
        val todayCampDay = _state.value.todayCampDay
        if ((_state.value.isPositionMaster && dayRecordsState != DayRecordsState.CHECKED_BY_GROUP && dayRecordsState != DayRecordsState.CHECKED_BY_GAME_MASTER) && todayCampDay >= campDay) {
            if (_state.value.discipline == Discipline.Individual.NEGATIVE_POINTS) {
                enabledCreatingRecords = true
            } else {
                val activeChildren = _state.value.children.filter { it.isActive }
                val allChildrenHaveRecords =
                    activeChildren.all { child ->
                        campsDayRecords.any { record -> record.competitorId == child.id }
                    }
                if (allChildrenHaveRecords) {
                    enabledCreatingRecords = false
                } else {
                    enabledCreatingRecords = true
                }
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
        if (role == Role.GAME_MASTER) {
            enabledUpdatingRecords = true
        } else if (_state.value.isPositionMaster && dayRecordsState != DayRecordsState.CHECKED_BY_GAME_MASTER && dayRecordsState != DayRecordsState.CHECKED_BY_GROUP) {
            enabledUpdatingRecords = true
        } else {
            enabledUpdatingRecords = false
        }
        _state.update {
            it.copy(enabledUpdatingRecords = enabledUpdatingRecords)
        }
    }

    private fun findFilterCriteriaAndFilterRecords() {
        val selectedFilterCriteria = _state.value.selectedFilterCriteria
        if (selectedFilterCriteria == FilterCriteria.GROUPS) {
            filterRecords(
                selectedItem = _state.value.selectedGroup,
                records = campsDayRecords
            )
        } else if (selectedFilterCriteria == FilterCriteria.TRAIL_CATEGORIES) {
            filterRecords(
                selectedItem = _state.value.selectedTrailCategory,
                records = campsDayRecords
            )
        }
    }

    private fun filterRecords(
        selectedItem: SelectableItem?,
        records: List<IndividualDisciplineRecord>
    ) {
        val children = _state.value.children

        val filteredRecords = when (selectedItem) {
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
        _state.update {
            it.copy(
                selectedGroup = if (selectedItem is Group) selectedItem else null,
                selectedTrailCategory = if (selectedItem is TrailCategory) selectedItem else null,
                filteredRecords = filteredRecords,
                warningMessage = warningMessage,
            )
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
                )
            }
        }
    }

    private suspend fun loadRecords() {
        individualDisciplineRecordRepository.getIndividualDisciplineRecordsDay(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }
            campsDayRecords = records.sortedWith(compareBy { record ->
                childMap[record.competitorId]?.nickName ?: ""
            })
            _state.update {
                it.copy(
                    warningMessage = if (campsDayRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null,
                    filteredRecords = campsDayRecords
                )
            }
        }.onError { error ->
            campsDayRecords = emptyList()
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
                workedOff = previousRecord.workedOff
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

    private fun submitRecordsByDisciplineMaser() {
        viewModelScope.launch {
            reviewInfoRepository.submitRecordsPositionMaster(
                submit = true,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
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

    private fun submitRecordsByGameMaster() {
        viewModelScope.launch {
            reviewInfoRepository.submitRecordsCamp(
                discipline = _state.value.discipline,
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

    private fun setChildrenWithoutRecords() {
        val recordedChildrenIds = _state.value.filteredRecords.map { it.competitorId }.toSet()

        val activeChildrenWithoutRecord = _state.value.children.filter { child ->
            child.isActive && !recordedChildrenIds.contains(child.id)
        }.map { child ->
            LightChild(
                id = child.id,
                nickname = child.nickName,
                trailCategoryId = child.trailCategoryId ?: -1
            )
        }.sortedBy { it.nickname }
        val childrenJson = Json.encodeToString(activeChildrenWithoutRecord)

        _state.update {
            it.copy(activeChildrenWithoutRecord = childrenJson)
        }
    }

}

sealed interface DailyDisciplineListAction {
    data class OnChangeCampDay(val campDay: Int) : DailyDisciplineListAction
    data class OnCriteriaSelected(val filterCriteria: FilterCriteria) :
        DailyDisciplineListAction

    data class OnFilterItemSelected(val item: SelectableItem?) : DailyDisciplineListAction
    data class OnRecordUpdate(
        val record: IndividualDisciplineRecord,
        val newValue: String?,
        val newComment: String
    ) :
        DailyDisciplineListAction

    data class OnUpdateWorkedOff(
        val record: IndividualDisciplineRecord,
        val newWorkedOff: Boolean,
    ) : DailyDisciplineListAction

    data object OnSubmitRecords : DailyDisciplineListAction
    data object OnSubmitRecordsByGameMaster : DailyDisciplineListAction
    data object OnChildrenWithoutRecords : DailyDisciplineListAction
}

data class DailyDisciplineListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int = 1,
    val todayCampDay: Int = 1,
    val campDuration: Int = 21,
    val dayRecordsState: DayRecordsState = DayRecordsState.WITHOUT_STATE,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val children: List<Child> = emptyList(),
    val activeChildrenWithoutRecord: String? = null,
    val leaders: List<Leader> = emptyList(),
    val filteredRecords: List<IndividualDisciplineRecord> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList(),
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val enabledCreatingRecords: Boolean? = null,
    val enabledUpdatingRecords: Boolean? = null,
    val selectedFilterCriteria: FilterCriteria = FilterCriteria.GROUPS,
    val selectedGroup: Group? = null,
    val selectedTrailCategory: TrailCategory? = null,
    val isPositionMaster: Boolean = false,
)