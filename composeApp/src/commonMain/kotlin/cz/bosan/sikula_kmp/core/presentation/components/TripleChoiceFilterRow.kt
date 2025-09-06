package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TripleChoiceIconFilterRow(
    selectedButtonIndex: Int?,
    firstIcon: @Composable () -> Unit,
    secondIcon: @Composable () -> Unit,
    thirdIcon: @Composable () -> Unit,
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    onThirdClick: () -> Unit,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    modifier: Modifier = Modifier
) {
    var selectedButton by remember { mutableStateOf(selectedButtonIndex) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(selectedButtonIndex) {
        selectedButton = selectedButtonIndex
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(37.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(30.dp)
                ).drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        size = size,
                        cornerRadius = CornerRadius(30.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                },
        ) {
            Row {
                TripleChoiceIconButton(
                    isAllSelected = (selectedButtonIndex == 0),
                    onClick = onFirstClick,
                    icon = firstIcon
                )
                TripleChoiceIconButton(
                    isAllSelected = (selectedButtonIndex == 1),
                    onClick = onSecondClick,
                    icon = secondIcon
                )
                TripleChoiceIconButton(
                    isAllSelected = (selectedButtonIndex == 2),
                    onClick = onThirdClick,
                    icon = thirdIcon
                )
            }
        }
    }


}

@Composable
fun TripleChoiceIconButton(
    isAllSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
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
        icon()
    }
}