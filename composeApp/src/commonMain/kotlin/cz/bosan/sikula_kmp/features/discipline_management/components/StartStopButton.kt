package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.materialTheme.extended
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.continue_timer
import sikula_kmp.composeapp.generated.resources.start_timer
import sikula_kmp.composeapp.generated.resources.stop_timer

@Composable
fun StartStopButton(
    state: StartStopButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.padding(2.dp).defaultMinSize(minWidth = 55.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor =
            when (state) {
                StartStopButtonState.START -> MaterialTheme.colorScheme.extended.success
                StartStopButtonState.STOP -> MaterialTheme.colorScheme.error
                StartStopButtonState.CONTINUE -> MaterialTheme.colorScheme.extended.warning
            }
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = when (state) {
                StartStopButtonState.START -> stringResource(Res.string.start_timer)
                StartStopButtonState.STOP -> stringResource(Res.string.stop_timer)
                StartStopButtonState.CONTINUE -> stringResource(Res.string.continue_timer)
            },
            style = MaterialTheme.typography.titleSmall
        )
    }
}

enum class StartStopButtonState {
    START,
    STOP,
    CONTINUE
}