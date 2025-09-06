package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline.CountRecordingTeamDisciplineRoute
import cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline.CountRecordingRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_record_list.BoatRaceRecordListRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording.BoatRaceRecordingRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.crew_records.CrewRecordsRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list.BadgesListRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting.BadgesListGrantingRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_discipline_list.DailyDisciplineListRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_team_discipline_list.DailyTeamDisciplineRecordListRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list.DualOptionListRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records.NegativePointsAllRecordsRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_recording.NegativePointsRecordingRoute
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.position_discipline_hub.PositionDisciplinesHubRoute
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDto
import cz.bosan.sikula_kmp.managers.camp_manager.data.toCrewDto
import cz.bosan.sikula_kmp.managers.children_manager.data.LightChild
import cz.bosan.sikula_kmp.managers.children_manager.data.toChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface PositionDisciplines {
    @Serializable
    data class PositionDisciplinesHub(val disciplineId: Int? = null, val campDay: Int? = null) :
        PositionDisciplines

    @Serializable
    data class SingleEventRecordsList(val disciplineId: String) : PositionDisciplines

    @Serializable
    data class DailyDisciplineRecordsList(val disciplineId: String, val campDay: Int? = null) :
        PositionDisciplines

    @Serializable
    data object BadgeRecordsList : PositionDisciplines

    @Serializable
    data object BadgeGrantingList : PositionDisciplines

    @Serializable
    data class NegativePointsRecording(val campDay: Int) : PositionDisciplines

    @Serializable
    data object NegativePointsAllRecords : PositionDisciplines

    @Serializable
    data class MorseRecording(
        val disciplineId: String,
        val childrenJson: String?,
        val campDay: Int,
    ) : PositionDisciplines

    @Serializable
    data class BoatRaceRecordsList(val campDay: Int? = null) : PositionDisciplines

    @Serializable
    data class DailyTeamDisciplineRecordsList(val disciplineId: String, val campDay: Int? = null) :
        PositionDisciplines

    @Serializable
    data class DailyTeamDisciplineRecording(
        val disciplineId: String,
        val crewsJson: String?,
        val campDay: Int,
    ) : PositionDisciplines

    @Serializable
    data class BoatRaceRecording(val crewsJson: String?, val campDay: Int) : PositionDisciplines

    @Serializable
    data class CrewRecords(val disciplineId: String, val crewJson: String) : PositionDisciplines
}

