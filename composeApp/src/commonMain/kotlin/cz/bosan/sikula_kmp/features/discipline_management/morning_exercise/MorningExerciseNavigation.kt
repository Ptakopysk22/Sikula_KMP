package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline.CountRecordingRoute
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.morning_exercise_hub.MorningExerciseHubRoute
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list.RecordListRoute
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording.TrailRecordingRoute
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

private interface MorningExercise {
    @Serializable
    data class MorningExerciseHub(val disciplineId: Int? = null, val campDay: Int? = null) : MorningExercise

    @Serializable
    data class RecordsList(val disciplineId: String, val campDay: Int? = null) : MorningExercise

    @Serializable
    data class TrailRecording(val childrenJson: String?, val campDay: Int) : MorningExercise

    @Serializable
    data class CountRecording(
        val disciplineId: String,
        val childrenJson: String?,
        val campDay: Int,
    ) : MorningExercise

}

fun NavGraphBuilder.morningExercise(
    navController: NavController,
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onChildRecordsClick: (Int, String) -> Unit,
    onAboutAppClick: () -> Unit,
) {
    navigation<Graph.MorningExerciseGraph>(
        startDestination =  MorningExercise.MorningExerciseHub(null, null),
    ) {
        composable<MorningExercise.MorningExerciseHub> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getInt("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")

            if ((disciplineId != null && campDay != null) && (disciplineId != 0 && campDay!= 0)) {

                LaunchedEffect(Unit) {
                    navController.navigate(
                        MorningExercise.RecordsList(
                            disciplineId = disciplineId.toString(),
                            campDay = campDay
                        )
                    )
                }
            } else {

            MorningExerciseHubRoute(
                onLogout = onLogout,
                navigationBarActions = navigationBarActions,
                onAboutAppClick = onAboutAppClick,
                onDisciplineClick = { discipline ->
                    navController.navigate(
                        MorningExercise.RecordsList(
                            disciplineId = discipline.getId(),
                            campDay = null
                        )
                    )
                }
            )}
        }
        composable<MorningExercise.RecordsList> { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getString("disciplineId")
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val discipline = getDisciplineById(disciplineId)

            RecordListRoute(
                viewModel = koinViewModel(parameters = { parametersOf(discipline, campDay) }),
                onAddClick = { discipline, children, campDay ->
                    if (discipline == Discipline.Individual.TRAIL) {
                        navController.navigate(
                            MorningExercise.TrailRecording(
                                childrenJson = children,
                                campDay = campDay
                            )
                        )
                    } else {
                        navController.navigate(
                            MorningExercise.CountRecording(
                                disciplineId = discipline.getId(),
                                childrenJson = children,
                                campDay = campDay
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
                onBackClick = {
                    navController.navigate(MorningExercise.MorningExerciseHub(null, null)) {
                        popUpTo<MorningExercise.MorningExerciseHub> { inclusive = true }
                    }
                }
            )

        }
        composable<MorningExercise.CountRecording> { backStackEntry ->
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
                        MorningExercise.RecordsList(
                            disciplineId = discipline.getId(),
                        )
                    ) {
                        popUpTo<MorningExercise.RecordsList> { inclusive = true }
                    }
                }
            )
        }
        composable<MorningExercise.TrailRecording> { backStackEntry ->
            val campDay = backStackEntry.arguments?.getInt("campDay")
            val childrenJson = backStackEntry.arguments?.getString("childrenJson")
            val children: List<Child> = childrenJson?.let { json ->
                Json.decodeFromString<List<LightChild>>(json).map { childJson ->
                    childJson.toChild()
                }
            } ?: emptyList()

            TrailRecordingRoute(
                viewModel = koinViewModel(parameters = {
                    parametersOf(
                        children,
                        campDay
                    )
                }),
                onBackClick = {
                    navController.navigate(
                        MorningExercise.RecordsList(
                            disciplineId = Discipline.Individual.TRAIL.getId(),
                        )
                    ) {
                        popUpTo<MorningExercise.RecordsList> { inclusive = true }
                    }
                }
            )
        }
    }
}