package cz.bosan.sikula_kmp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cz.bosan.sikula_kmp.app.App
import cz.bosan.sikula_kmp.core.domain.NFCManager

class MainActivity : ComponentActivity() {

    private lateinit var nfcManager: NFCManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        nfcManager = NFCManager()
        nfcManager.init(this)

        setContent {
            App(nfcManager = nfcManager)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcManager.handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcManager.startReading{}
    }

    override fun onPause() {
        super.onPause()
        nfcManager.stopReading()
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}