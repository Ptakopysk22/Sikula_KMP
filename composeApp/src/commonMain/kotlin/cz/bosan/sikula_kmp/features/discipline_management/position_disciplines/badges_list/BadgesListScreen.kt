package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.CampDayCarousel
import cz.bosan.sikula_kmp.core.presentation.components.FloatingActionButton
import cz.bosan.sikula_kmp.core.presentation.components.GroupCategoryFilterRow
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineFilterBar
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.badgeDisciplines
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun BadgesListRoute(
    onAddClick: () -> Unit,
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: BadgesListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NegativePointsListScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        navigationBarActions = navigationBarActions,
        onAddClick = onAddClick,
        onChildRecordClick = onChildRecordClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun NegativePointsListScreen(
    state: BadgesListState,
    modifier: Modifier = Modifier,
    onAction: (BadgesListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: () -> Unit,
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState { state.campDuration }
    val keyboardController = LocalSoftwareKeyboardController.current
    val disciplineRowState: LazyListState = rememberLazyListState()

    val renderWithoutAnimation = remember { mutableStateOf(true) }

    LaunchedEffect(state.campDay) {
        val targetPage = state.campDay - 1
        if (pagerState.currentPage != targetPage) {
            if (renderWithoutAnimation.value) {
                pagerState.scrollToPage(targetPage)
                renderWithoutAnimation.value = false
            } else {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val newDay = pagerState.currentPage + 1
        if (newDay != state.campDay) {
            onAction(BadgesListAction.OnChangeCampDay(newDay))
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val targetPage = pagerState.currentPage.coerceIn(0, state.campDuration - 1)
            pagerState.animateScrollToPage(targetPage)
        }
    }

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
                unSubmitRecords = { }
            )
        },
        bottomBar = {
            NavigationBar(
                role = state.currentLeader.leader.role,
                currentDestination = Destination.POSITION_DISCIPLINES,
                navigationBarActions = navigationBarActions
            )
        }, floatingActionButton = {
            if (state.enabledUpdatingRecords == true && state.errorMessage == null) {
                FloatingActionButton(
                    onClick = { onAddClick() },
                    icon = rememberVectorPainter(Icons.Filled.Add),
                    description = stringResource(Res.string.description_add)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CampDayCarousel(
                campDay = state.campDay,
                campDuration = state.campDuration,
                changeWithoutAnimation = { renderWithoutAnimation.value = true },
                onDayChanged = { campDay ->
                    onAction(
                        BadgesListAction.OnChangeCampDay(
                            campDay
                        )
                    )
                },
            )
            DisciplineFilterBar(
                items = badgeDisciplines,
                clickedDiscipline = state.selectedDiscipline,
                onItemClick = { onAction(BadgesListAction.OnFilterItemSelected(it)) },
                modifier = Modifier.padding(vertical = 2.dp),
                disciplineRowState = disciplineRowState
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                userScrollEnabled = (pagerState.currentPage in 0..<state.campDuration)
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                    contentAlignment = Alignment.TopStart
                )
                {
                    WrapBox(
                        isLoading = state.isLoading,
                        errorMessage = state.errorMessage,
                        content = {
                            Column {
                                GroupCategoryFilterRow(
                                    selectedFilterCriteria = state.selectedFilterCriteria,
                                    onCriteriaSelected = {
                                        onAction(
                                            BadgesListAction.OnCriteriaSelected(
                                                it
                                            )
                                        )
                                    },
                                    groups = state.groups,
                                    trailCategories = state.trailCategories,
                                    selectedGroup = state.selectedGroup,
                                    selectedTrailCategory = state.selectedTrailCategory,
                                    onFilterItemSelected = {
                                        onAction(
                                            BadgesListAction.OnFilterItemSelected(
                                                it
                                            )
                                        )
                                    }
                                )
                                if (state.warningMessage != null) {
                                    Message(
                                        text = state.warningMessage.asString(),
                                        messageTyp = MessageTyp.WARNING
                                    )
                                } else {
                                    BadgeList(
                                        records = state.filteredRecords,
                                        children = state.children,
                                        onRecordClick = {
                                            onChildRecordClick(
                                                Discipline.Badges.BADGES,
                                                it
                                            )
                                        },
                                        groups = state.groups,
                                        crews = state.crews,
                                        trailCategories = state.trailCategories,
                                        leaders = state.leaders,
                                        enabledUpdateRecords = state.enabledUpdatingRecords,
                                        campBadges = state.campBadges,
                                        onRecordChange = {
                                            onAction(
                                                BadgesListAction.OnRecordUpdate(
                                                    it
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
