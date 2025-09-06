package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import cz.bosan.sikula_kmp.core.domain.ContextHolder

@Composable
actual fun ByteArrayImage(
    byteArray: ByteArray,
    modifier: Modifier,
    contentDescription: String?
) {
    val imageRequest = remember(byteArray) {
        ImageRequest.Builder(ContextHolder.applicationContext)
            .data(byteArray)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier
    )
}