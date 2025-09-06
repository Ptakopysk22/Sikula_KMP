package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.position_discipline_hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.discipline_management.components.DisciplineButton
import cz.bosan.sikula_kmp.features.drawer.Drawer
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PositionDisciplinesHubRoute(
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onDisciplineClick: (Discipline) -> Unit,
    onAboutAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PositionDisciplinesHubViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(PositionDisciplinesHubAction.ResetLogout)
            onLogout()
        }
    }

    PositionDisciplinesHubScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is PositionDisciplinesHubAction.OnDisciplineClick -> onDisciplineClick(action.discipline)
                else -> Unit
            }
            viewModel.onAction(action)
        },
        navigationBarActions = navigationBarActions,
        onAboutAppClick = onAboutAppClick
    )
}

@Composable
private fun PositionDisciplinesHubScreen(
    state: PositionDisciplineHubState,
    modifier: Modifier = Modifier,
    onAction: (PositionDisciplinesHubAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                currentLeader = state.currentLeader,
                onLogout = { onAction.invoke(PositionDisciplinesHubAction.OnLogoutClicked) },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onAboutAppClick = onAboutAppClick
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar =
            {
                MainTopBar(
                    currentLeader = state.currentLeader,
                    onProfileClick = { scope.launch { drawerState.open() } },
                    showProfile = true,
                    showButton = false,
                )
            },
            bottomBar = {
                NavigationBar(
                    role = state.currentLeader.leader.role,
                    currentDestination = Destination.POSITION_DISCIPLINES,
                    navigationBarActions = navigationBarActions
                )

            }
        ) { innerPadding ->
            WrapBox(
                isLoading = state.isLoading,
                errorMessage = null,
                content = {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(state.disciplines) { discipline ->
                            DisciplineButton(
                                discipline = discipline,
                                onClick = { clickedDiscipline ->
                                    onAction(
                                        PositionDisciplinesHubAction.OnDisciplineClick(
                                            clickedDiscipline
                                        )
                                    )
                                },
                                disciplineState = state.disciplineStates.find { it.discipline.id == discipline.id }?.dayRecordsState,
                                isIconGray = (state.currentLeader.leader.role == Role.GAME_MASTER || state.currentLeader.leader.role == Role.DIRECTOR),
                            )
                        }
                    }
                }
            )
        }
    }
}
