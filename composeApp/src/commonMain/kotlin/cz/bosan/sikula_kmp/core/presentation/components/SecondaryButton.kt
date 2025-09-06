package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryButton(
    content: @Composable () -> Unit,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (enabled) 1f else 0.5f

    Surface(
        modifier = modifier.graphicsLayer(alpha = alpha).padding(horizontal = 6.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        ElevatedButton(
            onClick = if (enabled) onClick else { {} },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = Modifier.defaultMinSize(minHeight = 37.dp)
        ) {
            content()
        }
    }
}