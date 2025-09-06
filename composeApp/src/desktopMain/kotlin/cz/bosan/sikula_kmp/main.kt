package cz.bosan.sikula_kmp

import android.app.Application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import cz.bosan.sikula_kmp.app.App
import cz.bosan.sikula_kmp.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun main() = application {
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()
        override fun store(key: String, value: String) = storage.set(key, value)
        override fun retrieve(key: String) = storage[key]
        override fun clear(key: String) { storage.remove(key) }
        override fun log(msg: String) = println(msg)
    })
    val options = FirebaseOptions(
        applicationId = "secret\n",
        apiKey = "secret",
        projectId = "id",
    )
    Firebase.initialize(Application(), options)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Sikula_KMP",
    ) {
        initKoin()
        App(
            navController = TODO(),
            viewModel = TODO(),
            nfcManager = TODO()
        )
    }
}