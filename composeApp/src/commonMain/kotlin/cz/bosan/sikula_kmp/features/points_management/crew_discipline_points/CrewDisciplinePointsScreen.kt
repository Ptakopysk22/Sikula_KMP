package cz.bosan.sikula_kmp.features.points_management.crew_discipline_points

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.points_management.components.PointsTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrewDisciplinePointsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrewDisciplinePointsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CrewDisciplinePointsScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CrewDisciplinePointsScreen(
    state: CrewDisciplinePointsState,
    modifier: Modifier = Modifier,
    onAction: (CrewDisciplinePointsAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            PointsTopBar(
                onBackClick = onBackClick,
                discipline = state.discipline,
                crew = state.crew,
                isFirstScreen = false,
                currentLeader = state.currentLeader,
                onProfileClick = {},
                keyboardController = keyboardController,
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
                        if (state.warningMessage != null) {
                            Message(
                                text = state.warningMessage.asString(),
                                messageTyp = MessageTyp.WARNING
                            )
                        } else {
                            PointRecordListCrewDiscipline(
                                records = state.records,
                            )
                        }
                    }
                }
            )
        }
    }
}
