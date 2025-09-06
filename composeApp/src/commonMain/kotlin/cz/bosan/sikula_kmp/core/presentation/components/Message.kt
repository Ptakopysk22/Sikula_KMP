package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.materialTheme.extended

@Composable
fun Message(
    text: String,
    messageTyp: MessageTyp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = if (messageTyp == MessageTyp.INFO) {
            modifier.padding(horizontal = 20.dp, vertical = 5.dp).fillMaxWidth().background(
                color = MaterialTheme.colorScheme.extended.info,
                shape = MaterialTheme.shapes.small
            )
        } else {
            modifier.fillMaxSize()
        },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = if (messageTyp == MessageTyp.INFO) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.headlineSmall
            },
            color = when (messageTyp) {
                MessageTyp.ERROR -> MaterialTheme.colorScheme.error
                MessageTyp.WARNING -> Color.Black //MaterialTheme.colorScheme.extended.warning
                MessageTyp.INFO -> MaterialTheme.colorScheme.extended.onInfo
            },
            modifier = Modifier.padding(8.dp)
        )
    }
}

enum class MessageTyp {
    ERROR,
    WARNING,
    INFO
}