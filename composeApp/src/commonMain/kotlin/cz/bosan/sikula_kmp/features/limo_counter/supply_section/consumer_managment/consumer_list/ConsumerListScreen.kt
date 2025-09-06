package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.FloatingActionButton
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.drawer.Drawer
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.Consumer
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun ConsumerListRoute(
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onConsumerClick: (Consumer) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsumerListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(ConsumerListAction.ResetLogout)
            onLogout()
        }
    }

    ConsumerListScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is ConsumerListAction.OnConsumerSelected -> onConsumerClick(action.consumer)
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
private fun ConsumerListScreen(
    state: ConsumerListState,
    modifier: Modifier,
    onAction: (ConsumerListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: () -> Unit,
    onAboutAppClick: () -> Unit,
) {
    val consumerListState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                currentLeader = state.currentLeader,
                onLogout = { onAction.invoke(ConsumerListAction.OnLogoutClicked) },
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
                    textInBox = "Konzumenti",
                    showProfile = true,
                    showButton = false
                )
            },
            bottomBar = {
                state.currentLeader.leader.role.let {
                    NavigationBar(
                        role = it,
                        currentDestination = Destination.CONSUMER_MANAGER,
                        navigationBarActions = navigationBarActions,
                    )
                }
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAddClick() },
                    icon = rememberVectorPainter(image = Icons.Default.Add),
                    description = stringResource(Res.string.description_add)
                )
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
                        ConsumerList(
                            //leaders = state.leaders,
                            //consumers = state.consumers,
                            consumerLeaders = state.consumers,
                            onConsumerClick = {
                                onAction(
                                    ConsumerListAction.OnConsumerSelected(
                                        it
                                    )
                                )
                            },
                            scrollState = consumerListState
                        )
                    }
                )
            }
        }
    }
}

