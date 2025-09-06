package cz.bosan.sikula_kmp.features.discipline_management.child_records

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.managers.children_manager.data.LightChild
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface ChildRecordsGraph {
    @Serializable
    data class ChildRecords(
        val disciplineId: String,
        val childJson: String
    ) : ChildRecordsGraph
}

fun NavGraphBuilder.childRecordsGraph(
    navController: NavController,
) {
    navigation<Graph.ChildRecordsGraph>(
        startDestination = ChildRecordsGraph.ChildRecords("", ""),
    ) {
        composable<ChildRecordsGraph.ChildRecords> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val childJson = backStackEntry.arguments?.getString("childJson") ?: ""
            val lightChild: LightChild = Json.decodeFromString(childJson)

            val discipline = getDisciplineById(disciplineId.toString())

            ChildRecordsRoute(
                viewModel = koinViewModel(parameters = { parametersOf(discipline, lightChild) }),
                onBackClick = { discipline ->
                    navController.popBackStack()
                }
            )
        }
    }
}