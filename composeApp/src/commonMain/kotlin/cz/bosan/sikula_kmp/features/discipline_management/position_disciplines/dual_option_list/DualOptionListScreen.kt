package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DualOptionListRoute(
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: DualOptionListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DualOptionListScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        navigationBarActions = navigationBarActions,
        onChildRecordClick = onChildRecordClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun DualOptionListScreen(
    state: DualOptionListState,
    modifier: Modifier = Modifier,
    onAction: (DualOptionListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = { onBackClick() },
                discipline = state.discipline,
                keyboardController = keyboardController,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                showState = false,
                showInfoIcon = false,
                submitRecords = {},
                unSubmitRecords = {}
            )
        },
        bottomBar = {
            NavigationBar(
                role = state.currentLeader.leader.role,
                currentDestination = Destination.POSITION_DISCIPLINES,
                navigationBarActions = navigationBarActions
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
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 2.dp),
                        contentAlignment = Alignment.TopStart
                    )
                    {
                        Column {
                            if (state.currentLeader.leader.role == Role.DIRECTOR || state.currentLeader.leader.role == Role.GAME_MASTER) {
                                DualOptionRecordListCampWithFilters(
                                    errorMessage = state.errorMessage,
                                    warningMessage = state.warningMessage,
                                    selectedFilterCriteria = state.selectedFilterCriteria,
                                    onFilterCriteriaSelected = {
                                        onAction(
                                            DualOptionListAction.OnCriteriaSelected(
                                                it
                                            )
                                        )
                                    },
                                    onFilterItemSelected = {
                                        onAction(
                                            DualOptionListAction.OnFilterItemSelected(
                                                it
                                            )
                                        )
                                    },
                                    onRecordClick = {
                                        onChildRecordClick(
                                            state.discipline,
                                            it
                                        )
                                    },
                                    groups = state.groups,
                                    selectedGroup = state.selectedGroup,
                                    crews = state.crews,
                                    trailCategories = state.trailCategories,
                                    selectedTrailCategory = state.selectedTrailCategory,
                                    children = state.children,
                                    records = state.filteredRecords,
                                    leaders = state.leaders,
                                    enabledUpdateRecords = state.enabledUpdatingRecords,
                                    onRecordValueChange = { record, value, comment ->
                                        onAction(
                                            DualOptionListAction.OnRecordUpdate(
                                                record,
                                                value,
                                                comment
                                            )
                                        )
                                    },
                                    onRecordCreated = { value, competitorId, quest ->
                                        onAction(
                                            DualOptionListAction.OnRecordCreate(
                                                value,
                                                competitorId,
                                                quest
                                            )
                                        )
                                    },
                                    discipline = state.discipline,
                                    selectedAgilityFilterCriteria = state.selectedAgilityFilterCriteria,
                                    changeAgilityFilterCriteria = {
                                        onAction(
                                            DualOptionListAction.OnChangeAgilityFilterCriteria(
                                                it
                                            )
                                        )
                                    },
                                    selectedAgilityItem = state.selectedAgilityItem,
                                    changeSelectedAgilityItem = {
                                        onAction(
                                            DualOptionListAction.OnChangeAgilitySelectedItem(
                                                it
                                            )
                                        )
                                    }
                                )
                            } else {
                                when {
                                    state.warningMessage != null -> {
                                        Message(
                                            text = state.warningMessage.asString(),
                                            messageTyp = MessageTyp.WARNING
                                        )
                                    }

                                    else ->
                                        DualOptionRecordListGroup(
                                            records = state.filteredRecords,
                                            children = state.children,
                                            onRecordClick = {
                                                onChildRecordClick(
                                                    state.discipline,
                                                    it
                                                )
                                            },
                                            trailCategories = state.trailCategories,
                                            leaders = state.leaders,
                                            discipline = state.discipline,
                                            enabledUpdateRecords = state.enabledUpdatingRecords,
                                            onRecordValueChange = { record, value, comment ->
                                                onAction(
                                                    DualOptionListAction.OnRecordUpdate(
                                                        record,
                                                        value,
                                                        comment
                                                    )
                                                )
                                            }, onRecordCreated = { value, competitorId, quest ->
                                                onAction(
                                                    DualOptionListAction.OnRecordCreate(
                                                        value,
                                                        competitorId,
                                                        quest
                                                    )
                                                )
                                            },
                                            selectedAgilityFilterCriteria = state.selectedAgilityFilterCriteria,
                                            changeAgilityFilterCriteria = {
                                                onAction(
                                                    DualOptionListAction.OnChangeAgilityFilterCriteria(
                                                        it
                                                    )
                                                )
                                            },
                                            selectedAgilityItem = state.selectedAgilityItem,
                                            changeSelectedAgilityItem = {
                                                onAction(
                                                    DualOptionListAction.OnChangeAgilitySelectedItem(
                                                        it
                                                    )
                                                )
                                            }
                                        )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
