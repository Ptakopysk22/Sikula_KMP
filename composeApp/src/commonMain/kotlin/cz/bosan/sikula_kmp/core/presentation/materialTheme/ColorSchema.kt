package cz.bosan.sikula_kmp.core.presentation.materialTheme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val lightColorSchema: ColorScheme = lightColorScheme(
    primary = Color(0xFF2A3478),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFE28F26),
    onSecondary = Color(0xFF000000),
    background = Color(0xFFDFE3FF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFEDEFFF),
    onSurface = Color(0xFF000000),
    primaryContainer = Color(0xFFB4BDFA),
    onPrimaryContainer = Color(0xFF000000),
    error = Color(0xFF8E1227),
    onError = Color(0xFFFFFFFF)
)

@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        success = Color(0xFF1D7A13),
        onSuccess = Color.White,
        warning = Color(0xFF99490C),
        onWarning = Color.Black,
        info = Color(0xFF1C4DAF),
        onInfo = Color.White
    )
}

val localExtendedColors =  ExtendedColors(
    success = Color(0xFF4CAF50),
    onSuccess = Color.White,
    warning = Color(0xFFFFC107),
    onWarning = Color.Black,
    info = Color(0xFF1C4DAF),
    onInfo = Color.White
)

val ColorScheme.extended: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current