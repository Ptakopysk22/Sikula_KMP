package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_record_list

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
import androidx.compose.runtime.setValue
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
import cz.bosan.sikula_kmp.core.presentation.components.SwipeDismissConfirmationDialog
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.features.discipline_management.components.TeamRecordList
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun BoatRaceRecordListRoute(
    onAddClick: (String?, Int) -> Unit,
    onCrewRecordClick: (Crew) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: BoatRaceRecordListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BoatRaceRecordListScreen(
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
private fun BoatRaceRecordListScreen(
    state: BoatRaceRecordListState,
    modifier: Modifier = Modifier,
    onAction: (BoatRaceRecordListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: (String?, Int) -> Unit,
    onCrewRecordClick: (Crew) -> Unit,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState { state.campDuration }
    val keyboardController = LocalSoftwareKeyboardController.current

    val renderWithoutAnimation = remember { mutableStateOf(true) }
    val showPickerMap = remember { mutableStateMapOf<Int, Boolean>() }
    var showDialog by remember { mutableStateOf(false) }
    var crewToNull by remember { mutableStateOf(Crew.EMPTY) }
    var recordToNull by remember { mutableStateOf(TeamDisciplineRecord.EMPTY) }
    var commentToNull by remember { mutableStateOf("") }

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
            onAction(BoatRaceRecordListAction.OnChangeCampDay(newDay))
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
            onAddClick(state.crewsWithoutRecord, state.campDay)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = { onBackClick() },
                discipline = Discipline.Team.BOAT_RACE,
                keyboardController = keyboardController,
                dayRecordsState = state.dayRecordsState,
                role = state.currentLeader.leader.role,
                isDisciplineMaster = (state.isCurrentLeaderBoatRaceMaster),
                showState = true,
                showInfoIcon = false,
                submitRecords = {
                    if (state.currentLeader.leader.role == Role.GAME_MASTER) {
                        onAction(BoatRaceRecordListAction.OnSubmitRecordsByGameMaster)
                    } else {
                        onAction(BoatRaceRecordListAction.OnSubmitRecords)
                    }
                },
                unSubmitRecords = { },
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
                    onClick = { onAction(BoatRaceRecordListAction.OnCrewsWithoutRecords) },
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
                        BoatRaceRecordListAction.OnChangeCampDay(
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
                                when {
                                    state.warningMessage != null -> {
                                        Message(
                                            text = state.warningMessage.asString(),
                                            messageTyp = MessageTyp.WARNING
                                        )
                                    }

                                    else ->
                                        TeamRecordList(
                                            records = state.records,
                                            crews = state.crews,
                                            onRecordClick = {
                                                onCrewRecordClick(
                                                    it
                                                )
                                            },
                                            onRecordChange = { record, value, comment ->
                                                if (value == null) {
                                                    showDialog = true
                                                    crewToNull =
                                                        state.crews.find { it.id == record.crewId }
                                                            ?: Crew.EMPTY
                                                    recordToNull = record
                                                    commentToNull = comment
                                                } else {
                                                    onAction(
                                                        BoatRaceRecordListAction.OnRecordUpdate(
                                                            record,
                                                            value,
                                                            comment
                                                        )
                                                    )
                                                    showPickerMap[record.id!!] = false
                                                }
                                            },
                                            leaders = state.leaders,
                                            enabledUpdateRecords = state.enabledUpdatingRecords,
                                            discipline = Discipline.Team.BOAT_RACE,
                                            showTimePickers = showPickerMap,
                                            onShowTimePickerChange = { id ->
                                                val current = showPickerMap[id] ?: false
                                                showPickerMap[id] = !current
                                            },
                                            onUpdateTimeClick = {
                                                    id ->
                                                showPickerMap[id] = true
                                            },
                                            onUpdateCountsForImprovement = { record, value ->
                                                onAction(
                                                    BoatRaceRecordListAction.OnUpdateCountsForImprovement(
                                                        record,
                                                        value
                                                    )
                                                )
                                            },
                                        )
                                }
                            }
                        }
                    )
                }
            }
            if (showDialog) {
                SwipeDismissConfirmationDialog(
                    itemName = crewToNull.name,
                    onCancel = {
                        crewToNull = Crew.EMPTY
                        recordToNull = TeamDisciplineRecord.EMPTY
                        commentToNull = ""
                        showDialog = false
                    },
                    onConfirm = {
                        onAction(
                            BoatRaceRecordListAction.OnRecordUpdate(
                                recordToNull,
                                null,
                                it
                            )
                        )
                        showDialog = false
                        showPickerMap.keys.forEach { key ->
                            showPickerMap[key] = false
                        }
                    },
                    comment = commentToNull
                )
            }
        }
    }
}