fun NavGraphBuilder.positionDisciplines(
    navController: NavController,
    onLogout: () -> Unit,
    onAboutAppClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onChildRecordsClick: (Int, String) -> Unit,
) {
    navigation<Graph.PositionDisciplinesGraph>(
        startDestination = PositionDisciplines.PositionDisciplinesHub(null, null),
    ) {
        composable<PositionDisciplines.PositionDisciplinesHub> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getInt("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")

            if ((disciplineId != null && campDay != null) && (disciplineId != 0 && campDay != 0)) {

                val discipline = getDisciplineById(disciplineId.toString())

                LaunchedEffect(Unit) {
                    if (discipline == Discipline.Individual.NEGATIVE_POINTS || discipline == Discipline.Individual.MORSE) {
                        navController.navigate(
                            PositionDisciplines.DailyDisciplineRecordsList(
                                disciplineId = disciplineId.toString(),
                                campDay = campDay
                            )
                        )
                    } else if (discipline == Discipline.Team.BOAT_RACE) {
                        navController.navigate(
                            PositionDisciplines.BoatRaceRecordsList(campDay = campDay)
                        )
                    } else if (discipline == Discipline.Team.QUIZ) {
                        navController.navigate(
                            PositionDisciplines.DailyTeamDisciplineRecordsList(
                                disciplineId = disciplineId.toString(),
                                campDay = campDay
                            )
                        )
                    }
                }
            } else {
                PositionDisciplinesHubRoute(
                    onLogout = onLogout,
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = onAboutAppClick,
                    onDisciplineClick = { discipline ->
                        if (discipline == Discipline.Individual.NEGATIVE_POINTS || discipline == Discipline.Individual.MORSE) {
                            navController.navigate(
                                PositionDisciplines.DailyDisciplineRecordsList(
                                    disciplineId = discipline.getId(),
                                    campDay = null
                                )
                            )
                        } else if (discipline == Discipline.Badges.BADGES) {
                            navController.navigate(
                                PositionDisciplines.BadgeRecordsList
                            )
                        } else if (discipline == Discipline.Team.BOAT_RACE) {
                            navController.navigate(
                                PositionDisciplines.BoatRaceRecordsList(campDay = null)
                            )
                        } else if (discipline == Discipline.Team.QUIZ) {
                            navController.navigate(
                                PositionDisciplines.DailyTeamDisciplineRecordsList(
                                    disciplineId = discipline.getId(),
                                    campDay = null
                                )
                            )
                        } else {
                            navController.navigate(
                                PositionDisciplines.SingleEventRecordsList(
                                    disciplineId = discipline.getId(),
                                )
                            )
                        }
                    }
                )
            }
        }
        composable<PositionDisciplines.SingleEventRecordsList> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val discipline = getDisciplineById(disciplineId)

            DualOptionListRoute(
                viewModel = koinViewModel(parameters = { parametersOf(discipline) }),
                onChildRecordClick = { discipline, child ->
                    val lightChild = LightChild(
                        id = child.id,
                        nickname = child.nickName,
                        trailCategoryId = child.trailCategoryId ?: -1
                    )
                    onChildRecordsClick(discipline.id, Json.encodeToString(lightChild))

                },
                navigationBarActions = navigationBarActions,
                onBackClick = {
                    navController.navigate(PositionDisciplines.PositionDisciplinesHub(null, null)) {
                        popUpTo<PositionDisciplines.PositionDisciplinesHub> { inclusive = true }
                    }
                }
            )

        }
        composable<PositionDisciplines.DailyDisciplineRecordsList> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val discipline = getDisciplineById(disciplineId)

            DailyDisciplineListRoute(
                viewModel = koinViewModel(parameters = { parametersOf(discipline, campDay) }),
                onBackClick = {
                    navController.navigate(PositionDisciplines.PositionDisciplinesHub(null, null)) {
                        popUpTo<PositionDisciplines.PositionDisciplinesHub> { inclusive = true }
                    }
                },
                onAddClick = { discipline, childrenJson, campday ->
                    if (discipline == Discipline.Individual.MORSE) {
                        navController.navigate(
                            PositionDisciplines.MorseRecording(
                                disciplineId = discipline.getId(),
                                childrenJson = childrenJson,
                                campDay = campday
                            )
                        )
                    } else {
                        navController.navigate(
                            PositionDisciplines.NegativePointsRecording(
                                campDay = campday
                            )
                        )
                    }
                },
                onChildRecordClick = { discipline, child ->
                    val lightChild = LightChild(
                        id = child.id,
                        nickname = child.nickName,
                        trailCategoryId = child.trailCategoryId ?: -1
                    )
                    onChildRecordsClick(discipline.id, Json.encodeToString(lightChild))
                },
                navigationBarActions = navigationBarActions,
                onListClick = {
                    navController.navigate(PositionDisciplines.NegativePointsAllRecords)
                }
            )
        }
        composable<PositionDisciplines.NegativePointsRecording> { backStackEntry ->
            val campDay = backStackEntry.arguments?.getInt("campDay")

            NegativePointsRecordingRoute(
                viewModel = koinViewModel(parameters = { parametersOf(campDay) }),
                onBackClick = {
                    navController.navigate(PositionDisciplines.DailyDisciplineRecordsList(Discipline.Individual.NEGATIVE_POINTS.getId())) {
                        popUpTo<PositionDisciplines.DailyDisciplineRecordsList> { inclusive = true }
                    }
                },
            )
        }
        composable<PositionDisciplines.NegativePointsAllRecords> {

            NegativePointsAllRecordsRoute(
                onBackClick = {
                    navController.navigate(PositionDisciplines.DailyDisciplineRecordsList(Discipline.Individual.NEGATIVE_POINTS.getId())) {
                        popUpTo<PositionDisciplines.DailyDisciplineRecordsList> { inclusive = true }
                    }
                },
            )
        }
        composable<PositionDisciplines.MorseRecording> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val childrenJson = backStackEntry.arguments?.getString("childrenJson")
            val children: List<Child> = childrenJson?.let { json ->
                Json.decodeFromString<List<LightChild>>(json).map { childJson ->
                    childJson.toChild()
                }
            } ?: emptyList()

            val discipline = getDisciplineById(disciplineId)

            CountRecordingRoute(
                viewModel = koinViewModel(parameters = {
                    parametersOf(
                        discipline,
                        children,
                        campDay
                    )
                }),
                onBackClick = { discipline ->
                    navController.navigate(
                        PositionDisciplines.DailyDisciplineRecordsList(
                            disciplineId = discipline.getId(),
                        )
                    ) {
                        popUpTo<PositionDisciplines.DailyDisciplineRecordsList> { inclusive = true }
                    }
                }
            )
        }
        composable<PositionDisciplines.BadgeRecordsList> {
            BadgesListRoute(
                onBackClick = {
                    navController.navigate(PositionDisciplines.PositionDisciplinesHub(null, null)) {
                        popUpTo<PositionDisciplines.PositionDisciplinesHub> { inclusive = true }
                    }
                },
                onAddClick = {
                    navController.navigate(
                        PositionDisciplines.BadgeGrantingList
                    )
                },
                onChildRecordClick = { discipline, child ->
                    val lightChild = LightChild(
                        id = child.id,
                        nickname = child.nickName,
                        trailCategoryId = child.trailCategoryId ?: -1
                    )
                    onChildRecordsClick(discipline.id, Json.encodeToString(lightChild))
                },
                navigationBarActions = navigationBarActions,
            )
        }
        composable<PositionDisciplines.BadgeGrantingList> {
            BadgesListGrantingRoute(
                onBackClick = {
                    navController.navigate(PositionDisciplines.BadgeRecordsList) {
                        popUpTo<PositionDisciplines.BadgeRecordsList> { inclusive = true }
                    }
                },
            )
        }
        composable<PositionDisciplines.BoatRaceRecordsList> { backStackEntry ->
            val campDay = backStackEntry.arguments?.getInt("campDay")
            BoatRaceRecordListRoute(
                viewModel = koinViewModel(parameters = { parametersOf(campDay) }),
                onAddClick = { crews, campDay ->
                    navController.navigate(
                        PositionDisciplines.BoatRaceRecording(
                            crewsJson = crews,
                            campDay = campDay
                        )
                    )
                },
                onCrewRecordClick = { crew ->
                    navController.navigate(
                        PositionDisciplines.CrewRecords(
                            disciplineId = Discipline.Team.BOAT_RACE.getId(),
                            crewJson = Json.encodeToString((crew.toCrewDto()))
                        )
                    )
                },
                navigationBarActions = navigationBarActions,
                onBackClick = {
                    navController.navigate(PositionDisciplines.PositionDisciplinesHub(null, null)) {
                        popUpTo<PositionDisciplines.PositionDisciplinesHub> { inclusive = true }
                    }
                }
            )
        }
        composable<PositionDisciplines.BoatRaceRecording> { backStackEntry ->
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val crewsJson = backStackEntry.arguments?.getString("crewsJson")
            val crews: List<CrewDto> = crewsJson?.let { json ->
                Json.decodeFromString<List<CrewDto>>(json)
            } ?: emptyList()

            BoatRaceRecordingRoute(
                viewModel = koinViewModel(parameters = {
                    parametersOf(
                        crews,
                        campDay
                    )
                }),
                onBackClick = {
                    navController.navigate(
                        PositionDisciplines.BoatRaceRecordsList(campDay = null)
                    ) {
                        popUpTo<PositionDisciplines.SingleEventRecordsList> { inclusive = true }
                    }
                }
            )
        }
        composable<PositionDisciplines.DailyTeamDisciplineRecordsList> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val discipline = getDisciplineById(disciplineId)

            DailyTeamDisciplineRecordListRoute(
                viewModel = koinViewModel(parameters = { parametersOf(discipline, campDay) }),
                onBackClick = {
                    navController.navigate(PositionDisciplines.PositionDisciplinesHub(null, null)) {
                        popUpTo<PositionDisciplines.PositionDisciplinesHub> { inclusive = true }
                    }
                },
                onAddClick = { discipline, crewsJson, campday ->
                    navController.navigate(
                        PositionDisciplines.DailyTeamDisciplineRecording(
                            disciplineId = discipline.getId(),
                            crewsJson = crewsJson,
                            campDay = campday
                        )
                    )
                },
                onCrewRecordClick = { discipline, crew ->
                    navController.navigate(
                        PositionDisciplines.CrewRecords(
                            disciplineId = discipline.getId(),
                            crewJson = Json.encodeToString((crew.toCrewDto()))
                        )
                    )
                },
                navigationBarActions = navigationBarActions,
            )
        }
        composable<PositionDisciplines.DailyTeamDisciplineRecording> { backStackEntry ->
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
                        PositionDisciplines.DailyTeamDisciplineRecordsList(
                            disciplineId = discipline.getId(),
                            null
                        )
                    ) {
                        popUpTo<PositionDisciplines.DailyTeamDisciplineRecordsList> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<PositionDisciplines.CrewRecords> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val crewJson = backStackEntry.arguments?.getString("crewJson") ?: ""
            val crewDto: CrewDto = Json.decodeFromString(crewJson)

            val discipline = getDisciplineById(disciplineId)

            CrewRecordsRoute(
                onBackClick = { discipline ->
                    navController.popBackStack()
                },
                viewModel = koinViewModel(parameters = { parametersOf(discipline, crewDto) }),
            )
        }
    }
}