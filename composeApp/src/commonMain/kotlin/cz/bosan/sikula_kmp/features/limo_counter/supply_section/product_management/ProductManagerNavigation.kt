package cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_detail.ProductDetailRoute
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_list.ProductListRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface ProductManager {
    @Serializable
    data object ProductList : ProductManager

    @Serializable
    data class ProductDetail(val productId: Int?) : ProductManager

}

fun NavGraphBuilder.productManager(
    navController: NavController,
    onLogout: () -> Unit,
    onAboutAppClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
) {
    navigation<Graph.ProductManager>(
        startDestination = ProductManager.ProductList,
    ) {

        composable<ProductManager.ProductList> {

            ProductListRoute(
                onLogout = onLogout,
                onAddClick = { navController.navigate(ProductManager.ProductDetail(productId = newItemID)) },
                onProductClick = { navController.navigate(ProductManager.ProductDetail(productId = it.id)) },
                navigationBarActions = navigationBarActions,
                onAboutAppClick = onAboutAppClick
            )
        }

        composable<ProductManager.ProductDetail> { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")

            ProductDetailRoute(
                viewModel = koinViewModel(parameters = { parametersOf(productId) }),
                onBackClick = {
                    navController.navigate(ProductManager.ProductList) {
                        popUpTo<ProductManager.ProductList> { inclusive = true }
                    }
                },
            )
        }


    }
}