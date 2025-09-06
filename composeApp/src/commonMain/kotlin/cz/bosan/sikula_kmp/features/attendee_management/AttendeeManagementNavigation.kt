package cz.bosan.sikula_kmp.features.attendee_management

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import cz.bosan.sikula_kmp.app.Graph
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.features.attendee_management.attendee_list.AttendeeListRoute
import cz.bosan.sikula_kmp.features.attendee_management.check_user.CheckUserAction
import cz.bosan.sikula_kmp.features.attendee_management.check_user.CheckUserRoute
import cz.bosan.sikula_kmp.features.attendee_management.check_user.CheckUserViewModel
import cz.bosan.sikula_kmp.features.attendee_management.child_detail.ChildDetailAction
import cz.bosan.sikula_kmp.features.attendee_management.child_detail.ChildDetailRoute
import cz.bosan.sikula_kmp.features.attendee_management.child_detail.ChildDetailViewModel
import cz.bosan.sikula_kmp.features.attendee_management.children_list.ChildrenListRoute
import cz.bosan.sikula_kmp.features.attendee_management.leader_detail.LeaderDetailAction
import cz.bosan.sikula_kmp.features.attendee_management.leader_detail.LeaderDetailRoute
import cz.bosan.sikula_kmp.features.attendee_management.leader_detail.LeaderDetailViewModel
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.serialization.Serializable
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private interface AttendeeManager {
    @Serializable
    data class AttendeeList(val selectedTabIndex: Int) : AttendeeManager

    @Serializable
    data object ChildrenList : AttendeeManager

    @Serializable
    data class CheckUser(val isLeaderChecking: Boolean) : AttendeeManager

    @Serializable
    data class ChildDetail(val id: String) : AttendeeManager

    @Serializable
    data class LeaderDetail(val id: String) : AttendeeManager
}

fun NavGraphBuilder.attendeeManager(
    navController: NavController,
    onLogout: () -> Unit,
    navigationBarActions: NavigationBarActions,
    onAboutAppClick: () -> Unit,
    userRole: Role,
) {
    val startDestination = when (userRole) {
        Role.DIRECTOR -> AttendeeManager.AttendeeList(selectedTabIndex = 1)
        else -> AttendeeManager.ChildrenList
    }
    navigation<Graph.AttendeeManager>(
        startDestination = startDestination,
    ) {

        composable<AttendeeManager.AttendeeList> { backStackEntry ->
            val selectedTabIndex = backStackEntry.arguments?.getInt("selectedTabIndex")

            AttendeeListRoute(
                navigationBarActions = navigationBarActions,
                viewModel = koinViewModel(parameters = { parametersOf(selectedTabIndex) }),
                onLogout = onLogout,
                onAddClick = { isLeader ->
                    navController.navigate(AttendeeManager.CheckUser(isLeader))
                },
                onLeaderClick = { leader ->
                    navController.navigate(
                        AttendeeManager.LeaderDetail(id = leader.id.toString())
                    )
                },
                onChildClick = { child ->
                    navController.navigate(AttendeeManager.ChildDetail(id = child.id.toString()))
                },
                onAboutAppClick = onAboutAppClick
            )
        }
        composable<AttendeeManager.ChildrenList> {
            ChildrenListRoute(
                navigationBarActions = navigationBarActions,
                onLogout = onLogout,
                onAddClick = { navController.navigate(AttendeeManager.CheckUser(false)) },
                onChildClick = { child ->
                    navController.navigate(AttendeeManager.ChildDetail(id = child.id.toString()))
                },
                onAboutAppClick = onAboutAppClick
            )
        }
        composable<AttendeeManager.CheckUser> { backStackEntry ->
            val isLeader = backStackEntry.arguments?.getBoolean("isLeaderChecking") ?: true
            val viewModel = koinViewModel<CheckUserViewModel>()

            LaunchedEffect(isLeader) {
                viewModel.onAction(CheckUserAction.OnDecideCheckingUserStatus(isLeader))
            }

            val attendeeManagerViewModel: AttendeeManagerViewModel = getKoin().get()

            CheckUserRoute(
                onAssignAttendee = { user ->
                    attendeeManagerViewModel.setSelectedUser(user, isLeader)
                    if (isLeader) {
                        navController.navigate(AttendeeManager.LeaderDetail(user.id.toString()))
                    } else {
                        navController.navigate(AttendeeManager.ChildDetail(user.id.toString()))
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<AttendeeManager.LeaderDetail> { backStackEntry ->
            val leaderDetail: AttendeeManager.LeaderDetail = backStackEntry.toRoute()

            val viewModel = koinViewModel<LeaderDetailViewModel>()
            val attendeeManagerViewModel: AttendeeManagerViewModel = getKoin().get()

            LaunchedEffect(leaderDetail.id) {
                val newLeader = attendeeManagerViewModel.getSelectedLeader()
                if (newLeader == null) {
                    viewModel.onAction(LeaderDetailAction.OnLoadLeader(id = leaderDetail.id.toInt()))
                } else {
                    viewModel.onAction(LeaderDetailAction.OnLoadNewLeader(leader = newLeader))
                }
            }

            LeaderDetailRoute(
                viewModel = viewModel,
                onBackClick = {
                    navController.navigate(AttendeeManager.AttendeeList(selectedTabIndex = 1)) {
                        popUpTo<AttendeeManager.AttendeeList> { inclusive = true }
                    }
                }
            )
        }
        composable<AttendeeManager.ChildDetail> { backStackEntry ->
            val childDetail: AttendeeManager.ChildDetail = backStackEntry.toRoute()

            val viewModel = koinViewModel<ChildDetailViewModel>()
            val attendeeManagerViewModel: AttendeeManagerViewModel = getKoin().get()

            LaunchedEffect(childDetail.id) {
                val newChild = attendeeManagerViewModel.getSelectedChild()
                if (newChild == null) {
                    viewModel.onAction(ChildDetailAction.OnLoadChild(id = childDetail.id.toInt()))
                } else {
                    viewModel.onAction(ChildDetailAction.OnLoadNewChild(child = newChild))
                }

            }
            ChildDetailRoute(
                viewModel = viewModel,
                onBackClick = {
                    if (userRole == Role.DIRECTOR) {
                        navController.navigate(AttendeeManager.AttendeeList(selectedTabIndex = 0)) {
                            popUpTo<AttendeeManager.AttendeeList> { inclusive = true }
                        }
                    } else {
                        navController.navigate(AttendeeManager.ChildrenList) {
                            popUpTo<AttendeeManager.ChildrenList> { inclusive = true }
                        }
                    }
                },
            )
        }
    }
}