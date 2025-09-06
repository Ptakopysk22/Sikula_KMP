package cz.bosan.sikula_kmp.core.domain

import android.content.Context

object ContextHolder {
    lateinit var applicationContext: Context
        private set

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
}