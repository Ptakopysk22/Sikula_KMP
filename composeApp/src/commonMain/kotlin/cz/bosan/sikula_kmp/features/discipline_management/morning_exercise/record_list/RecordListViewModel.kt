package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list

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
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.domain.DayReviewInfo
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
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

class RecordListViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    private val serverRepository: ServerRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val campRepository: CampRepository,
    initDiscipline: Discipline,
    campDay: Int?
) : ViewModel() {

    private val _state = MutableStateFlow(
        RecordListState(
            discipline = initDiscipline
        )
    )
    val state: StateFlow<RecordListState> = _state

    private var campsDayRecords: List<IndividualDisciplineRecord> = emptyList()

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    dayRecordsState = if (serverRepository.isServerResponding()) DayRecordsState.WITHOUT_STATE else DayRecordsState.OFFLINE,
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    campDay = if(campDay == 0 || campDay == null) leaderRepository.getCurrentCampDay() else campDay,
                    todayCampDay = leaderRepository.getCurrentCampDay(),
                    campDuration = leaderRepository.getCampDuration(),
                )
            viewModelScope.launch {
                state.map { it.currentLeader }
                    .distinctUntilChanged()
                    .collectLatest {
                        loadChildren()
                        loadTrailCategories()
                        loadCampLeaders()

                        if (serverRepository.isServerResponding()) {
                            loadGroups()
                            loadCrews()
                            setSelectedGroup()
                        }

                        setDayRecordsState()
                        enabledCreatingRecords()
                        enabledUpdateRecords()
                        setShowCampsRecords()
                    }
            }
        }
    }

    fun onAction(action: RecordListAction) {
        when (action) {
            is RecordListAction.OnChangeCampDay -> updateCampDay(action.campDay)
            RecordListAction.OnChildrenWithoutRecords -> setChildrenWithoutRecords()
            is RecordListAction.OnCriteriaSelected -> {
                _state.update {
                    it.copy(
                        selectedFilterCriteria = action.filterCriteria,
                        filteredRecords = campsDayRecords,
                        selectedGroup = null,
                        selectedTrailCategory = null,
                    )
                }
            }

            is RecordListAction.OnFilterItemSelected -> {
                filterRecords(
                    selectedItem = action.item,
                    records = campsDayRecords
                )
            }

            is RecordListAction.OnRecordUpdate ->
                if (_state.value.enabledUpdatingRecords == true) {
                    updateRecord(action.record, action.newValue, action.newComment)
                }

            RecordListAction.OnSubmitRecords -> submitRecordsByGroup()
            RecordListAction.OnUnSubmitRecords -> unSubmitRecordsByGroup()
            RecordListAction.OnSubmitRecordsByGameMaster -> submitRecordsByGameMaster()
            is RecordListAction.OnUpdateCountsForImprovement ->
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
            setShowCampsRecords()
        }
    }

    private suspend fun setDayRecordsState() {
        var dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
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
            loadRecords(showCampsRecordsToGroup = false)
        } else {
            reviewInfoRepository.getDayReviewInfo(
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay
            ).onSuccess { dayReviewInfo ->
                if (dayReviewInfo?.reviewed == true) {
                    dayRecordsState = DayRecordsState.CHECKED_BY_GAME_MASTER
                    loadRecords()
                    findFilterCriteriaAndFilterRecords()
                    setDayRecordStateAllGroups(dayReviewInfo)
                } else {
                    loadRecords(showCampsRecordsToGroup = false)
                    if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
                        findFilterCriteriaAndFilterRecords()
                        val readySubmitByGameMaster = setDayRecordStateAllGroups(dayReviewInfo)
                        if (readySubmitByGameMaster) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            dayRecordsState = DayRecordsState.IN_PROGRESS
                        }
                        _state.update {
                            it.copy(isLoading = false)
                        }
                    } else if (role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) {
                        val groupReviewInfo =
                            dayReviewInfo?.groupReviewInfos?.find { it.groupId == _state.value.currentLeader.leader.groupId }
                        if (groupReviewInfo?.readyForReview == true) {
                            dayRecordsState = DayRecordsState.CHECKED_BY_GROUP
                        } else {
                            val records = _state.value.filteredRecords
                            val activeChildren = _state.value.groupChildren.filter { it.isActive }
                            if (groupReviewInfo?.readyForReview == null && records.isEmpty()) {
                                dayRecordsState = DayRecordsState.WITHOUT_STATE
                            } else {
                                val nonSynchronizeRecords =
                                    records.filter { it.isUploaded == false }
                                val allChildrenHaveRecords =
                                    activeChildren.all { child ->
                                        records.any { record -> record.competitorId == child.id }
                                    }
                                if (allChildrenHaveRecords) {
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
                }
            }.onError {
                if (role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) {
                    loadRecords(showCampsRecordsToGroup = false)
                    val records = _state.value.filteredRecords
                    val nonSynchronizeRecords =
                        records.filter { it.isUploaded == false }
                    val allChildrenHaveRecords =
                        _state.value.groupChildren.all { child ->
                            records.any { record -> record.competitorId == child.id }
                        }
                    if (records.isEmpty()) {
                        dayRecordsState = DayRecordsState.WITHOUT_STATE
                    } else {
                        if (allChildrenHaveRecords) {
                            if (nonSynchronizeRecords.isNotEmpty()) {
                                dayRecordsState = DayRecordsState.NON_SYNCHRONIZE
                            } else {
                                dayRecordsState = DayRecordsState.NON_CHECKED_BY_GROUP
                            }
                        } else {
                            dayRecordsState = DayRecordsState.IN_PROGRESS
                        }
                    }
                } else if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
                    loadRecords()
                    findFilterCriteriaAndFilterRecords()
                    setDayRecordStateAllGroups(
                        dayReviewInfo = DayReviewInfo(
                            campDay = _state.value.campDay,
                            readyForReview = false,
                            reviewed = false,
                            groupReviewInfos = emptyList()
                        )
                    )
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
        val records = _state.value.filteredRecords
        val allChildrenHaveRecords =
            _state.value.groupChildren.filter { it.isActive }.all { child ->
                records.any { record -> record.competitorId == child.id }
            }
        val enabledCreatingRecords: Boolean
        val campDay = _state.value.campDay
        val todayCampDay = _state.value.todayCampDay
        val role = _state.value.currentLeader.leader.role
        if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
            if (dayRecordsState == DayRecordsState.IN_PROGRESS) {
                enabledCreatingRecords = true
            } else if (dayRecordsState == DayRecordsState.WITHOUT_STATE && todayCampDay == campDay) {
                enabledCreatingRecords = true
            } else if (dayRecordsState == DayRecordsState.OFFLINE && campDay == todayCampDay) {
                if (allChildrenHaveRecords) {
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
        if (role == Role.GAME_MASTER) {
            enabledUpdatingRecords = true
        } else if (role == Role.DIRECTOR) {
            enabledUpdatingRecords = false
        } else if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
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

    private fun setShowCampsRecords() {
        val showCampsRecords: Boolean
        val role = _state.value.currentLeader.leader.role
        val dayRecordsState = _state.value.dayRecordsState
        if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
            showCampsRecords = true
        } else if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
            if (dayRecordsState == DayRecordsState.CHECKED_BY_GAME_MASTER) {
                showCampsRecords = true
            } else {
                showCampsRecords = false
            }
        } else {
            showCampsRecords = false
        }
        _state.update {
            it.copy(showCampsRecords = showCampsRecords)
        }
    }

    private suspend fun loadRecords(showCampsRecordsToGroup: Boolean = true) {
        _state.update {
            it.copy(isLoading = true)
        }
        val role = _state.value.currentLeader.leader.role
        if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
            loadDayRecordsCamps()
        } else if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
            if (showCampsRecordsToGroup) {
                loadDayRecordsCamps()
            } else {
                loadDayRecordsGroup()
            }
        }
        _state.update {
            it.copy(isLoading = false)
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
        val children = _state.value.allChildren

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
        val dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        if (dayRecordsState == DayRecordsState.OFFLINE && (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER)) {
            val localChildren = childRepository.getGroupChildrenLocally(
                campId = _state.value.currentLeader.camp.id,
                groupId = _state.value.currentLeader.leader.groupId!!,
            )
            _state.update {
                it.copy(
                    groupChildren = localChildren,
                    allChildren = emptyList(),
                )
            }
        } else {
            childRepository.getCampsChildren(
                campId = _state.value.currentLeader.camp.id,
                role = _state.value.currentLeader.leader.role,
                groupIdForSavingLocally = _state.value.currentLeader.leader.groupId
            ).onSuccess { children ->
                val groupChildren =
                    children.filter { it.groupId == _state.value.currentLeader.leader.groupId }
                _state.update {
                    it.copy(
                        groupChildren = groupChildren,
                        allChildren = children,
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        allChildren = emptyList(),
                        groupChildren = emptyList(),
                        errorMessage = error.toUiText(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun loadTrailCategories() {
        val dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        if (dayRecordsState == DayRecordsState.OFFLINE && (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER)) {
            val localCategories = childRepository.getTrailCategoriesLocally(
                campId = _state.value.currentLeader.camp.id,
            )
            _state.update {
                it.copy(
                    trailCategories = localCategories
                )
            }
        } else {
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
    }

    private suspend fun loadCampLeaders() {
        val dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        if (dayRecordsState == DayRecordsState.OFFLINE && (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER)) {
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
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun loadDayRecordsGroup() {
        val dayRecordsState = _state.value.dayRecordsState
        val role = _state.value.currentLeader.leader.role
        val childMap = _state.value.groupChildren.associateBy { it.id }
        if (dayRecordsState == DayRecordsState.OFFLINE && (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER)) {
            val localRecords = getDayRecordsLocally()
            val warningMessage =
                if (localRecords.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
            _state.update {
                it.copy(
                    filteredRecords = localRecords.sortedWith(compareBy { record ->
                        childMap[record.competitorId]?.nickName ?: ""
                    }),
                    warningMessage = warningMessage,
                )
            }
        } else {
            val localRecords = getDayRecordsLocally()
                .filter { it.isUploaded == false }
            _state.value.currentLeader.leader.groupId?.let {
                individualDisciplineRecordRepository.getIndividualDisciplineRecordsGroup(
                    discipline = _state.value.discipline,
                    campId = _state.value.currentLeader.camp.id,
                    groupId = it,
                    campDay = _state.value.campDay
                ).onSuccess { records ->
                    val filteredLocalRecords = localRecords.filterNot { localRecord ->
                        records.any { it.competitorId == localRecord.competitorId }
                    }
                    val warningMessage =
                        if ((records + filteredLocalRecords).isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null
                    _state.update {
                        it.copy(
                            filteredRecords = (records + filteredLocalRecords).sortedWith(compareBy { record ->
                                childMap[record.competitorId]?.nickName ?: ""
                            }),
                            warningMessage = warningMessage,
                        )
                    }
                    synchronizeRemainingRecords()
                }.onError { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.toUiText(),
                            filteredRecords = emptyList(),
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadDayRecordsCamps() {
        individualDisciplineRecordRepository.getIndividualDisciplineRecordsDay(
            discipline = _state.value.discipline,
            campId = _state.value.currentLeader.camp.id,
            campDay = _state.value.campDay
        ).onSuccess { records ->
            val childMap = _state.value.allChildren.associateBy { it.id }
            campsDayRecords = records.sortedWith(compareBy { record ->
                childMap[record.competitorId]?.nickName ?: ""
            })
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

    private suspend fun getDayRecordsLocally(): List<IndividualDisciplineRecord> {
        val localRecords: List<IndividualDisciplineRecord>
        if (_state.value.currentLeader.leader.role == Role.CHILD_LEADER || _state.value.currentLeader.leader.role == Role.HEAD_GROUP_LEADER) {
            localRecords = individualDisciplineRecordRepository.getIndividualRecordsLocally(
                campId = _state.value.currentLeader.camp.id,
                campDay = _state.value.campDay,
                discipline = _state.value.discipline,
            )
        } else {
            localRecords = emptyList()
        }
        val childrenInGroupIds = _state.value.groupChildren.map { it.id }.toSet()
        val groupRecords = localRecords.filter { it.competitorId in childrenInGroupIds }
        return groupRecords
    }

    private fun setChildrenWithoutRecords() {
        val recordedChildrenIds = _state.value.filteredRecords.map { it.competitorId }.toSet()

        val activeChildrenWithoutRecord = _state.value.groupChildren.filter { child ->
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

    private fun setSelectedGroup() {
        val groupId = _state.value.currentLeader.leader.groupId
        val groups = _state.value.groups
        val leadersGroup = groups.find { it.id == groupId }
        _state.update {
            it.copy(
                selectedGroup = leadersGroup
            )
        }
    }

    private fun updateRecord(
        previousRecord: IndividualDisciplineRecord,
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
            var updatedRecord = IndividualDisciplineRecord.EMPTY
            if (previousRecord.isUploaded == false || (previousRecord.isUploaded == true && dayRecordsState == DayRecordsState.OFFLINE)) {
                updatedRecord = IndividualDisciplineRecord(
                    id = previousRecord.id,
                    competitorId = previousRecord.competitorId,
                    campDay = previousRecord.campDay,
                    value = newValue,
                    timeStamp = LocalDateTime.now(),
                    refereeId = _state.value.currentLeader.leader.id,
                    comment = newComment,
                    countsForImprovement = previousRecord.countsForImprovement,
                    isUploaded = false,
                    improvement = null,
                    isRecord = null,
                )
                individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                    record = updatedRecord,
                    discipline = _state.value.discipline,
                    campId = _state.value.currentLeader.camp.id,
                    idOnServer = null
                )
            } else {
                if (dayRecordsState != DayRecordsState.OFFLINE) {
                    var isUploaded: Boolean? = null
                    var improvement: String? = null
                    var isRecord: Boolean? = null
                    updatedRecord = IndividualDisciplineRecord(
                        id = previousRecord.id,
                        competitorId = previousRecord.competitorId,
                        campDay = previousRecord.campDay,
                        value = newValue,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = newComment,
                        countsForImprovement = previousRecord.countsForImprovement,
                    )
                    individualDisciplineRecordRepository.updateIndividualRecord(
                        record = updatedRecord,
                        discipline = _state.value.discipline,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        improvement = updatedRecord.improvement
                        isRecord = updatedRecord.isRecord
                        isUploaded = true
                    }.onError { error ->
                        isUploaded = false
                        _state.update {
                            it.copy(
                                errorMessage = error.toUiText(),
                                filteredRecords = emptyList()
                            )
                        }
                    }
                    updatedRecord = IndividualDisciplineRecord(
                        id = previousRecord.id,
                        competitorId = previousRecord.competitorId,
                        campDay = previousRecord.campDay,
                        value = newValue,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = newComment,
                        countsForImprovement = previousRecord.countsForImprovement,
                        isUploaded = isUploaded,
                        improvement = improvement,
                        isRecord = isRecord,
                    )
                    individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                        record = updatedRecord,
                        discipline = _state.value.discipline,
                        campId = _state.value.currentLeader.camp.id,
                        idOnServer = updatedRecord.id
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

    private fun updateCountsForImprovement(
        previousRecord: IndividualDisciplineRecord,
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
            var updatedRecord = IndividualDisciplineRecord.EMPTY
            if (previousRecord.isUploaded == false || (previousRecord.isUploaded == true && dayRecordsState == DayRecordsState.OFFLINE)) {
                updatedRecord = IndividualDisciplineRecord(
                    id = previousRecord.id,
                    competitorId = previousRecord.competitorId,
                    campDay = previousRecord.campDay,
                    value = previousRecord.value,
                    timeStamp = LocalDateTime.now(),
                    refereeId = _state.value.currentLeader.leader.id,
                    comment = previousRecord.comment,
                    countsForImprovement = newValue,
                    isUploaded = false,
                    improvement = null,
                    isRecord = null,
                )
                individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                    record = updatedRecord,
                    discipline = _state.value.discipline,
                    campId = _state.value.currentLeader.camp.id,
                    idOnServer = null
                )
            } else {
                if (dayRecordsState != DayRecordsState.OFFLINE) {
                    var isUploaded: Boolean? = null
                    var improvement: String? = null
                    var isRecord: Boolean? = null
                    updatedRecord = IndividualDisciplineRecord(
                        id = previousRecord.id,
                        competitorId = previousRecord.competitorId,
                        campDay = previousRecord.campDay,
                        value = previousRecord.value,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = previousRecord.comment,
                        countsForImprovement = newValue,
                    )
                    individualDisciplineRecordRepository.updateIndividualRecordCountsForImprovement(
                        record = updatedRecord,
                        discipline = _state.value.discipline,
                        countsForImprovement = newValue,
                        campId = _state.value.currentLeader.camp.id
                    ).onSuccess { updatedRecord ->
                        improvement = updatedRecord.improvement
                        isRecord = updatedRecord.isRecord
                        isUploaded = true
                    }.onError { error ->
                        isUploaded = false
                        _state.update {
                            it.copy(
                                errorMessage = error.toUiText(),
                                filteredRecords = emptyList()
                            )
                        }
                    }
                    updatedRecord = IndividualDisciplineRecord(
                        id = previousRecord.id,
                        competitorId = previousRecord.competitorId,
                        campDay = previousRecord.campDay,
                        value = previousRecord.value,
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = previousRecord.comment,
                        countsForImprovement = newValue,
                        isUploaded = isUploaded,
                        improvement = improvement,
                        isRecord = isRecord,
                    )
                    individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                        record = updatedRecord,
                        discipline = _state.value.discipline,
                        campId = _state.value.currentLeader.camp.id,
                        idOnServer = updatedRecord.id
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

    private suspend fun synchronizeRemainingRecords(): Boolean {
        val nonSynchronizeRecords = _state.value.filteredRecords.filter { it.isUploaded == false }
        var successFullySynchronize = true
        for (record in nonSynchronizeRecords) {
            individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                record = record,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess { newRecord ->
                individualDisciplineRecordRepository.insertOrUpdateIndividualRecordLocally(
                    idOnServer = newRecord.id,
                    record = IndividualDisciplineRecord(
                        id = record.id,
                        competitorId = record.competitorId,
                        campDay = record.campDay,
                        value = record.value,
                        timeStamp = record.timeStamp,
                        refereeId = record.refereeId,
                        comment = record.comment,
                        countsForImprovement = record.countsForImprovement,
                        isUploaded = true,
                    ),
                    campId = _state.value.currentLeader.camp.id,
                    discipline = _state.value.discipline
                )
                loadDayRecordsGroup()
            }.onError { successFullySynchronize = false }
        }
        return successFullySynchronize
    }

    private fun submitRecordsByGroup() {
        viewModelScope.launch {
            var successFullySynchronize = true
            if (state.value.dayRecordsState == DayRecordsState.NON_SYNCHRONIZE) {
                successFullySynchronize = synchronizeRemainingRecords()
            }
            if (successFullySynchronize) {
                reviewInfoRepository.submitRecordsGroup(
                    submit = true,
                    discipline = _state.value.discipline,
                    campId = _state.value.currentLeader.camp.id,
                    groupId = _state.value.currentLeader.leader.groupId!!,
                    campDay = _state.value.campDay
                ).onSuccess {
                    _state.update {
                        it.copy(dayRecordsState = DayRecordsState.CHECKED_BY_GROUP)
                    }
                    enabledUpdateRecords()
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


    private fun unSubmitRecordsByGroup() {
        viewModelScope.launch {
            reviewInfoRepository.submitRecordsGroup(
                submit = false,
                discipline = _state.value.discipline,
                campId = _state.value.currentLeader.camp.id,
                groupId = _state.value.currentLeader.leader.groupId!!,
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

    private fun setDayRecordStateAllGroups(dayReviewInfo: DayReviewInfo?): Boolean {
        val groups = _state.value.groups
        val dayRecordStateAllGroups = mutableListOf<Pair<Group, DayRecordsState>>()
        var readySubmitByGameMaster = if (dayReviewInfo?.readyForReview == true) true else false
        for (group in groups) {
            var groupDayStateRecords: DayRecordsState
            val groupReviewInfo = dayReviewInfo?.groupReviewInfos?.find { it.groupId == group.id }
            if (groupReviewInfo?.readyForReview == true) {
                groupDayStateRecords = DayRecordsState.CHECKED_BY_GROUP
            } else {
                val groupChildren = _state.value.allChildren.filter { it.groupId == group.id }
                val allChildrenHaveRecords = groupChildren.all { child ->
                    campsDayRecords.any { record -> record.competitorId == child.id }
                }
                val atLeastOneChildHasRecord = groupChildren.any { child ->
                    campsDayRecords.any { record -> record.competitorId == child.id }
                }
                groupDayStateRecords = when {
                    groupChildren.isEmpty() -> DayRecordsState.WITHOUT_STATE
                    allChildrenHaveRecords -> DayRecordsState.NON_CHECKED_BY_GROUP
                    atLeastOneChildHasRecord -> DayRecordsState.IN_PROGRESS
                    else -> DayRecordsState.WITHOUT_STATE
                }
            }
            if (groupDayStateRecords == DayRecordsState.NON_CHECKED_BY_GROUP || groupDayStateRecords == DayRecordsState.IN_PROGRESS) {
                readySubmitByGameMaster = false
            }
            dayRecordStateAllGroups.add(Pair(group, groupDayStateRecords))
        }
        _state.update {
            it.copy(dayRecordStateAllGroups = dayRecordStateAllGroups)
        }

        return readySubmitByGameMaster
    }

}

sealed interface RecordListAction {
    data object OnChildrenWithoutRecords : RecordListAction
    data class OnChangeCampDay(val campDay: Int) : RecordListAction
    data class OnCriteriaSelected(val filterCriteria: FilterCriteria) : RecordListAction
    data class OnFilterItemSelected(val item: SelectableItem?) : RecordListAction
    data class OnRecordUpdate(
        val record: IndividualDisciplineRecord,
        val newValue: String?,
        val newComment: String
    ) :
        RecordListAction

    data class OnUpdateCountsForImprovement(
        val record: IndividualDisciplineRecord,
        val newValue: Boolean
    ) :
        RecordListAction

    data object OnSubmitRecords : RecordListAction
    data object OnUnSubmitRecords : RecordListAction
    data object OnSubmitRecordsByGameMaster : RecordListAction
}

data class RecordListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline,
    val campDay: Int = 1,
    val campDuration: Int = 21,
    val dayRecordsState: DayRecordsState = DayRecordsState.WITHOUT_STATE,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val groupChildren: List<Child> = emptyList(),
    val allChildren: List<Child> = emptyList(),
    val leaders: List<Leader> = emptyList(),
    val activeChildrenWithoutRecord: String? = null,
    val filteredRecords: List<IndividualDisciplineRecord> = emptyList(),
    val trailCategories: List<TrailCategory> = emptyList(),
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val enabledCreatingRecords: Boolean? = null,
    val enabledUpdatingRecords: Boolean? = null,
    val showCampsRecords: Boolean? = null,
    val todayCampDay: Int = 1,
    val selectedFilterCriteria: FilterCriteria = FilterCriteria.GROUPS,
    val selectedGroup: Group? = null,
    val selectedTrailCategory: TrailCategory? = null,
    val dayRecordStateAllGroups: List<Pair<Group, DayRecordsState>> = emptyList()
)