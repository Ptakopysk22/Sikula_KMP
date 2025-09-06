package cz.bosan.sikula_kmp.core.domain

import android.os.Build

actual object Platform {
    actual val isIos: Boolean = false
    actual val isAndroid: Boolean = true
    actual val name: String = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
}