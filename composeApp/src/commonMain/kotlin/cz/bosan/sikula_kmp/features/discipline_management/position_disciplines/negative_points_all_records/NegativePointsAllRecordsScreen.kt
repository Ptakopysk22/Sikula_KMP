package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineTopBar
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NegativePointsAllRecordsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NegativePointsAllRecordsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NegativePointsAllRecordsScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun NegativePointsAllRecordsScreen(
    state: NegativePointsAllRecordsState,
    modifier: Modifier = Modifier,
    onAction: (NegativePointsAllRecordsAction) -> Unit,
    onBackClick: () -> Unit,
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            DisciplineTopBar(
                onBackClick = onBackClick,
                discipline = Discipline.Individual.NEGATIVE_POINTS,
                dayRecordsState = DayRecordsState.WITHOUT_STATE,
                role = state.currentLeader.leader.role,
                showState = false,
                showInfoIcon = false,
                keyboardController = LocalSoftwareKeyboardController.current,
                submitRecords = {},
                unSubmitRecords = {},
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        WorkedOffFilterRow(
                            selectedButtonIndex = state.selectedButtonIndex,
                            onFirstClick = {
                                onAction(
                                    NegativePointsAllRecordsAction.OnFilterItemSelected(
                                        0
                                    )
                                )
                            },
                            onSecondClick = {
                                onAction(
                                    NegativePointsAllRecordsAction.OnFilterItemSelected(
                                        1
                                    )
                                )
                            },
                            onThirdClick = {
                                onAction(
                                    NegativePointsAllRecordsAction.OnFilterItemSelected(
                                        2
                                    )
                                )
                            },
                        )
                        if (state.warningMessage != null) {
                            Message(
                                text = state.warningMessage.asString(),
                                messageTyp = MessageTyp.WARNING
                            )
                        } else {
                            NegativePointsAllRecordsList(
                                records = state.filteredRecords,
                                children = state.children,
                                onRecordClick = {},
                                onUpdateWorkedOff = { record, newValue ->
                                    onAction(
                                        NegativePointsAllRecordsAction.OnUpdateWorkedOff(
                                            record,
                                            newValue
                                        )
                                    )
                                },
                                crews = state.crews,
                                leaders = state.leaders,
                                enabledUpdateRecords = state.isPositionMaster,
                            )
                        }
                    }
                }
            )
        }
    }
}


