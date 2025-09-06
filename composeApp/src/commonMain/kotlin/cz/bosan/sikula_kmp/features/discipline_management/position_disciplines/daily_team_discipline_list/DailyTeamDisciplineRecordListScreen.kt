package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_team_discipline_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.features.discipline_management.components.TeamRecordList
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun DailyTeamDisciplineRecordListRoute(
    onAddClick: (Discipline, String?, Int) -> Unit,
    onCrewRecordClick: (Discipline, Crew) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: DailyTeamDisciplineRecordListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DailyTeamDisciplineRecordListScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        navigationBarActions = navigationBarActions,
        onAddClick = onAddClick,
        onCrewRecordClick = onCrewRecordClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun DailyTeamDisciplineRecordListScreen(
    state: DailyTeamDisciplineRecordListState,
    modifier: Modifier = Modifier,
    onAction: (DailyTeamDisciplineRecordListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: (Discipline, String?, Int) -> Unit,
    onCrewRecordClick: (Discipline, Crew) -> Unit,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState { state.campDuration }
    val keyboardController = LocalSoftwareKeyboardController.current

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
            onAction(DailyTeamDisciplineRecordListAction.OnChangeCampDay(newDay))
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val targetPage = pagerState.currentPage.coerceIn(0, state.campDuration - 1)
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(state.crewsWithoutRecord) {
        state.crewsWithoutRecord?.let {
            onAddClick(state.discipline, state.crewsWithoutRecord, state.campDay)
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
                dayRecordsState = state.dayRecordsState,
                role = state.currentLeader.leader.role,
                isDisciplineMaster = state.isPositionMaster,
                showState = true,
                showInfoIcon = false,
                submitRecords = {
                    if (state.currentLeader.leader.role == Role.GAME_MASTER) {
                        onAction(DailyTeamDisciplineRecordListAction.OnSubmitRecordsByGameMaster)
                    } else {
                        onAction(DailyTeamDisciplineRecordListAction.OnSubmitRecords)
                    }
                },
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
            if (state.enabledCreatingRecords == true) {
                FloatingActionButton(
                    onClick = { onAction(DailyTeamDisciplineRecordListAction.OnCrewsWithoutRecords) },
                    icon = rememberVectorPainter(Icons.Default.Add),
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
                        DailyTeamDisciplineRecordListAction.OnChangeCampDay(
                            campDay
                        )
                    )
                },
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
                                if (state.warningMessage != null) {
                                    Message(
                                        text = state.warningMessage.asString(),
                                        messageTyp = MessageTyp.WARNING
                                    )
                                } else {
                                    TeamRecordList(
                                        records = state.records,
                                        crews = state.crews,
                                        onRecordClick = {
                                            onCrewRecordClick(
                                                state.discipline,
                                                it
                                            )
                                        },
                                        leaders = state.leaders,
                                        enabledUpdateRecords = state.enabledUpdatingRecords,
                                        onRecordChange = { record, value, comment ->
                                            onAction(
                                                DailyTeamDisciplineRecordListAction.OnRecordUpdate(
                                                    record,
                                                    value,
                                                    comment
                                                )
                                            )
                                        },
                                        discipline = state.discipline,
                                        showTimePickers = mutableStateMapOf(),
                                        onShowTimePickerChange = {},
                                        onUpdateTimeClick = {},
                                        onUpdateCountsForImprovement = { record, counts -> }
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
