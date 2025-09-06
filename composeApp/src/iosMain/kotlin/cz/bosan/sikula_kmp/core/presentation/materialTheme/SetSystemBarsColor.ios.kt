package cz.bosan.sikula_kmp.core.presentation.materialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationBar
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UITabBar
import platform.UIKit.setStatusBarStyle
import platform.UIKit.*

@Composable
actual fun SetSystemBarsColor(
    statusBarColor: Color,
    navigationBarColor: Color,
    isLight: Boolean
) {
    val statusColor = statusBarColor.toArgb().toUIColor()
    val navColor = navigationBarColor.toArgb().toUIColor()

    val window = UIApplication.sharedApplication.keyWindow
    window?.backgroundColor = statusColor

    val navBar = UINavigationBar.appearance()
    navBar.barTintColor = statusColor
    navBar.backgroundColor = statusColor
    navBar.setTranslucent(false)

    val tabBar = UITabBar.appearance()
    tabBar.barTintColor = navColor
    tabBar.backgroundColor = navColor
    tabBar.setTranslucent(false)

    UIApplication.sharedApplication.setStatusBarStyle(1)
}

fun Int.toUIColor(): UIColor {
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    val alpha = ((this shr 24) and 0xFF) / 255.0
    return UIColor(red = red, green = green, blue = blue, alpha = alpha)
}