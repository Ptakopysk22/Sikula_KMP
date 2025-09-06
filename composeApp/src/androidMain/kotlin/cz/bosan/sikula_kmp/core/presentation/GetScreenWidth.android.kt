package cz.bosan.sikula_kmp.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenWidth(): Dp {
    val density = LocalDensity.current.density
    val displayMetrics = LocalContext.current.resources.displayMetrics
    return displayMetrics.widthPixels.dp/density
}