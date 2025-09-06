package cz.bosan.sikula_kmp.features.limo_counter.supply_section.cash_register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import kotlinx.serialization.Serializable

private interface CashRegister {
    @Serializable
    data object ProductList : CashRegister

}

fun NavGraphBuilder.cashRegister(
    navController: NavController,
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
) {
    navigation<Graph.CashRegister>(
        startDestination = CashRegister.ProductList,
    ) {

        composable<CashRegister.ProductList> {

        }


    }
}