package cz.bosan.sikula_kmp.features.attendee_management.children_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun ChildrenListRoute(
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onChildClick: (Child) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChildrenListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(ChildrenListAction.ResetLogout)
            onLogout()
        }
    }

    ChildrenListScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is ChildrenListAction.OnChildSelected -> onChildClick(action.child)
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
private fun ChildrenListScreen(
    state: ChildrenListState,
    modifier: Modifier,
    onAction: (ChildrenListAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onAddClick: () -> Unit,
    onAboutAppClick: () -> Unit,
) {
    val childListState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                currentLeader = state.currentLeader,
                onLogout = { onAction.invoke(ChildrenListAction.OnLogoutClicked) },
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
                        navigationBarActions = navigationBarActions,
                    )
                }
            }, floatingActionButton = {
                if (state.errorMassage == null) {
                    FloatingActionButton(
                        onClick = { onAddClick() },
                        icon = rememberVectorPainter(image = Icons.Default.Add),
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
                    errorMessage = state.errorMassage,
                    content = {
                        ChildListSingleGroup(
                            children = state.children,
                            onChildClick = { onAction(ChildrenListAction.OnChildSelected(it)) },
                            rowScrollState = childListState,
                            trailCategory = state.trailCategories,
                            crews = state.crews
                        )
                    },
                )
            }
        }
    }
}