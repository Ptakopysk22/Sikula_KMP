package cz.bosan.sikula_kmp.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.domain.NFCManager
import cz.bosan.sikula_kmp.core.presentation.materialTheme.AppShapes
import cz.bosan.sikula_kmp.core.presentation.materialTheme.LocalExtendedColors
import cz.bosan.sikula_kmp.core.presentation.materialTheme.OutfitTypography
import cz.bosan.sikula_kmp.core.presentation.materialTheme.SetSystemBarsColor
import cz.bosan.sikula_kmp.core.presentation.materialTheme.lightColorSchema
import cz.bosan.sikula_kmp.core.presentation.materialTheme.localExtendedColors
import cz.bosan.sikula_kmp.features.about_app.aboutApp
import cz.bosan.sikula_kmp.features.attendee_management.attendeeManager
import cz.bosan.sikula_kmp.features.discipline_management.child_records.childRecordsGraph
import cz.bosan.sikula_kmp.features.home.home
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.morningExercise
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.positionDisciplines
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumerManager
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.productManager
import cz.bosan.sikula_kmp.features.points_management.pointsManagement
import cz.bosan.sikula_kmp.features.signin.signIn
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.cash_register.cashRegister
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
    nfcManager: NFCManager,
) {

    val appState by viewModel.state.collectAsStateWithLifecycle()
    val userRole = appState.currentLeader?.leader?.role

    val navigationBarActions = NavigationBarActions(navController = navController)

    fun onLogout() {
        navController.navigate(Graph.SignIn) {
            popUpTo(0)
            launchSingleTop = true
        }
    }

    fun navigateToAboutApp() {
        navController.navigate(Graph.AboutApp) {
            popUpTo<Graph.AboutApp> { inclusive = true }
        }
    }

    LaunchedEffect(appState.shouldLogout) {
        if (appState.shouldLogout) {
            onLogout()
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides localExtendedColors) {
        MaterialTheme(
            colorScheme = lightColorSchema,
            typography = OutfitTypography(),
            shapes = AppShapes
        ) {
            SetSystemBarsColor(
                statusBarColor = MaterialTheme.colorScheme.background,
                navigationBarColor = MaterialTheme.colorScheme.primary,
                isLight = false //MaterialTheme.colorScheme.background.luminance() > 0.5f
            )
            NavHost(
                navController = navController,
                startDestination = if (appState.isUserLoggedIn) Graph.Home else Graph.SignIn
            ) {
                signIn(
                    onSignInFlowFinished = {
                        navController.navigate(Graph.Home) {
                            popUpTo<Graph.SignIn> { inclusive = true }
                        }
                    }
                )
                home(
                    navigationBarActions = navigationBarActions,
                    onLogout = { onLogout() },
                    onAboutAppClick = { navigateToAboutApp() },
                    onDestinationChange = { destination, discipline, campDay ->
                        if (destination == Destination.MORNING_EXERCISE) {
                            navController.navigate(
                                Graph.MorningExerciseGraph(
                                    discipline.id,
                                    campDay
                                )
                            ) {
                                popUpTo<Graph.MorningExerciseGraph> { inclusive = true }
                            }
                        } else if (destination == Destination.POSITION_DISCIPLINES) {
                            navController.navigate(
                                Graph.PositionDisciplinesGraph(
                                    discipline.id,
                                    campDay
                                )
                            ) {
                                popUpTo<Graph.PositionDisciplinesGraph> { inclusive = true }
                            }
                        } else if (destination == Destination.POINTS_MANAGER) {
                            navController.navigate(
                                Graph.PointsManagementGraph(
                                    discipline.id,
                                    campDay
                                )
                            ) {
                                popUpTo<Graph.PointsManagementGraph> { inclusive = true }
                            }
                        }
                    }
                )
                aboutApp(onBackClick = {
                    navController.navigate(Graph.Home) {
                        popUpTo<Graph.Home> { inclusive = true }
                    }
                })
                attendeeManager(
                    navController = navController,
                    navigationBarActions = navigationBarActions,
                    onLogout = { onLogout() },
                    onAboutAppClick = { navigateToAboutApp() },
                    userRole = userRole ?: Role.NO_ROLE,
                )
                morningExercise(
                    navController = navController,
                    onLogout = { onLogout() },
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = { navigateToAboutApp() },
                    onChildRecordsClick = { disciplineId, lightChild ->
                        navController.navigate(
                            Graph.ChildRecordsGraph(
                                disciplineId.toString(),
                                lightChild
                            )
                        )
                    },
                )
                positionDisciplines(
                    navController = navController,
                    onLogout = { onLogout() },
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = { navigateToAboutApp() },
                    onChildRecordsClick = { disciplineId, lightChild ->
                        navController.navigate(
                            Graph.ChildRecordsGraph(
                                disciplineId.toString(),
                                lightChild
                            )
                        )
                    }
                )
                pointsManagement(
                    navController = navController,
                    onLogout = { onLogout() },
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = { navigateToAboutApp() },
                )
                childRecordsGraph(
                    navController = navController
                )
                consumerManager(
                    navController = navController,
                    onLogout = { onLogout() },
                    navigationBarActions = navigationBarActions,
                    onAboutAppClick = { navigateToAboutApp() },
                    nfcManager = nfcManager
                )
                productManager(
                    navController = navController,
                    onLogout = { onLogout() },
                    onAboutAppClick = { navigateToAboutApp() },
                    navigationBarActions = navigationBarActions
                )
                cashRegister(
                    navController = navController,
                    onLogout = { onLogout() },
                    onAboutAppClick = { navigateToAboutApp() },
                    navigationBarActions = navigationBarActions
                )
            }
        }
    }
}

class NavigationBarActions(navController: NavHostController) {
    private fun NavHostController.navigateAndClearStack(route: Any) {
        this.navigate(route) {
            popUpTo(0)
            launchSingleTop = true
        }
    }

    val actions: Map<Destination, () -> Unit> = mapOf(
        Destination.HOME to {
            navController.navigate(Graph.Home) {
                popUpTo(0)
                launchSingleTop = true
            }
        },
        Destination.ATTENDEE_MANGER to {
            navController.navigate(Graph.AttendeeManager) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.MORNING_EXERCISE to {
            navController.navigate(Graph.MorningExerciseGraph(null, null)) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.POSITION_DISCIPLINES to {
            navController.navigate(Graph.PositionDisciplinesGraph(null, null)) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.POINTS_MANAGER to {
            navController.navigate(Graph.PointsManagementGraph(null, null)) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.CONSUMER_MANAGER to {
            navController.navigate(Graph.ConsumerManager) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.PRODUCT_MANAGER to {
            navController.navigate(Graph.ProductManager) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
        Destination.CASH_REGISTER to {
            navController.navigate(Graph.CashRegister) {
                navController.navigateAndClearStack(Graph.Home)
            }
        },
    )
}