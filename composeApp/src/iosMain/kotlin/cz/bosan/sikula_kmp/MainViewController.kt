package cz.bosan.sikula_kmp

import androidx.compose.ui.window.ComposeUIViewController
import cz.bosan.sikula_kmp.app.App
import cz.bosan.sikula_kmp.core.domain.NFCManager
import cz.bosan.sikula_kmp.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App(nfcManager = NFCManager())
}