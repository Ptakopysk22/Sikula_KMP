package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import cz.bosan.sikula_kmp.core.data.imageFromBytes

@Composable
actual fun ByteArrayImage(
    byteArray: ByteArray,
    modifier: Modifier,
    contentDescription: String?
) {
    val imageBitmap = remember(byteArray) { imageFromBytes(byteArray) }

    imageBitmap?.let {
        Image(
            painter = BitmapPainter(it),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}