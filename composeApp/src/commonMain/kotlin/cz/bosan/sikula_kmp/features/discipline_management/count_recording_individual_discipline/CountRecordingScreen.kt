package cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CountRecordingRoute(
    onBackClick: (Discipline) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountRecordingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CountRecordingScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CountRecordingScreen(
    state: CountRecordingState,
    modifier: Modifier = Modifier,
    onAction: (CountRecordingAction) -> Unit,
    onBackClick: (Discipline) -> Unit,
) {
    val recordListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = {
                    onAction(CountRecordingAction.OnFillRecord("", Child.EMPTY, ""))
                    onBackClick(state.discipline)
                },
                discipline = state.discipline,
                keyboardController = keyboardController,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                submitRecords = {},
                unSubmitRecords = {},
                showInfoIcon = true,
                showInfoChange = { onAction(CountRecordingAction.OnShowInfoChange) }
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
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
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
                        LastFillRecordBar(
                            discipline = state.discipline,
                            child = state.lastFillChild,
                            record = state.lastFillRecord,
                            onUpdateRecord = {
                                onAction(CountRecordingAction.OnUpdateLastRecord(it))
                            },
                        )
                        key(state.children) {
                            CountRecodingList(
                                children = state.children,
                                scrollState = recordListState,
                                onFillRecord = { value, child, comment ->
                                    onAction(
                                        CountRecordingAction.OnFillRecord(
                                            value = value,
                                            child = child,
                                            comment = comment
                                        )
                                    )
                                },
                                discipline = state.discipline,
                                onDoneClick = {
                                    onAction(
                                        CountRecordingAction.OnFillRecord(
                                            "",
                                            Child.EMPTY,
                                            ""
                                        )
                                    )
                                    onBackClick(state.discipline)
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}
