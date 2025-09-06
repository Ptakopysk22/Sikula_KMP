package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ByteArrayImage(
    byteArray: ByteArray,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)