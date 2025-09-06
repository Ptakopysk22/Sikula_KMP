package cz.bosan.sikula_kmp.core.domain

import platform.UIKit.UIDevice

actual object Platform {
    actual val isIos: Boolean = true
    actual val isAndroid: Boolean = false
    actual val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}