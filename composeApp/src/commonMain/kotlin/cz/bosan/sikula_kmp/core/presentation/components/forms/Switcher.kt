package cz.bosan.sikula_kmp.core.presentation.components.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun Switcher(
    selectedButtonIndex: Int?,
    firstLabel: String,
    secondLabel: String,
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    modifier: Modifier = Modifier
) {
    var selectedButton by remember { mutableStateOf(selectedButtonIndex) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelLarge

    val firstWidth = textMeasurer.measure(AnnotatedString(firstLabel), style = textStyle).size.width
    val secondWidth =
        textMeasurer.measure(AnnotatedString(secondLabel), style = textStyle).size.width

    val density = LocalDensity.current
    val maxWidth = with(density) {
        maxOf(firstWidth, secondWidth).toDp()
    } + 26.dp

    LaunchedEffect(selectedButtonIndex) {
        selectedButton = selectedButtonIndex
    }

    Row(
        modifier = modifier.padding(vertical = 5.dp),
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
                SwitcherButton(
                    text = firstLabel,
                    onClick = {
                        selectedButton = 0
                        onFirstClick()
                    },
                    isSelected = selectedButton == 0,
                    width = maxWidth
                )
                SwitcherButton(
                    text = secondLabel,
                    onClick = {
                        selectedButton = 1
                        onSecondClick()
                    },
                    isSelected = selectedButton == 1,
                    width = maxWidth
                )
            }
        }
    }
}

@Composable
fun SwitcherButton(
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    width: Dp,
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .height(37.dp)
            .then(
                if (isSelected) Modifier
                    .zIndex(1f)
                    .shadow(
                        elevation = 9.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                        spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    )
                    .clip(CircleShape)
                else Modifier
            ),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
        shape = RoundedCornerShape(30.dp),
        colors = if (isSelected) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface)
        else ButtonDefaults.buttonColors(Color.Transparent),
        border = if (isSelected) BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        ) else null,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    )
}