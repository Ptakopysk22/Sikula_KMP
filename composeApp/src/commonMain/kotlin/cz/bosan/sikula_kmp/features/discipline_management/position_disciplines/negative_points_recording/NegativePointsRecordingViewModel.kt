package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res

class NegativePointsRecordingViewModel(
    private val leaderRepository: LeaderRepository,
    private val campRepository: CampRepository,
    private val childRepository: ChildRepository,
    private val individualDisciplineRecordRepository: IndividualDisciplineRecordRepository,
    campDay: Int,
) : ViewModel() {
    private val _state = MutableStateFlow(
        NegativePointsRecordingState(
            campDay = campDay
        )
    )
    val state: StateFlow<NegativePointsRecordingState> = _state

    private var allChildren: List<Child> = emptyList()

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            _state.value =
                _state.value.copy(
                    currentLeader = currentLeader,
                )
            loadGroups()
            loadCrews()
            loadChildren()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: NegativePointsRecordingAction) {
        when (action) {
            is NegativePointsRecordingAction.OnChildSelected -> {
                _state.update {
                    it.copy(
                        selectedChild = action.child,
                        selectedNegativePointsVariant = if (action.child.role == ChildRole.CREW_MASTER) negativePointsVariantList[1] else negativePointsVariantList[0],
                        isSearchingChild = false
                    )
                }
            }

            is NegativePointsRecordingAction.OnSearchClick -> {
                searchChildContainingString(action.query)
            }

            is NegativePointsRecordingAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchedQuery = action.query
                    )
                }
                searchChildContainingString(action.query)
            }

            is NegativePointsRecordingAction.OnSearchBarFocusedChange -> {
                _state.update {
                    it.copy(isSearchBarFocused = action.isFocused)
                }
            }

            is NegativePointsRecordingAction.OnNegativePointsVariantSelected -> {
                _state.update { it.copy(selectedNegativePointsVariant = action.negativePointsVariant) }
            }

            is NegativePointsRecordingAction.OnCommentChange -> {
                _state.update { it.copy(comment = action.newComment) }
            }

            NegativePointsRecordingAction.OnCreateRecord -> createRecord()
        }
    }

    private fun searchChildContainingString(query: String) {
        viewModelScope.launch {
            val lowerQuery = query.lowercase()

            val filteredChildren = allChildren.filter { child ->
                val nickname = child.nickName.lowercase()
                val name = child.name.lowercase()

                nickname.contains(lowerQuery) || name.contains(lowerQuery)
            }
            _state.update { it.copy(filteredChildren = filteredChildren.sortedBy { it.nickName }) }
        }
    }

    private fun createRecord() {
        viewModelScope.launch {
            if (_state.value.comment.isEmpty()) {
                _state.update {
                    it.copy(isCommentValid = false)
                }
            } else {
                individualDisciplineRecordRepository.createIndividualDisciplineRecord(
                    record = IndividualDisciplineRecord(
                        id = null,
                        competitorId = _state.value.selectedChild?.id!!,
                        campDay = _state.value.campDay,
                        value = _state.value.selectedNegativePointsVariant?.value.toString(),
                        timeStamp = LocalDateTime.now(),
                        refereeId = _state.value.currentLeader.leader.id,
                        comment = _state.value.comment,
                    ),
                    discipline = Discipline.Individual.NEGATIVE_POINTS,
                    campId = _state.value.currentLeader.camp.id
                ).onSuccess {
                    _state.update{
                        it.copy(isSearchingChild = true,
                            selectedChild = null,
                            comment = "",
                            isCommentValid = true,
                            selectedNegativePointsVariant = null,
                            searchedQuery = "")
                    }
                }.onError { error ->
                    _state.update{
                        it.copy(errorMessage = error.toUiText())
                    }
                }
            }
        }
    }

    private suspend fun loadChildren() {
        childRepository.getCampsChildren(
            campId = _state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role,
        ).onSuccess { children ->
            allChildren = children
            _state.update {
                it.copy(
                    filteredChildren = children.sortedBy { it.nickName },
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    filteredChildren = emptyList(),
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
}

sealed interface NegativePointsRecordingAction {
    data class OnSearchQueryChange(val query: String) : NegativePointsRecordingAction
    data class OnSearchClick(val query: String) : NegativePointsRecordingAction
    data class OnChildSelected(val child: Child) : NegativePointsRecordingAction
    data class OnNegativePointsVariantSelected(val negativePointsVariant: NegativePointsVariant) :
        NegativePointsRecordingAction
    data class OnCommentChange(val newComment: String) : NegativePointsRecordingAction
    data object OnCreateRecord: NegativePointsRecordingAction
    data class OnSearchBarFocusedChange(val isFocused: Boolean) : NegativePointsRecordingAction
}

data class NegativePointsRecordingState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val campDay: Int,
    val isLoading: Boolean = true,
    val isSearchBarFocused: Boolean = false,
    val isSearchingChild: Boolean = true,
    val selectedChild: Child? = null,
    val searchedChildren: List<Child> = emptyList(),
    val selectedNegativePointsVariant: NegativePointsVariant? = null,
    val comment: String = "",
    val isCommentValid: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val searchedQuery: String = "",
    val filteredChildren: List<Child> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val groups: List<Group> = emptyList(),
)

data class NegativePointsVariant(
    val id: Int,
    val name: String,
    val value: Int,
)

val negativePointsVariantList = listOf(
    NegativePointsVariant(
        id = 1,
        name = "Mínusák",
        value = -1
    ),
    NegativePointsVariant(
        id = 2,
        name = "Špalík",
        value = -3
    ),
    NegativePointsVariant(
        id = 3,
        name = "Kolík",
        value = -5
    )
)