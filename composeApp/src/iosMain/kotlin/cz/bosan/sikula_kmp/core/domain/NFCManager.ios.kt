package cz.bosan.sikula_kmp.core.domain

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import platform.CoreNFC.*
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.data
import platform.darwin.NSObject
import platform.posix.memcpy
import platform.Foundation.*
import platform.darwin.*
import kotlinx.cinterop.*
import platform.CoreNFC.NFCTagProtocol
import platform.CoreNFC.NFCISO7816TagProtocol
import platform.CoreNFC.NFCISO15693TagProtocol
import platform.CoreNFC.NFCFeliCaTagProtocol

/*
actual class NFCManager actual constructor() : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private var readerSession: NFCNDEFReaderSession? = null
    private var onTagRead: ((String) -> Unit)? = null

    actual fun startReading(onTagRead: (String) -> Unit) {
        this.onTagRead = onTagRead
        readerSession = NFCNDEFReaderSession(
            delegate = this,
            queue = null,
            invalidateAfterFirstRead = true
        )
        readerSession?.beginSession()
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        // Session closed or failed
    }

    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {
        val messages = didDetectNDEFs.filterIsInstance<NFCNDEFMessage>()
        val result = messages
            .flatMap { it.records }
            .joinToString("\n") { record ->
                val payload = (record as? NFCNDEFPayload)?.payload
                payload?.toByteArray()?.decodeToString() ?: ""
            }

        onTagRead?.invoke(result)
    }

    actual fun writeTag(data: String, onComplete: (Boolean) -> Unit) {
        // iOS zápis je podporovaný až od iOS 13+ a jen pro NDEF tagy
        // Zde je třeba vytvořit NFCNDEFMessage a použít session.writeNDEF
        onComplete(false) // Placeholder
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray =
        ByteArray(length.toInt()).also { use ->
            memScoped {
                memcpy(use.refTo(0), bytes, length)
            }
        }


    /*actual fun initialize() {
    }*/
}*/

actual class NFCManager actual constructor() : NSObject(), NFCTagReaderSessionDelegateProtocol {

    private var tagSession: NFCTagReaderSession? = null
    private var onTagRead: ((String) -> Unit)? = null

    actual fun startReading(onTagRead: (String) -> Unit) {
        this.onTagRead = onTagRead
        // Použij konstruktor místo alloc+init
        tagSession = NFCTagReaderSession(
            NFCPollingISO14443,
            this,
            null
        )
        tagSession?.beginSession()
    }

    actual fun stopReading() {
        tagSession?.invalidateSession()
        tagSession = null
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        val tags = didDetectTags.filterIsInstance<NFCTagProtocol>()
        if (tags.isEmpty()) {
            session.invalidateSessionWithErrorMessage("No tags found")
            return
        }

        val tag = tags[0]

        // Použij "when" na typ tagu - v Kotlin/Native jsou to interface třídy
        when (tag) {
            is NFCISO7816TagProtocol -> {
                val idData = tag.identifier ?: NSData.data()
                val uid = idData.toByteArray().joinToString(":") {
                    it.toInt().and(0xFF).toString(16).padStart(2, '0').uppercase()
                }
                onTagRead?.invoke("UID: $uid")
            }

            is NFCISO15693TagProtocol -> {
                val idData = tag.identifier ?: NSData.data()
                val uid = idData.toByteArray().joinToString(":") {
                    it.toInt().and(0xFF).toString(16).padStart(2, '0').uppercase()
                }
                onTagRead?.invoke("UID: $uid")
            }

            is NFCFeliCaTagProtocol -> {
                val idData = tag.currentIDm ?: NSData.data()
                val uid = idData.toByteArray().joinToString(":") {
                    it.toInt().and(0xFF).toString(16).padStart(2, '0').uppercase()
                }
                onTagRead?.invoke("UID: $uid")
            }

            else -> {
                session.invalidateSessionWithErrorMessage("Unsupported tag type")
                return
            }
        }
        session.invalidateSession()
    }

    override fun tagReaderSessionDidBecomeActive(session: NFCTagReaderSession) {
        // Session started
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        // Session ended or failed
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray =
        ByteArray(length.toInt()).also { use ->
            memScoped {
                memcpy(use.refTo(0), bytes, length)
            }
        }

    actual fun writeTag(data: String, onComplete: (Boolean) -> Unit) {
    }
}