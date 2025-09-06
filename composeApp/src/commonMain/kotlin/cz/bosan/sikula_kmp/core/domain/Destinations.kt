package cz.bosan.sikula_kmp.core.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.beer_bottle
import sikula_kmp.composeapp.generated.resources.chart_line_up
import sikula_kmp.composeapp.generated.resources.destination_attendee_manager
import sikula_kmp.composeapp.generated.resources.destination_cash_register
import sikula_kmp.composeapp.generated.resources.destination_children_manager
import sikula_kmp.composeapp.generated.resources.destination_consumer_manager
import sikula_kmp.composeapp.generated.resources.destination_home
import sikula_kmp.composeapp.generated.resources.destination_limo_counter
import sikula_kmp.composeapp.generated.resources.destination_morning_exercise
import sikula_kmp.composeapp.generated.resources.destination_points_manager
import sikula_kmp.composeapp.generated.resources.destination_positions_disciplines
import sikula_kmp.composeapp.generated.resources.destination_product_manager
import sikula_kmp.composeapp.generated.resources.function
import sikula_kmp.composeapp.generated.resources.home
import sikula_kmp.composeapp.generated.resources.limo_counter_icon
import sikula_kmp.composeapp.generated.resources.money_wavy
import sikula_kmp.composeapp.generated.resources.sun_horizon
import sikula_kmp.composeapp.generated.resources.users_three

enum class Destination {
    HOME,
    ATTENDEE_MANGER,
    LIMO_COUNTER,
    POINTS_MANAGER,
    MORNING_EXERCISE,
    POSITION_DISCIPLINES,
    CONSUMER_MANAGER,
    PRODUCT_MANAGER,
    CASH_REGISTER
}

fun onDestinationSelected(destination: Destination, navigationBarActions: NavigationBarActions): () -> Unit {
    return when(destination) {
        Destination.HOME -> navigationBarActions.actions[Destination.HOME] ?: {}
        Destination.ATTENDEE_MANGER -> navigationBarActions.actions[Destination.ATTENDEE_MANGER] ?: {}
        Destination.LIMO_COUNTER -> navigationBarActions.actions[Destination.LIMO_COUNTER] ?: {}
        Destination.POINTS_MANAGER -> navigationBarActions.actions[Destination.POINTS_MANAGER] ?: {}
        Destination.MORNING_EXERCISE -> navigationBarActions.actions[Destination.MORNING_EXERCISE] ?: {}
        Destination.POSITION_DISCIPLINES -> navigationBarActions.actions[Destination.POSITION_DISCIPLINES] ?: {}
        Destination.CONSUMER_MANAGER -> navigationBarActions.actions[Destination.CONSUMER_MANAGER] ?: {}
        Destination.PRODUCT_MANAGER -> navigationBarActions.actions[Destination.PRODUCT_MANAGER]?: {}
        Destination.CASH_REGISTER ->  navigationBarActions.actions[Destination.CASH_REGISTER]?: {}
    }
}

@Composable
fun getDestinationName(destination: Destination, role: Role): String {
    return when (destination) {
        Destination.HOME -> Res.string.destination_home
        Destination.ATTENDEE_MANGER -> if (role == Role.DIRECTOR) Res.string.destination_attendee_manager else Res.string.destination_children_manager
        Destination.LIMO_COUNTER -> Res.string.destination_limo_counter
        Destination.POINTS_MANAGER -> Res.string.destination_points_manager
        Destination.MORNING_EXERCISE -> Res.string.destination_morning_exercise
        Destination.POSITION_DISCIPLINES -> Res.string.destination_positions_disciplines
        Destination.CONSUMER_MANAGER -> Res.string.destination_consumer_manager
        Destination.PRODUCT_MANAGER -> Res.string.destination_product_manager
        Destination.CASH_REGISTER -> Res.string.destination_cash_register
    }.let { stringResource(it) }
}

@Composable
fun getDestinationIcon(destination: Destination, role: Role): Painter {
    return when (destination) {
        Destination.HOME -> Res.drawable.home
        Destination.ATTENDEE_MANGER -> Res.drawable.users_three
        Destination.LIMO_COUNTER -> Res.drawable.limo_counter_icon
        Destination.POINTS_MANAGER -> Res.drawable.chart_line_up
        Destination.MORNING_EXERCISE -> Res.drawable.sun_horizon
        Destination.POSITION_DISCIPLINES -> Res.drawable.function
        Destination.CONSUMER_MANAGER -> Res.drawable.users_three
        Destination.PRODUCT_MANAGER -> Res.drawable.beer_bottle
        Destination.CASH_REGISTER -> Res.drawable.money_wavy
    }.let { painterResource(it) }
}