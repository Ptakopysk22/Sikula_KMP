package cz.bosan.sikula_kmp.features.attendee_management.attendee_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.children
import sikula_kmp.composeapp.generated.resources.leaders

@Composable
fun PageChanger(
    selectedTabIndex: Int,
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedButton by remember { mutableStateOf(selectedTabIndex) }
    val borderColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(selectedTabIndex) {
        selectedButton = selectedTabIndex
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(45.dp)
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
                }
        ) {
            Row {
                PageChangerButton(
                    text = stringResource(Res.string.children),
                    onClick = {
                        selectedButton = 0
                        onFirstClick()
                    },
                    isSelected = selectedButton == 0
                )
                PageChangerButton(
                    text = stringResource(Res.string.leaders),
                    onClick = {
                        selectedButton = 1
                        onSecondClick()
                    },
                    isSelected = selectedButton == 1
                )
            }
        }
    }
}

@Composable
fun PageChangerButton(
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(140.dp)
            .height(45.dp)
            .shadow(
                elevation = if (isSelected) 20.dp else 0.dp,
                shape = RoundedCornerShape(40.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            ),
        shape = RoundedCornerShape(40.dp),
        colors = if (isSelected) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        else ButtonDefaults.buttonColors(Color.Transparent),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    )
}