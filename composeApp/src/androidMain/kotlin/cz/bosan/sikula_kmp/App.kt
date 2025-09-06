package cz.bosan.sikula_kmp

import android.app.Application
import cz.bosan.sikula_kmp.core.domain.ContextHolder
import cz.bosan.sikula_kmp.di.initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(this)
        initKoin {
            androidContext(this@App)
        }
    }
}