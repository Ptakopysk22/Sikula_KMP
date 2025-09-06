package cz.bosan.sikula_kmp.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.drawer.Drawer
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation

@Composable
fun HomeRoute(
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onRemainderItemClick: (Discipline, Int) -> Unit,
    onAboutAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(HomeAction.ResetLogout)
            onLogout()
        }
    }

    HomeScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        navigationBarActions = navigationBarActions,
        onRemainderItemClick = onRemainderItemClick,
        onAboutAppClick = onAboutAppClick
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onAction: (HomeAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onRemainderItemClick: (Discipline, Int) -> Unit,
    onAboutAppClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                currentLeader = state.currentLeader,
                onLogout = {
                    onAction.invoke(HomeAction.OnLogoutClicked)
                },
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
                    textInBox = stringResource(
                        Res.string.camp_day_format_abbreviation,
                        state.campDay
                    ),
                    onProfileClick = { scope.launch { drawerState.open() } },
                    showProfile = true,
                    showButton = false,
                )
            },
            bottomBar = {
                NavigationBar(
                    role = state.currentLeader.leader.role,
                    currentDestination = Destination.HOME,
                    navigationBarActions = navigationBarActions
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                WrapBox(
                    isLoading = state.isLoading,
                    errorMessage = state.errorMessage,
                    content = {
                        Column(modifier = Modifier.fillMaxSize()) {
                            PointsChart(
                                crews = state.crews,
                                pointRecords = state.pointRecords,
                                warningMessage = state.warningMessage,
                                role = state.currentLeader.leader.role,
                                selectedDate = state.selectedPointDay,
                                campDuration = state.campDuration,
                                onDayChange = { onAction(HomeAction.OnChangePointDay(it)) },
                                modifier = Modifier.weight(0.5f).padding(horizontal = 16.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth().weight(0.5f)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    CampAttendeesBox(
                                        campAttendees = state.attendeesCount,
                                        errorMessage = state.attendeesCountErrorMessage,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            end = 8.dp,
                                            bottom = 8.dp
                                        ),
                                    )
                                    BirthdayBox(
                                        birthdayUsers = state.birthDayUsers,
                                        errorMessage = state.birthdayErrorMessage,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 8.dp,
                                            bottom = 8.dp
                                        ),
                                    )
                                }
                                RemainderListBox(
                                    disciplineStates = state.disciplineStates,
                                    onClick = { discipline, campDay ->
                                        onRemainderItemClick(
                                            discipline,
                                            campDay
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                        .padding(
                                            start = 8.dp,
                                            top = 8.dp,
                                            end = 16.dp,
                                            bottom = 8.dp
                                        ),
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}