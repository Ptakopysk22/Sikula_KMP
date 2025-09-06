package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.UiText

@Composable
fun WrapBox(
    isLoading: Boolean,
    errorMessage: UiText?,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            PulseAnimation(modifier = Modifier.size(150.dp))
        }
    } else {
        when {
            errorMessage != null -> {
                Message(
                    text = errorMessage.asString(),
                    messageTyp = MessageTyp.ERROR,
                )
            }

            else -> content()
        }
    }
}