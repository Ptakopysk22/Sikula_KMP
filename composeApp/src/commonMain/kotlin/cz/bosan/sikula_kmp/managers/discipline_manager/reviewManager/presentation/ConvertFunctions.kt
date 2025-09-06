package cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.materialTheme.extended
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import org.jetbrains.compose.resources.painterResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.check
import sikula_kmp.composeapp.generated.resources.checks
import sikula_kmp.composeapp.generated.resources.empty
import sikula_kmp.composeapp.generated.resources.wifi_slash
import sikula_kmp.composeapp.generated.resources.x

@Composable
fun getDayRecordsStateIcon(dayRecordsState: DayRecordsState): Painter {
    return when (dayRecordsState) {
        DayRecordsState.OFFLINE -> Res.drawable.wifi_slash
        DayRecordsState.IN_PROGRESS -> Res.drawable.x
        DayRecordsState.CHECKED_BY_GROUP -> Res.drawable.check
        DayRecordsState.CHECKED_BY_GAME_MASTER -> Res.drawable.checks
        DayRecordsState.NON_CHECKED_BY_GROUP -> Res.drawable.x
        DayRecordsState.NON_SYNCHRONIZE -> Res.drawable.x
        DayRecordsState.WITHOUT_STATE -> Res.drawable.empty
    }.let { painterResource(it) }
}

@Composable
fun getDayRecordsStateColor(dayRecordsState: DayRecordsState): Color {
    return when (dayRecordsState) {
        DayRecordsState.OFFLINE -> Color.Black
        DayRecordsState.IN_PROGRESS -> Color.White
        DayRecordsState.CHECKED_BY_GROUP -> Color.White
        DayRecordsState.CHECKED_BY_GAME_MASTER -> Color.White
        DayRecordsState.NON_CHECKED_BY_GROUP -> Color.White
        DayRecordsState.NON_SYNCHRONIZE -> Color.White
        DayRecordsState.WITHOUT_STATE -> Color.Black
    }
}

@Composable
fun getDayRecordsStateColorBackground(dayRecordsState: DayRecordsState, isGray: Boolean = false): Color {
    return when (dayRecordsState) {
        DayRecordsState.OFFLINE -> Color.Transparent
        DayRecordsState.IN_PROGRESS ->  MaterialTheme.colorScheme.error
        DayRecordsState.CHECKED_BY_GROUP -> if(isGray) Color.Gray else MaterialTheme.colorScheme.extended.success
        DayRecordsState.CHECKED_BY_GAME_MASTER -> MaterialTheme.colorScheme.extended.success
        DayRecordsState.NON_CHECKED_BY_GROUP -> MaterialTheme.colorScheme.error
        DayRecordsState.NON_SYNCHRONIZE -> MaterialTheme.colorScheme.error
        DayRecordsState.WITHOUT_STATE -> Color.Transparent
    }
}

@Composable
fun getDayRecordsStateIconSize(dayRecordsState: DayRecordsState, isSmall: Boolean): Dp {
    return when (dayRecordsState) {
        DayRecordsState.OFFLINE -> 26.dp
        DayRecordsState.IN_PROGRESS -> if(isSmall) 15.dp else 20.dp
        DayRecordsState.CHECKED_BY_GROUP -> if(isSmall) 15.dp else 20.dp
        DayRecordsState.CHECKED_BY_GAME_MASTER -> 20.dp
        DayRecordsState.NON_CHECKED_BY_GROUP -> if(isSmall) 15.dp else 20.dp
        DayRecordsState.NON_SYNCHRONIZE -> if(isSmall) 15.dp else 20.dp
        DayRecordsState.WITHOUT_STATE -> 26.dp
    }
}