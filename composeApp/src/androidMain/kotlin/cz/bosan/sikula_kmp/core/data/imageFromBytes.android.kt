package cz.bosan.sikula_kmp.core.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun imageFromBytes(byteArray: ByteArray): ImageBitmap? {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        ?.asImageBitmap()
}