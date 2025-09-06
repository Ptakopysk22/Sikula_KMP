package cz.bosan.sikula_kmp.core.domain

expect object Platform {
    val isIos: Boolean
    val isAndroid: Boolean
    val name: String
}