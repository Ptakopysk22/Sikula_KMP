package cz.bosan.sikula_kmp.features.attendee_management.attendee_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.FloatingActionButton
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.drawer.Drawer
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun AttendeeListRoute(
    onLogout: () -> Unit,
    onAddClick: (Boolean) -> Unit,
    onLeaderClick: (Leader) -> Unit,
    onChildClick: (Child) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendeeListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(AttendeeListAction.ResetLogout)
            onLogout()
        }
    }

    AttendeeListScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is AttendeeListAction.OnLeaderSelected -> onLeaderClick(action.leader)
                is AttendeeListAction.OnChildSelected -> onChildClick(action.child)
                else -> Unit
            }
            viewModel.onAction(action)
        },
        navigationBarActions = navigationBarActions,
        onAddClick = onAddClick,
        onAboutAppClick = onAboutAppClick
    )
}

@Composable
private fun AttendeeListScreen(
    state: AttendeeListState,
    modifier: Modifier = Modifier,
    onAction: (AttendeeListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: (Boolean) -> Unit,
    onAboutAppClick: () -> Unit,
) {
    val pagerState = rememberPagerState { 2 }
    val leaderListState = rememberLazyListState()
    val childListState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(AttendeeListAction.OnTabSelected(pagerState.currentPage))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                currentLeader = state.currentLeader,
                onLogout = { onAction.invoke(AttendeeListAction.OnLogoutClicked) },
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
                state.currentLeader.leader.role.let {
                    NavigationBar(
                        role = it,
                        currentDestination = Destination.ATTENDEE_MANGER,
                        navigationBarActions = navigationBarActions
                    )
                }
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAddClick(state.selectedTabIndex == 1) },
                    icon = rememberVectorPainter(image = Icons.Default.Add),
                    description = stringResource(Res.string.description_add)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PageChanger(
                        selectedTabIndex = state.selectedTabIndex,
                        onFirstClick = { onAction(AttendeeListAction.OnTabSelected(0)) },
                        onSecondClick = { onAction(AttendeeListAction.OnTabSelected(1)) }
                    )
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp),
                        color = Color.Transparent,
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth().weight(1f)
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
                                        when (pageIndex) {
                                            0 -> {
                                                ChildListWithFilters(
                                                    warningMessage = state.warningMessage,
                                                    selectedFilterCriteria = state.selectedFilterCriteria,
                                                    onFilterCriteriaSelected = {
                                                        onAction(
                                                            AttendeeListAction.OnCriteriaSelected(
                                                                it
                                                            )
                                                        )
                                                    },
                                                    onFilterItemSelected = {
                                                        onAction(
                                                            AttendeeListAction.OnFilterItemSelected(
                                                                it
                                                            )
                                                        )
                                                    },
                                                    onChildSelected = {
                                                        onAction(
                                                            AttendeeListAction.OnChildSelected(
                                                                it
                                                            )
                                                        )
                                                    },
                                                    groups = state.groups,
                                                    selectedGroup = state.selectedGroup,
                                                    crews = state.crews,
                                                    trailCategories = state.trailCategories,
                                                    selectedTrailCategory = state.selectedTrailCategory,
                                                    children = state.children,
                                                    scrollState = childListState,
                                                )

                                            }

                                            1 -> {
                                                LeaderList(
                                                    leaders = state.leaders,
                                                    onLeaderClick = {
                                                        onAction(
                                                            AttendeeListAction.OnLeaderSelected(
                                                                leader = it
                                                            )
                                                        )
                                                    },
                                                    scrollState = leaderListState,
                                                    groups = state.groups
                                                )
                                            }
                                        }
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