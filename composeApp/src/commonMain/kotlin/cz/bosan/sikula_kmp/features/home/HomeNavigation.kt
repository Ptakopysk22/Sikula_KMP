package cz.bosan.sikula_kmp.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.morningExerciseDisciplines
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.positionDisciplines
import kotlinx.serialization.Serializable

private interface Home {
    @Serializable
    data object Root
}

fun NavGraphBuilder.home(
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onDestinationChange: (Destination, Discipline, Int) -> Unit,
    onAboutAppClick: () -> Unit,
) {
    navigation<Graph.Home>(
        startDestination = Home.Root,
    ) {
        composable<Home.Root> {
            HomeRoute(
                onLogout = onLogout,
                navigationBarActions = navigationBarActions,
                onAboutAppClick = onAboutAppClick,
                onRemainderItemClick = { discipline, campDay ->
                    if (discipline in morningExerciseDisciplines) {
                        onDestinationChange(Destination.MORNING_EXERCISE, discipline, campDay)
                    } else if (discipline in positionDisciplines) {
                        onDestinationChange(Destination.POSITION_DISCIPLINES, discipline, campDay)
                    } else {
                        onDestinationChange(Destination.POINTS_MANAGER, discipline, campDay)
                    }
                }
            )
        }
    }
}