package cz.bosan.sikula_kmp.core.data

import android.content.Intent
import android.net.Uri
import cz.bosan.sikula_kmp.core.domain.ContextHolder

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ContextHolder.applicationContext.startActivity(intent)
}