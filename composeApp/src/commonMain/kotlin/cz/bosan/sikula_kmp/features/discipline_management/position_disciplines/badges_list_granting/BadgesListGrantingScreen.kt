package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BadgesListGrantingRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BadgesListGrantingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BadgesListGrantingListScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun BadgesListGrantingListScreen(
    state: BadgesListGrantingState,
    modifier: Modifier = Modifier,
    onAction: (BadgesListGrantingAction) -> Unit,
    onBackClick: () -> Unit,
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = { onBackClick() },
                discipline = state.discipline,
                keyboardController = null,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                showState = false,
                showInfoIcon = false,
                submitRecords = {},
                unSubmitRecords = {}
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WrapBox(
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                content = {
                    Column {
                        LastDistributedBadge(
                            children = state.children,
                            record = state.badgeDistributedHistory.lastOrNull(),
                            badgeIdToDisciplineId = state.badgeIdToDisciplineId,
                            onUpdateRecord = {
                                onAction(
                                    BadgesListGrantingAction.OnBadgeCancelDistribution(
                                        it
                                    )
                                )
                            },
                        )
                        BadgeListGranting(
                            records = state.badgesToBeAwarded,
                            children = state.children,
                            onBadgeGrant = {
                                onAction(
                                    BadgesListGrantingAction.OnBadgeDistributed(
                                        it
                                    )
                                )
                            },
                            crews = state.crews,
                            campBadges = state.campBadges,
                        )
                        if (state.badgesToBeRemoved.isNotEmpty()) {
                            BadgeListRemovingItem(
                                records = state.badgesToBeRemoved,
                                onBadgeGrant = {
                                    onAction(
                                        BadgesListGrantingAction.OnBadgeDistributed(
                                            it
                                        )
                                    )
                                },
                                children = state.children,
                                crews = state.crews,
                                campBadges = state.campBadges,
                            )
                        }
                    }
                }
            )
        }
    }
}
