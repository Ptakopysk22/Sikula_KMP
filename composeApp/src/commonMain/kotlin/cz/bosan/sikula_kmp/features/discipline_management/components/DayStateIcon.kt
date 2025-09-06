package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.presentation.getDayRecordsStateColor
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.presentation.getDayRecordsStateColorBackground
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.presentation.getDayRecordsStateIcon
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.presentation.getDayRecordsStateIconSize
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.day_state

@Composable
fun DayStateCircleIcon(
    dayRecordsState: DayRecordsState,
    isGray: Boolean = false,
    isSmall: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            color = getDayRecordsStateColorBackground(
                dayRecordsState = dayRecordsState,
                isGray = isGray
            ),
            shape = CircleShape,
        ).size(26.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = getDayRecordsStateIcon(dayRecordsState),
            tint = getDayRecordsStateColor(dayRecordsState),
            contentDescription = stringResource(Res.string.day_state),
            modifier = Modifier.size(
                getDayRecordsStateIconSize(
                    dayRecordsState = dayRecordsState,
                    isSmall = isSmall
                )
            )
        )
    }
}