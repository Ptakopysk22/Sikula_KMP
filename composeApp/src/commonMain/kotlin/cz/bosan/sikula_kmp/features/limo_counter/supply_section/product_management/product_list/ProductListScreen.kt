package cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_list

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
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.Product
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun ProductListRoute(
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAboutAppClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.logoutSuccessful) {
        if (state.logoutSuccessful) {
            viewModel.onAction(ProductListAction.ResetLogout)
            onLogout()
        }
    }

    ProductListScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is ProductListAction.OnProductSelected -> onProductClick(action.product)
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
private fun ProductListScreen(
    state: ProductListState,
    modifier: Modifier,
    onAction: (ProductListAction) -> Unit,
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
                onLogout = { onAction.invoke(ProductListAction.OnLogoutClicked) },
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
                    textInBox = "Položky",
                    showProfile = true,
                    showButton = false
                )
            },
            bottomBar = {
                state.currentLeader.leader.role.let {
                    NavigationBar(
                        role = it,
                        currentDestination = Destination.PRODUCT_MANAGER,
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
                        ProductList(
                            products = state.products,
                            onProductClick = {
                                onAction(
                                    ProductListAction.OnProductSelected(
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
