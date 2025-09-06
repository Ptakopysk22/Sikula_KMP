package cz.bosan.sikula_kmp.core.domain

actual class NFCManager actual constructor() {
    actual fun startReading(onTagRead: (String) -> Unit) {
    }

    actual fun stopReading() {
    }

    actual fun writeTag(data: String, onComplete: (Boolean) -> Unit) {
    }
}