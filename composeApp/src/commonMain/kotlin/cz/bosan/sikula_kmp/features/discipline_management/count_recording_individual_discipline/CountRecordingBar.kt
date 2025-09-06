package cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.reset

@Composable
fun CountRecordingBar(
    items: List<String?>,
    clickedItemName: String?,
    onItemClick: (String?) -> Unit,
    showCrossIcon: Boolean,
    modifier: Modifier = Modifier
) {
    val filteredItems = items.filterNotNull()
    var selectedItem by remember { mutableStateOf(clickedItemName) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(clickedItemName) {
        selectedItem = clickedItemName?.replace(".", ",")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
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
                filteredItems.forEach { item ->
                    val isSelected = selectedItem == item

                    Button(
                        onClick = {
                            selectedItem = item
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
                        colors = if (isSelected)
                            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface)
                        else
                            ButtonDefaults.buttonColors(Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        border = if (isSelected) BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        ) else null
                    ) {
                        Text(
                            item,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
        if (showCrossIcon) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(Res.string.reset),
                modifier = Modifier.size(30.dp).clickable {
                    selectedItem = null
                    onItemClick(null)
                }
            )
        }
    }
}