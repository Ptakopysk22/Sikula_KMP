package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation
import sikula_kmp.composeapp.generated.resources.description_next_day
import sikula_kmp.composeapp.generated.resources.description_previous_day

@Composable
fun CampDayCarousel(
    campDay: Int,
    campDuration: Int,
    changeWithoutAnimation: () -> Unit,
    onDayChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(45.dp).fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                if (campDay > 1) {
                    onDayChanged(campDay - 1)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(Res.string.description_previous_day),
                    modifier = Modifier.size(35.dp),
                    tint = if (campDay == 1) Color.Black.copy(alpha = 0.2f) else Color.Black
                )
            }
            dayButton(
                day = campDay - 2,
                campDuration = campDuration,
                onClick = { campDay ->
                    changeWithoutAnimation()
                    onDayChanged(campDay)
                }
            )
            dayButton(
                day = campDay - 1,
                campDuration = campDuration,
                onClick = { campDay -> onDayChanged(campDay) }
            )
            dayBox(day = campDay)
            dayButton(
                day = campDay + 1,
                campDuration = campDuration,
                onClick = { campDay -> onDayChanged(campDay) }
            )
            dayButton(
                day = campDay + 2,
                campDuration = campDuration,
                onClick = { campDay ->
                    changeWithoutAnimation()
                    onDayChanged(campDay)
                }
            )
            IconButton(onClick = {
                if (campDay < campDuration) {
                    onDayChanged(campDay + 1)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.description_next_day),
                    modifier = Modifier.size(35.dp),
                    tint = if (campDay == campDuration) Color.Black.copy(alpha = 0.2f) else Color.Black
                )
            }
        }
    }
}

@Composable
fun dayBox(
    day: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .heightIn()
            .width(75.dp)
            .shadow(
                elevation = 10.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(Res.string.camp_day_format_abbreviation, day),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun dayButton(
    day: Int,
    campDuration: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .heightIn()
            .width(35.dp)
            .clickable(onClick = {
                if (day in 1..campDuration) {
                    onClick(day)
                }
            })
    ) {
        if (day in 1..campDuration) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}