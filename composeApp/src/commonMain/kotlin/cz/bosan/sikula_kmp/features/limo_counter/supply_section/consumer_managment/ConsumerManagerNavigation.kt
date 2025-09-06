package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.NFCManager
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_detail.ConsumerDetailRoute
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_list.ConsumerListRoute
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.edit_deposit.EditDepositRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface ConsumerManager {
    @Serializable
    data object ConsumerList : ConsumerManager

    @Serializable
    data class ConsumerDetail(val consumerId: Int?) : ConsumerManager

    @Serializable
    data class EditDeposit(val consumerId: Int?) : ConsumerManager

}

fun NavGraphBuilder.consumerManager(
    navController: NavController,
    onLogout: () -> Unit,
    onAboutAppClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    nfcManager: NFCManager,
) {
    navigation<Graph.ConsumerManager>(
        startDestination = ConsumerManager.ConsumerList,
    ) {

        composable<ConsumerManager.ConsumerList> {

            ConsumerListRoute(
                onLogout = onLogout,
                onAddClick = { navController.navigate(ConsumerManager.ConsumerDetail(consumerId = newItemID)) },
                onConsumerClick = { navController.navigate(ConsumerManager.ConsumerDetail(consumerId = it.consumerId)) },
                navigationBarActions = navigationBarActions,
                onAboutAppClick = onAboutAppClick
            )
        }

        composable<ConsumerManager.ConsumerDetail> { backStackEntry ->
            val consumerId = backStackEntry.arguments?.getInt("consumerId")

            ConsumerDetailRoute(
                viewModel = koinViewModel(parameters = { parametersOf(consumerId) }),
                onBackClick = {
                    navController.navigate(ConsumerManager.ConsumerList) {
                        popUpTo<ConsumerManager.ConsumerList> { inclusive = true }
                    }
                },
                nfcManager = nfcManager,
                navigationBarActions = navigationBarActions,
                onAddConsumptionRecordClick = {},
                onAddDeposit = { navController.navigate(ConsumerManager.EditDeposit(it)) },
            )
        }

        composable<ConsumerManager.EditDeposit> { backStackEntry ->
            val consumerId = backStackEntry.arguments?.getInt("consumerId")

            EditDepositRoute(
                viewModel = koinViewModel(parameters = { parametersOf(consumerId) }),
                onBackClick = {
                    navController.navigate(ConsumerManager.ConsumerDetail(consumerId)) {
                        popUpTo<ConsumerManager.ConsumerDetail> { inclusive = true }
                    }
                },
            )
        }

    }
}