package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording

import cz.bosan.sikula_kmp.features.discipline_management.components.QuitTimerAlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import cz.bosan.sikula_kmp.features.discipline_management.components.TimerComponent
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.count_for_improvement
import sikula_kmp.composeapp.generated.resources.description_add
import sikula_kmp.composeapp.generated.resources.not_count_for_improvement
import sikula_kmp.composeapp.generated.resources.timer
import sikula_kmp.composeapp.generated.resources.zero_time

@Composable
fun BoatRaceRecordingRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BoatRaceRecordingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BoatRaceRecordingScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun BoatRaceRecordingScreen(
    state: BoatRaceRecordingState,
    modifier: Modifier = Modifier,
    onAction: (BoatRaceRecordingAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val crewListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isPanelVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    PreventBackNavigation(state.isCentralTimerRunning)

    HandleBackPress {
        if (state.isCentralTimerRunning) {
            showDialog = true
        } else {
            onBackClick()
        }
    }

    LaunchedEffect(state.isCrewsInitialization) {
        if (!state.isCrewsInitialization) {
            isPanelVisible = true
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
                        onBackClick()
                    }
                },
                discipline = Discipline.Team.BOAT_RACE,
                keyboardController = keyboardController,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                showInfoChange = { onAction(BoatRaceRecordingAction.OnShowInfoChange) },
                showInfoIcon = state.isCrewsInitialization,
                role = state.currentLeader.leader.role,
                submitRecords = {},
                unSubmitRecords = {},
            )
        }, floatingActionButton = {
            if (state.isCrewsInitialization) {
                FloatingActionButton(
                    onClick = { onAction(BoatRaceRecordingAction.OnUpdateInitializingState) },
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
                        if (state.isCrewsInitialization) {
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
                                firstLabel = stringResource(Res.string.count_for_improvement),
                                secondLabel = stringResource(Res.string.not_count_for_improvement),
                                onFirstClick = {
                                    onAction(
                                        BoatRaceRecordingAction.OnChangeCountsForImprovementCrews(
                                            true
                                        )
                                    )
                                },
                                onSecondClick = {
                                    onAction(
                                        BoatRaceRecordingAction.OnChangeCountsForImprovementCrews(
                                            false
                                        )
                                    )
                                },
                                arrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            BoatRaceReorderableList(
                                modifier = Modifier.zIndex(1f),
                                crews = state.crews,
                                targetImprovements = state.crewTargetImprovements,
                                countsForImprovementMap = state.countsForImprovementMap,
                                onChangeCountsForImprovementMap = { crewId, countsForImprovement ->
                                    onAction(
                                        BoatRaceRecordingAction.OnChangeCountsForImprovementCrew(
                                            crewId,
                                            countsForImprovement
                                        )
                                    )
                                },
                                onCrewDismiss = { crew, index, comment ->
                                    onAction(
                                        BoatRaceRecordingAction.OnCrewDismiss(
                                            crew = crew,
                                            index = index,
                                            comment = comment
                                        )
                                    )
                                },
                                onCrewMove = { newList ->
                                    onAction(
                                        BoatRaceRecordingAction.OnCrewRelocate(
                                            newList
                                        )
                                    )
                                },
                                crewsLocks = state.crewsLocks,
                                onLockChange = {
                                    onAction(
                                        BoatRaceRecordingAction.OnLockCrewChange(
                                            it
                                        )
                                    )
                                }
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (state.centralTimerStart == null) stringResource(
                                                Res.string.zero_time
                                            ) else {
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
                                    BoatRaceRecordingList(
                                        crews = state.crews,
                                        crewTimers = state.crewsInRace,
                                        finishedRecords = state.finishedRecords,
                                        scrollState = crewListState,
                                        onStartRecording = {
                                            onAction(
                                                BoatRaceRecordingAction.OnStartRecording(
                                                    it
                                                )
                                            )
                                        },
                                        onStopRecording = {
                                            onAction(
                                                BoatRaceRecordingAction.OnStopRecording(
                                                    it
                                                )
                                            )
                                        },
                                        onContinueRecording = { crew, record ->
                                            onAction(
                                                BoatRaceRecordingAction.OnContinueCrewTimer(
                                                    crew,
                                                    record
                                                )
                                            )
                                        },
                                        onRestart = {
                                            onAction(
                                                BoatRaceRecordingAction.OnRestartCrew(
                                                    it
                                                )
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        if (showDialog) {
            QuitTimerAlertDialog(
                onDismiss = { showDialog = false },
                discipline = Discipline.Team.BOAT_RACE
            )
        }
    }
}