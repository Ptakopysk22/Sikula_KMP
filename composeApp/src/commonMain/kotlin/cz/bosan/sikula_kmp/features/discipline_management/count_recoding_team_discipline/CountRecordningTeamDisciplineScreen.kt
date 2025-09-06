package cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline

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
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CountRecordingTeamDisciplineRoute(
    onBackClick: (Discipline) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountRecordingTeamDisciplineViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CountRecordingTeamDisciplineScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CountRecordingTeamDisciplineScreen(
    state: CountRecordingTeamDisciplineState,
    modifier: Modifier = Modifier,
    onAction: (CountRecordingTeamDisciplineAction) -> Unit,
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
                    onAction(CountRecordingTeamDisciplineAction.OnFillRecord("", Crew.EMPTY, ""))
                    onBackClick(state.discipline)
                },
                discipline = state.discipline,
                keyboardController = keyboardController,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                submitRecords = {},
                unSubmitRecords = {},
                showInfoIcon = true,
                showInfoChange = { onAction(CountRecordingTeamDisciplineAction.OnShowInfoChange) }
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
                        LastFillRecordBarTeamDiscipline(
                            discipline = state.discipline,
                            crew = state.lastFillCrew,
                            record = state.lastFillRecord,
                            onUpdateRecord = {
                                onAction(CountRecordingTeamDisciplineAction.OnUpdateLastRecord(it))
                            },
                        )
                        key(state.crews) {
                            CountRecodingListTeamDiscipline(
                                crews = state.crews,
                                scrollState = recordListState,
                                onFillRecord = { value, crew, comment ->
                                    onAction(
                                        CountRecordingTeamDisciplineAction.OnFillRecord(
                                            value = value,
                                            crew = crew,
                                            comment = comment
                                        )
                                    )
                                },
                                discipline = state.discipline,
                                onDoneClick = {
                                    onAction(
                                        CountRecordingTeamDisciplineAction.OnFillRecord(
                                            "",
                                            Crew.EMPTY,
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
