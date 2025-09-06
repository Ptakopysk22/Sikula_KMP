package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import cz.bosan.sikula_kmp.features.discipline_management.components.DayStateCircleIcon
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_all_groups
import sikula_kmp.composeapp.generated.resources.users_three

@Composable
fun <T : SelectableItem> FilterBar(
    items: List<T>,
    clickedItemName: String,
    onItemClick: (T?) -> Unit,
    dayRecordStates: List<Pair<Group, DayRecordsState>>,
    showDayStateRecords: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(clickedItemName) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(clickedItemName) {
        selectedItem = clickedItemName
    }

    Box(modifier = modifier.padding(bottom = if (showDayStateRecords) 6.dp else 2.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
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
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isAllSelected = selectedItem == "all"
                Button(
                    onClick = {
                        selectedItem = "all"
                        onItemClick(null)
                    },
                    modifier = if (isAllSelected) Modifier
                        .size(37.dp)
                        .zIndex(1f)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = Color.Black
                        )
                        .clip(CircleShape)
                    else
                        Modifier.size(37.dp),
                    shape = CircleShape,
                    colors = if (isAllSelected)
                        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface)
                    else
                        ButtonDefaults.buttonColors(Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    border = if (isAllSelected) BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.users_three),
                        modifier = Modifier.size(25.dp),
                        contentDescription = stringResource(Res.string.description_all_groups),
                        tint = if (isAllSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                items.forEach { item ->
                    val isSelected = selectedItem == item.name
                    Button(
                        onClick = {
                            selectedItem = item.name
                            onItemClick(item)
                        },
                        modifier = if (isSelected) Modifier
                            .size(37.dp)
                            .zIndex(1f)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                ambientColor = MaterialTheme.colorScheme.primary,
                                spotColor = Color.Black
                            )
                            .clip(CircleShape)
                        else
                            Modifier.size(37.dp),
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
                        Text(
                            item.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
        if (showDayStateRecords) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.9.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(y = (26).dp)
            ) {
                Spacer(Modifier.width(29.3.dp))
                items.forEach { item ->
                    if (item is Group) {
                        val state = dayRecordStates.find { it.first.id == item.id }?.second
                        DayStateCircleIcon(
                            dayRecordsState = state ?: DayRecordsState.WITHOUT_STATE,
                            isSmall = true,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


