package cz.bosan.sikula_kmp.core.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import platform.UIKit.*

@Composable
actual fun PreventBackNavigation(shouldPrevent: Boolean) {
    LaunchedEffect(shouldPrevent) {
        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        val navController = findNavigationController(viewController)
        navController?.interactivePopGestureRecognizer?.enabled = !shouldPrevent
    }
}

private fun findNavigationController(controller: UIViewController?): UINavigationController? {
    return when (controller) {
        is UINavigationController -> controller
        is UITabBarController -> findNavigationController(controller.selectedViewController)
        is UISplitViewController -> findNavigationController(controller.viewControllers.firstOrNull() as? UIViewController)
        else -> controller?.presentedViewController?.let { findNavigationController(it) }
    }
}