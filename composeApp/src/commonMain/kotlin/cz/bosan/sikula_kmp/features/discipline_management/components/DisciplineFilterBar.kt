package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineIcon
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName

@Composable
fun <T : SelectableItem> DisciplineFilterBar(
    items: List<T>,
    clickedDiscipline: Discipline,
    onItemClick: (T) -> Unit,
    disciplineRowState: LazyListState,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(clickedDiscipline.name) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(clickedDiscipline.name) {
        selectedItem = clickedDiscipline.name
    }
    Column {
        Row(
            modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getDisciplineName(clickedDiscipline),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = modifier
                    .padding(horizontal = 15.dp)
                    .height(37.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .drawBehind {
                        drawRoundRect(
                            color = borderColor,
                            size = size,
                            cornerRadius = CornerRadius(30.dp.toPx()),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    state = disciplineRowState,
                ) {
                    items(items) { item ->
                        val isSelected = selectedItem == item.name

                        Button(
                            onClick = {
                                selectedItem = item.name
                                onItemClick(item)
                            },
                            modifier = Modifier
                                .size(37.dp)
                                .then(
                                    if (isSelected) Modifier
                                        .zIndex(1f)
                                        .shadow(
                                            elevation = 10.dp,
                                            shape = CircleShape,
                                            ambientColor = MaterialTheme.colorScheme.primary,
                                            spotColor = Color.Black
                                        )
                                        .clip(CircleShape)
                                    else Modifier
                                ),
                            shape = CircleShape,
                            colors = if (isSelected) {
                                ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface)
                            } else {
                                ButtonDefaults.buttonColors(Color.Transparent)
                            },
                            contentPadding = PaddingValues(0.dp),
                            border = if (isSelected) BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ) else null
                        ) {
                            Icon(
                                painter = getDisciplineIcon(item as Discipline),
                                tint = Color.Gray,
                                contentDescription = getDisciplineName(item as Discipline)
                            )
                        }
                    }
                }
            }
        }
    }
}