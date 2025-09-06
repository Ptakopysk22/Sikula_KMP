package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list

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
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun RecordListRoute(
    onAddClick: (Discipline, String?, Int) -> Unit,
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: RecordListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecordListScreen(
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
private fun RecordListScreen(
    state: RecordListState,
    modifier: Modifier = Modifier,
    onAction: (RecordListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: (Discipline, String?, Int) -> Unit,
    onChildRecordClick: (Discipline, Child) -> Unit,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState { state.campDuration }
    val keyboardController = LocalSoftwareKeyboardController.current

    val renderWithoutAnimation = remember { mutableStateOf(true) }
    val showPickerMap = remember { mutableStateMapOf<Int, Boolean>() }
    var showDialog by remember { mutableStateOf(false) }
    var childToNull by remember { mutableStateOf(Child.EMPTY) }
    var recordToNull by remember { mutableStateOf(IndividualDisciplineRecord.EMPTY) }
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
            onAction(RecordListAction.OnChangeCampDay(newDay))
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val targetPage = pagerState.currentPage.coerceIn(0, state.campDuration - 1)
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(state.activeChildrenWithoutRecord) {
        state.activeChildrenWithoutRecord?.let {
            onAddClick(state.discipline, state.activeChildrenWithoutRecord, state.campDay)
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
                showState = true,
                showInfoIcon = false,
                submitRecords = {
                    if (state.currentLeader.leader.role == Role.GAME_MASTER) {
                        onAction(RecordListAction.OnSubmitRecordsByGameMaster)
                    } else {
                        onAction(RecordListAction.OnSubmitRecords)
                    }
                },
                unSubmitRecords = { onAction(RecordListAction.OnUnSubmitRecords) }
            )
        },
        bottomBar = {
            NavigationBar(
                role = state.currentLeader.leader.role,
                currentDestination = Destination.MORNING_EXERCISE,
                navigationBarActions = navigationBarActions
            )
        }, floatingActionButton = {
            if (state.enabledCreatingRecords == true) {
                FloatingActionButton(
                    onClick = { onAction(RecordListAction.OnChildrenWithoutRecords) },
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
                        RecordListAction.OnChangeCampDay(
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
                    modifier = Modifier.fillMaxSize().padding(top = 4.dp),
                    contentAlignment = Alignment.TopStart
                )
                {
                    WrapBox(
                        isLoading = state.isLoading,
                        errorMessage = state.errorMessage,
                        content = {
                            Column {
                                if (state.showCampsRecords == true) {
                                    RecordListCampWithFilters(
                                        errorMessage = state.errorMessage,
                                        warningMessage = state.warningMessage,
                                        selectedFilterCriteria = state.selectedFilterCriteria,
                                        onFilterCriteriaSelected = {
                                            onAction(
                                                RecordListAction.OnCriteriaSelected(
                                                    it
                                                )
                                            )
                                        },
                                        onFilterItemSelected = {
                                            onAction(
                                                RecordListAction.OnFilterItemSelected(
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
                                        children = state.allChildren,
                                        records = state.filteredRecords,
                                        leaders = state.leaders,
                                        enabledUpdateRecords = state.enabledUpdatingRecords,
                                        discipline = state.discipline,
                                        onRecordValueChange = { record, value, comment ->
                                            if (value == null) {
                                                showDialog = true
                                                childToNull =
                                                    state.allChildren.find { it.id == record.competitorId }
                                                        ?: Child.EMPTY
                                                recordToNull = record
                                                commentToNull = comment
                                            } else {
                                                onAction(
                                                    RecordListAction.OnRecordUpdate(
                                                        record,
                                                        value,
                                                        comment
                                                    )
                                                )
                                                showPickerMap[record.id!!] = false
                                            }
                                        },
                                        dayRecordStates = state.dayRecordStateAllGroups,
                                        showTimePickers = showPickerMap,
                                        onShowTimePickerChange = { id ->
                                            val current = showPickerMap[id] ?: false
                                            showPickerMap[id] = !current
                                        },
                                        onUpdateTimeClick = { id ->
                                            showPickerMap[id] = true
                                        },
                                        onUpdateCountsForImprovement = { record, value ->
                                            onAction(
                                                RecordListAction.OnUpdateCountsForImprovement(
                                                    record,
                                                    value
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
                                            RecordListSingleGroup(
                                                records = state.filteredRecords,
                                                children = state.groupChildren,
                                                onRecordClick = {
                                                    onChildRecordClick(
                                                        state.discipline,
                                                        it
                                                    )
                                                },
                                                trailCategories = state.trailCategories,
                                                leaders = state.leaders,
                                                enabledUpdateRecords = state.enabledUpdatingRecords,
                                                discipline = state.discipline,
                                                onRecordValueChange = { record, value, comment ->
                                                    if (value == null) {
                                                        showDialog = true
                                                        childToNull =
                                                            state.allChildren.find { it.id == record.competitorId }
                                                                ?: Child.EMPTY
                                                        recordToNull = record
                                                        commentToNull = comment
                                                    } else {
                                                        onAction(
                                                            RecordListAction.OnRecordUpdate(
                                                                record,
                                                                value,
                                                                comment
                                                            )
                                                        )
                                                        showPickerMap[record.id!!] = false
                                                    }
                                                },
                                                showTimePickers = showPickerMap,
                                                onShowTimePickerChange = { id ->
                                                    val current = showPickerMap[id] ?: false
                                                    showPickerMap[id] = !current
                                                },
                                                onUpdateTimeClick = { id ->
                                                    showPickerMap[id] = true
                                                },
                                                onUpdateCountsForImprovement = { record, value ->
                                                    onAction(
                                                        RecordListAction.OnUpdateCountsForImprovement(
                                                            record,
                                                            value
                                                        )
                                                    )
                                                },
                                            )
                                    }
                                }
                            }
                        }
                    )
                }
            }
            if (showDialog) {
                SwipeDismissConfirmationDialog(
                    itemName = childToNull.nickName,
                    onCancel = {
                        childToNull = Child.EMPTY
                        recordToNull = IndividualDisciplineRecord.EMPTY
                        commentToNull = ""
                        showDialog = false
                    },
                    onConfirm = {
                        onAction(
                            RecordListAction.OnRecordUpdate(
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