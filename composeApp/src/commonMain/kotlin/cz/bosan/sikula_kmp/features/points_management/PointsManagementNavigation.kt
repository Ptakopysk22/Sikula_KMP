package cz.bosan.sikula_kmp.features.points_management

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline.CountRecordingTeamDisciplineRoute
import cz.bosan.sikula_kmp.features.points_management.crew_discipline_points.CrewDisciplinePointsRoute
import cz.bosan.sikula_kmp.features.points_management.crew_points.CrewPointsRoute
import cz.bosan.sikula_kmp.features.points_management.discipline_points.DisciplinePointsRoute
import cz.bosan.sikula_kmp.features.points_management.point_dicipline_record_list.PointDisciplineRecordListRoute
import cz.bosan.sikula_kmp.features.points_management.points_hub.PointsHubRoute
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrewDto
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface PointsManagement {
    @Serializable
    data class PointsHub(val disciplineId: Int? = null, val campDay: Int? = null) : PointsManagement

    @Serializable
    data object DisciplinePoints : PointsManagement

    @Serializable
    data class CrewPoints(val crewJson: String, val campDay: Int) : PointsManagement

    @Serializable
    data class CrewDisciplinePoints(val crewJson: String, val disciplineId: String) :
        PointsManagement

    @Serializable
    data class PointDisciplineRecordList(val disciplineId: String, val campDay: Int? = null) :
        PointsManagement

    @Serializable
    data class PointsRecording(
        val disciplineId: String,
        val crewsJson: String?,
        val campDay: Int,
    ) : PointsManagement

}

fun NavGraphBuilder.pointsManagement(
    navController: NavController,
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
) {
    navigation<Graph.PointsManagementGraph>(
        startDestination = PointsManagement.PointsHub(null, null)
    ) {
        composable<PointsManagement.PointsHub> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getInt("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")

            if ((disciplineId != null && campDay != null) && (disciplineId != 0 && campDay != 0)) {

                LaunchedEffect(Unit) {
                    navController.navigate(
                        PointsManagement.PointDisciplineRecordList(
                            disciplineId = disciplineId.toString(),
                            campDay = campDay
                        )
                    )
                }
            } else {

                PointsHubRoute(
                    onLogout = onLogout,
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = onAboutAppClick,
                    onDisciplineClick = { discipline ->
                        if (discipline == Discipline.Team.ALL) {
                            navController.navigate((PointsManagement.DisciplinePoints))
                        } else {
                            navController.navigate(
                                PointsManagement.PointDisciplineRecordList(
                                    disciplineId = discipline.getId(),
                                    campDay = null
                                )
                            )
                        }
                    }
                )
            }
        }
        composable<PointsManagement.DisciplinePoints> {

            DisciplinePointsRoute(
                onCrewRecordClick = { campDay, crew ->
                    navController.navigate(
                        PointsManagement.CrewPoints(
                            crewJson = Json.encodeToString((crew.toCrewDto())),
                            campDay = campDay
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                navigationBarActions = navigationBarActions,
                onLogout = onLogout
            )

        }
        composable<PointsManagement.CrewPoints> { backStackEntry ->
            val crewJson = backStackEntry.arguments?.getString("crewJson") ?: ""
            val campDay = backStackEntry.arguments?.getInt("campDay") ?: ""
            val crewDto: CrewDto = Json.decodeFromString(crewJson)

            CrewPointsRoute(
                viewModel = koinViewModel(parameters = { parametersOf(crewDto, campDay) }),
                onRecordClick = { discipline, crew ->
                    navController.navigate(
                        PointsManagement.CrewDisciplinePoints(
                            crewJson = Json.encodeToString((crew.toCrewDto())),
                            disciplineId = discipline.getId()
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                navigationBarActions = navigationBarActions,
            )

        }
        composable<PointsManagement.CrewDisciplinePoints> { backStackEntry ->
            val crewJson = backStackEntry.arguments?.getString("crewJson") ?: ""
            val disciplineId = backStackEntry.arguments?.getString("disciplineId") ?: ""
            val crewDto: CrewDto = Json.decodeFromString(crewJson)
            val discipline = getDisciplineById(disciplineId)

            CrewDisciplinePointsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = koinViewModel(parameters = { parametersOf(discipline, crewDto) }),
            )
        }
        composable<PointsManagement.PointDisciplineRecordList> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId") ?: ""
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val discipline = getDisciplineById(disciplineId)

            PointDisciplineRecordListRoute(
                onBackClick = {
                    navController.navigate(
                        PointsManagement.PointsHub(
                            disciplineId = null,
                            campDay = null
                        )
                    ) {
                        popUpTo<PointsManagement.PointsHub> { inclusive = true }
                    }
                },
                viewModel = koinViewModel(parameters = { parametersOf(discipline, campDay) }),
                onAddClick = { discipline, crewsJson, campDay ->
                    navController.navigate(
                        PointsManagement.PointsRecording(
                            disciplineId = discipline.getId(),
                            crewsJson = crewsJson,
                            campDay = campDay
                        )
                    )
                },
                onCrewRecordClick = { discipline, crew ->
                    navController.navigate(
                        PointsManagement.CrewDisciplinePoints(
                            crewJson = Json.encodeToString((crew.toCrewDto())),
                            disciplineId = discipline.getId()
                        )
                    )
                },
                navigationBarActions = navigationBarActions,
            )
        }
        composable<PointsManagement.PointsRecording> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val crewsJson = backStackEntry.arguments?.getString("crewsJson")
            val crews: List<CrewDto> = crewsJson?.let { json ->
                Json.decodeFromString<List<CrewDto>>(json)
            } ?: emptyList()

            val discipline = getDisciplineById(disciplineId)

            CountRecordingTeamDisciplineRoute(
                viewModel = koinViewModel(parameters = {
                    parametersOf(
                        discipline,
                        crews,
                        campDay
                    )
                }),
                onBackClick = { discipline ->
                    navController.navigate(
                        PointsManagement.PointDisciplineRecordList(
                            disciplineId = discipline.getId(),
                        )
                    ) {
                        popUpTo<PointsManagement.PointDisciplineRecordList> { inclusive = true }
                    }
                }
            )
        }
    }
}