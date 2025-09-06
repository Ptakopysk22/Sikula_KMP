package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data.BadgesRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BadgesListGrantingViewModel(
    private val leaderRepository: LeaderRepository,
    private val childRepository: ChildRepository,
    private val campRepository: CampRepository,
    private val badgesRepository: BadgesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BadgesListGrantingState()
    )
    val state: StateFlow<BadgesListGrantingState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            _state.value =
                _state.value.copy(
                    currentLeader = currentLeader,
                )
            loadChildren()
            loadCrews()
            loadCampBadges()
            loadRecords()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: BadgesListGrantingAction) {
        when (action) {
            is BadgesListGrantingAction.OnBadgeDistributed -> grantBadge(action.record)
            is BadgesListGrantingAction.OnBadgeCancelDistribution -> cancelDistributionOfBadge(
                action.record
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
                    children = children.filter { it.isActive },
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

    private suspend fun loadCampBadges() {
        badgesRepository.getCampBadges(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { campBadges ->
            val badgeIdToDisciplineId = campBadges
                .associateBy({ it.id }, { it.disciplineId })

            _state.update {
                it.copy(
                    badgeIdToDisciplineId = badgeIdToDisciplineId,
                    campBadges = campBadges
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    campBadges = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadRecords() {
        badgesRepository.getBadgeResultsToBeAwarded(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }

            val allBadgesToBeAwarded = records
                .filter { record -> childMap.containsKey(record.competitorId) }
                .sortedWith(compareBy { record ->
                    childMap[record.competitorId]!!.nickName
                })

            val badgeRecordsWithDisciplineId = allBadgesToBeAwarded.mapNotNull { record ->
                val disciplineId = _state.value.badgeIdToDisciplineId[record.badgeId]
                disciplineId?.let {
                    it to record
                }
            }

            val groupedByDisciplineId = badgeRecordsWithDisciplineId.groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )

            val badgesToBeAwarded = groupedByDisciplineId.map { (disciplineId, records) ->

                val badgesSummaryList = records
                    .groupingBy { it.badgeId }
                    .eachCount()
                    .map { (badgeId, count) ->
                        BadgesSummary(
                            badgeId = badgeId,
                            countOfBadges = count
                        )
                    }
                BadgesDisciplineToBeAwarded(
                    disciplineId = disciplineId,
                    records = records.sortedBy { it.badgeId },
                    badgesSummaryList = badgesSummaryList
                )
            }
            _state.update {
                it.copy(
                    badgesToBeAwarded = badgesToBeAwarded
                )
            }

        }.onError { error ->
            _state.update {
                it.copy(
                    badgesToBeAwarded = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }

        badgesRepository.getBadgeResultsToBeRemoved(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { records ->
            val childMap = _state.value.children.associateBy { it.id }
            val badgesToBeRemoved = records
                .filter { record -> childMap.containsKey(record.competitorId) }
                .sortedWith(compareBy { record ->
                    childMap[record.competitorId]?.nickName ?: ""
                })
            _state.update {
                it.copy(
                    badgesToBeRemoved = badgesToBeRemoved
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    badgesToBeRemoved = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }

        _state.update {
            it.copy(
                warningMessage = if (_state.value.badgesToBeRemoved.isEmpty() && _state.value.badgesToBeAwarded.isEmpty()) Warning.Common.EMPTY_LIST.toUiText() else null,
            )
        }
    }

    private fun grantBadge(record: BadgeRecord) {
        viewModelScope.launch {
            badgesRepository.grantBadge(
                record = record,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess {
                val updatedRecord = if(record.toBeAwarded) {
                    record.copy(isAwarded = true, toBeAwarded = false)
                }else {
                    record.copy(isRemoved = true, toBeRemoved = false)
                }
                val disciplineId = _state.value.badgeIdToDisciplineId[updatedRecord.badgeId]

                val updatedAwardedList = _state.value.badgesToBeAwarded.mapNotNull { item ->
                    if (item.disciplineId == disciplineId) {
                        val updatedRecords = item.records.filterNot { it.id == updatedRecord.id }
                        val updatedSummaryList = item.badgesSummaryList.mapNotNull { summary ->
                            if (summary.badgeId == updatedRecord.badgeId) {
                                val newCount = summary.countOfBadges - 1
                                if (newCount > 0) summary.copy(countOfBadges = newCount) else null
                            } else {
                                summary
                            }
                        }
                        if (updatedRecords.isEmpty()) {
                            null
                        } else {
                            item.copy(
                                records = updatedRecords,
                                badgesSummaryList = updatedSummaryList
                            )
                        }
                    } else {
                        item
                    }
                }

                val updatedRemovedList =
                    _state.value.badgesToBeRemoved.filterNot { it.id == updatedRecord.id }

                _state.update {
                    it.copy(
                        badgesToBeAwarded = updatedAwardedList,
                        badgesToBeRemoved = updatedRemovedList,
                        badgeDistributedHistory = _state.value.badgeDistributedHistory + updatedRecord
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private fun cancelDistributionOfBadge(record: BadgeRecord) {
        viewModelScope.launch {
            badgesRepository.markBadgeToBeGranted(
                record = record,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess {
                _state.update {
                    it.copy(
                        badgeDistributedHistory = _state.value.badgeDistributedHistory - record
                    )
                }
                loadRecords()
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                    )
                }
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

sealed interface BadgesListGrantingAction {
    data class OnBadgeDistributed(val record: BadgeRecord) : BadgesListGrantingAction
    data class OnBadgeCancelDistribution(val record: BadgeRecord) : BadgesListGrantingAction

}

data class BadgesListGrantingState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val discipline: Discipline = Discipline.Badges.BADGES,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val children: List<Child> = emptyList(),
    val badgesToBeAwarded: List<BadgesDisciplineToBeAwarded> = emptyList(),
    val badgesToBeRemoved: List<BadgeRecord> = emptyList(),
    val campBadges: List<Badge> = emptyList(),
    val badgeIdToDisciplineId: Map<Int, Int> = emptyMap(),
    val groups: List<Group> = emptyList(),
    val crews: List<Crew> = emptyList(),
    val badgeDistributedHistory: List<BadgeRecord> = emptyList()
)

data class BadgesDisciplineToBeAwarded(
    val disciplineId: Int,
    val records: List<BadgeRecord>,
    val badgesSummaryList: List<BadgesSummary>
)

data class BadgesSummary(
    val badgeId: Int,
    val countOfBadges: Int
)

data class BadgesDisciplineToBeAwardedWithName(
    val record: BadgesDisciplineToBeAwarded,
    val discipline: Discipline,
    val name: String,
    val summary: List<BadgesSummary>
)