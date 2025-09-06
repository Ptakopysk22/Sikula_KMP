package cz.bosan.sikula_kmp.core.domain

expect class NFCManager() {
    fun startReading(onTagRead: (String) -> Unit)
    fun stopReading()
    fun writeTag(data: String, onComplete: (Boolean) -> Unit)
}