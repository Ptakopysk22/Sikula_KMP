package cz.bosan.sikula_kmp.features.limo_counter.supply_section.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.ByteArrayImage

@Composable
fun QrCodeImage(
    byteArray: ByteArray,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
    ) {
        val size = maxWidth

        Surface(
            modifier = Modifier
                .size(size)
                .clip(MaterialTheme.shapes.medium),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                ByteArrayImage(
                    byteArray = byteArray,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = contentDescription
                )
            }
        }
    }
}