package cz.bosan.sikula_kmp.core.presentation.materialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun SetSystemBarsColor(statusBarColor: Color, navigationBarColor: Color, isLight: Boolean)