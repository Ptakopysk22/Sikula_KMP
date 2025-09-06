package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.crew_records

import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineFilterBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrewRecordsRoute(
    onBackClick: (Discipline) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrewRecordsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CrewRecordsScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CrewRecordsScreen(
    state: CrewRecordsState,
    modifier: Modifier = Modifier,
    onAction: (CrewRecordsAction) -> Unit,
    onBackClick: (Discipline) -> Unit,
) {
    val disciplineRowState: LazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            CrewRecordsTopBar(
                onBackClick = { onBackClick(state.discipline) },
                crew = state.crew,
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
                        DisciplineFilterBar(
                            items = state.disciplines,
                            clickedDiscipline = state.discipline,
                            onItemClick = { onAction(CrewRecordsAction.OnFilterItemSelected(it)) },
                            disciplineRowState = disciplineRowState
                        )
                        if (state.warningMessage != null) {
                            Message(
                                text = state.warningMessage.asString(),
                                messageTyp = MessageTyp.WARNING
                            )
                        } else {
                            CrewRecordsList(
                                records = state.records,
                                leaders = state.leaders,
                                discipline = state.discipline,
                            )
                        }
                    }
                }
            )
        }
    }
}
