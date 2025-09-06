package cz.bosan.sikula_kmp.core.presentation.materialTheme
import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
actual fun SetSystemBarsColor(statusBarColor: Color, navigationBarColor: Color, isLight: Boolean) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return

    SideEffect {
        @Suppress("DEPRECATION")
        window.statusBarColor = statusBarColor.toArgb()
        @Suppress("DEPRECATION")
        window.navigationBarColor = navigationBarColor.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = true //isLight
        controller.isAppearanceLightNavigationBars = isLight
    }
}