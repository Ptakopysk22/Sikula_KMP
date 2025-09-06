package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.domain.HandleBackPress
import cz.bosan.sikula_kmp.core.domain.PreventBackNavigation
import cz.bosan.sikula_kmp.core.presentation.components.FloatingActionButton
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.features.discipline_management.components.QuitTimerAlertDialog
import cz.bosan.sikula_kmp.features.discipline_management.components.TimerComponent
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add
import sikula_kmp.composeapp.generated.resources.in_line
import sikula_kmp.composeapp.generated.resources.on_the_trail
import sikula_kmp.composeapp.generated.resources.timer
import sikula_kmp.composeapp.generated.resources.with_barriers
import sikula_kmp.composeapp.generated.resources.without_barriers
import sikula_kmp.composeapp.generated.resources.zero_time

@Composable
fun TrailRecordingRoute(
    onBackClick: (Discipline) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrailRecordingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TrailRecordingScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun TrailRecordingScreen(
    state: TrailRecordingState,
    modifier: Modifier = Modifier,
    onAction: (TrailRecordingAction) -> Unit,
    onBackClick: (Discipline) -> Unit,
) {
    val childBeforeStartListState = rememberLazyListState()
    val childOnTrailListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isPanelVisible by remember { mutableStateOf(false) }
    val shouldScrollChildrenBeforeStartToTop = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    PreventBackNavigation(state.isCentralTimerRunning)

    HandleBackPress {
        if (state.isCentralTimerRunning) {
            showDialog = true
        } else {
            onBackClick(Discipline.Individual.TRAIL)
        }
    }

    LaunchedEffect(state.isChildrenInitialization) {
        if (!state.isChildrenInitialization) {
            isPanelVisible = true
        }
    }

    LaunchedEffect(shouldScrollChildrenBeforeStartToTop.value) {
        if (shouldScrollChildrenBeforeStartToTop.value) {
            childBeforeStartListState.animateScrollToItem(0)
            shouldScrollChildrenBeforeStartToTop.value = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = {
                    if (state.isCentralTimerRunning) {
                        showDialog = true
                    } else {
                        onBackClick(Discipline.Individual.TRAIL)
                    }
                },
                discipline = Discipline.Individual.TRAIL,
                keyboardController = keyboardController,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                submitRecords = {},
                unSubmitRecords = {},
                showInfoIcon = state.isChildrenInitialization,
                showInfoChange = { onAction(TrailRecordingAction.OnShowInfoChange) }
            )
        }, floatingActionButton = {
            if (state.isChildrenInitialization) {
                FloatingActionButton(
                    onClick = { onAction(TrailRecordingAction.OnUpdateInitializingState) },
                    icon = painterResource(Res.drawable.timer),
                    description = stringResource(Res.string.description_add)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WrapBox(
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (state.isChildrenInitialization) {
                            if (state.infoMessage != null && state.showInfo) {
                                Message(
                                    text = state.infoMessage.asString(),
                                    messageTyp = MessageTyp.INFO
                                )
                            }
                            if (state.warningMessage != null) {
                                Message(
                                    text = state.warningMessage.asString(),
                                    messageTyp = MessageTyp.INFO
                                )
                            }
                            Switcher(
                                selectedButtonIndex = 0,
                                firstLabel = stringResource(Res.string.with_barriers),
                                secondLabel = stringResource(Res.string.without_barriers),
                                onFirstClick = {
                                    onAction(
                                        TrailRecordingAction.OnChangeCountsForImprovementChildren(
                                            true
                                        )
                                    )
                                },
                                onSecondClick = {
                                    onAction(
                                        TrailRecordingAction.OnChangeCountsForImprovementChildren(
                                            false
                                        )
                                    )
                                },
                                arrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            TrailReorderableList(
                                children = state.childrenOnStart,
                                targetImprovements = state.childTargetImprovements,
                                onChildDismiss = { child, index, comment ->
                                    onAction(
                                        TrailRecordingAction.OnChildDismiss(
                                            child = child,
                                            index = index,
                                            comment = comment
                                        )
                                    )
                                },
                                onChildMove = { from, to ->
                                    onAction(
                                        TrailRecordingAction.OnChildRelocate(
                                            from, to
                                        )
                                    )
                                },
                                countsForImprovementMap = state.countsForImprovementMap,
                                onChangeCountsForImprovementMap = { childId, countsForImprovement ->
                                    onAction(
                                        TrailRecordingAction.OnChangeCountsForImprovementChild(
                                            childId,
                                            countsForImprovement
                                        )
                                    )
                                },
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column {
                                    if (state.warningMessage != null) {
                                        Message(
                                            text = state.warningMessage.asString(),
                                            messageTyp = MessageTyp.INFO
                                        )
                                    }
                                    LastTrailRecordBar(
                                        children = state.allChildren,
                                        record = state.finishedRecords.keys.maxByOrNull { it.timeStamp },
                                        onContinueRecording = { child, record ->
                                            onAction(
                                                TrailRecordingAction.OnContinueChildTimer(
                                                    child,
                                                    record
                                                )
                                            )
                                        },
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (state.centralTimerStart == null) stringResource(Res.string.zero_time) else {
                                                if (state.isCentralTimerRunning) {
                                                    TimerComponent(
                                                        startTime = state.centralTimerStart
                                                    )
                                                } else formatTrailTime(state.centralTimerDuration?.inWholeSeconds?.toInt()!!)
                                            },
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(bottom = 2.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            stringResource(Res.string.in_line),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    TrailRecordingListBeforeStart(
                                        children = state.childrenOnStart,
                                        scrollState = childBeforeStartListState,
                                        onStartRecording = {
                                            onAction(
                                                TrailRecordingAction.OnStartRecording(
                                                    it
                                                )
                                            )
                                        },
                                        modifier = Modifier.weight(0.35f)
                                    )
                                    AnimatedVisibility(
                                        visible = isPanelVisible,
                                        enter = slideInVertically(
                                            initialOffsetY = { fullHeight -> fullHeight },
                                            animationSpec = tween(durationMillis = 300)
                                        ),
                                        modifier = Modifier.zIndex(2f).weight(0.65f)
                                        ) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(1f),
                                            shape = RoundedCornerShape(
                                                topStart = 20.dp,
                                                topEnd = 20.dp
                                            ),
                                            color = MaterialTheme.colorScheme.background,
                                            shadowElevation = 30.dp,
                                            border = BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                        .padding(bottom = 2.dp, top = 8.dp),
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        stringResource(Res.string.on_the_trail),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                TrailRecordingListOnTrail(
                                                    childTimers = state.childrenOnTrail,
                                                    scrollState = childOnTrailListState,
                                                    onStopRecording = {
                                                        onAction(
                                                            TrailRecordingAction.OnStopRecording(
                                                                it
                                                            )
                                                        )
                                                    },
                                                    onRestart = {
                                                        onAction(
                                                            TrailRecordingAction.OnRestartChild(
                                                                it
                                                            )
                                                        )
                                                        shouldScrollChildrenBeforeStartToTop.value =
                                                            true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
    if (showDialog) {
        QuitTimerAlertDialog(
            onDismiss = { showDialog = false },
            discipline = Discipline.Individual.TRAIL
        )
    }
}
