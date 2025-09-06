package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineIcon
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName

@Composable
fun DisciplineButton(
    discipline: Discipline,
    onClick: (Discipline) -> Unit,
    disciplineState: DayRecordsState?,
    isIconGray: Boolean,
    modifier: Modifier = Modifier,
) {
    val sideSize: Dp = 190.dp

    Surface(
        modifier = modifier
            .height(sideSize)
            .width(sideSize)
            .padding(5.dp)
            .clickable { onClick(discipline) },
        shadowElevation = 5.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(0.7f)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(26.dp).zIndex(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    disciplineState?.let {
                        DayStateCircleIcon(
                            dayRecordsState = it,
                            isGray = isIconGray
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = getDisciplineIcon(discipline),
                        contentDescription = getDisciplineName(discipline),
                        modifier = Modifier.size(75.dp),
                        tint = Color.Unspecified
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp).border(
                        width = 2.dp,
                        color = discipline.getColor(),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        )
                    )
                    .background(
                        color = discipline.getColor().copy(alpha = 0.1f),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        )
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getDisciplineName(discipline),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

        }
    }
}