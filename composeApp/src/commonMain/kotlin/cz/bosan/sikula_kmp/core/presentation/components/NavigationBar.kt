package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.domain.getDestinationIcon
import cz.bosan.sikula_kmp.core.domain.getDestinationName
import cz.bosan.sikula_kmp.core.domain.onDestinationSelected

@Composable
fun NavigationBar(
    role: Role,
    currentDestination: Destination,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(74.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val destinations = when (role) {
                Role.HEAD_GROUP_LEADER -> listOf(
                    Destination.HOME,
                    Destination.ATTENDEE_MANGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.MORNING_EXERCISE,
                    /*Destination.ATTENDEE_MANGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.HOME,
                    Destination.MORNING_EXERCISE,
                    Destination.LIMO_COUNTER*/
                )
                Role.CHILD_LEADER -> listOf(
                    Destination.HOME,
                    Destination.ATTENDEE_MANGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.MORNING_EXERCISE,
                    /*Destination.ATTENDEE_MANGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.HOME,
                    Destination.MORNING_EXERCISE,
                    Destination.LIMO_COUNTER*/
                )
                Role.DIRECTOR -> listOf(
                    Destination.ATTENDEE_MANGER,
                    Destination.POINTS_MANAGER,
                    Destination.HOME,
                    Destination.POSITION_DISCIPLINES,
                    Destination.MORNING_EXERCISE,
                    /*Destination.HOME,
                    Destination.ATTENDEE_MANGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.POINTS_MANAGER,
                    Destination.MORNING_EXERCISE,
                    Destination.LIMO_COUNTER*/
                )
                Role.SUPPLY -> listOf(
                    Destination.HOME,
                    Destination.CONSUMER_MANAGER,
                    Destination.CASH_REGISTER,
                    Destination.PRODUCT_MANAGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.LIMO_COUNTER
                )
                Role.GAME_MASTER -> listOf(
                    Destination.HOME,
                    Destination.POINTS_MANAGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.MORNING_EXERCISE,
                    /*Destination.POINTS_MANAGER,
                    Destination.POSITION_DISCIPLINES,
                    Destination.HOME,
                    Destination.MORNING_EXERCISE,
                    Destination.LIMO_COUNTER*/
                )
                Role.GUEST -> listOf(
                    Destination.HOME,
                    Destination.POSITION_DISCIPLINES,
                    /*Destination.POSITION_DISCIPLINES,
                    Destination.HOME,
                    Destination.LIMO_COUNTER*/
                )
                Role.NON_CHILD_LEADER -> listOf(
                    Destination.HOME,
                    Destination.POSITION_DISCIPLINES,
                    /*Destination.POSITION_DISCIPLINES,
                    Destination.HOME,
                    Destination.LIMO_COUNTER*/
                )
                Role.NO_ROLE -> emptyList()
            }
            destinations.forEach {
                NavigationBarButton(
                    onClick = onDestinationSelected(it, navigationBarActions),
                    icon = getDestinationIcon(it, role),
                    description = getDestinationName(it, role),
                    isSelected = (currentDestination == it)
                )
            }
        }
    }
}

@Composable
fun NavigationBarButton(
    onClick: () -> Unit,
    icon: Painter,
    description: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(2.dp).height(74.dp).width(60.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
            )
            HorizontalDivider(
                thickness = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}