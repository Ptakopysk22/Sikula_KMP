package cz.bosan.sikula_kmp.core.domain

import android.app.PendingIntent
import android.content.IntentFilter
import android.app.Activity
import android.content.Intent
import android.nfc.*
import android.os.Build

actual class NFCManager actual constructor() {

    private var activity: Activity? = null

    fun init(activity: Activity) {
        this.activity = activity
    }

    private var onTagRead: ((String) -> Unit)? = null

    actual fun startReading(onTagRead: (String) -> Unit) {
        this.onTagRead = onTagRead
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        if (nfcAdapter != null && nfcAdapter.isEnabled) {

            val intent = Intent(activity, activity!!::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val pendingIntent = PendingIntent.getActivity(
                activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val filters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
                    try {
                        addDataType("*/*")
                    } catch (e: IntentFilter.MalformedMimeTypeException) {
                        throw RuntimeException("Bad MIME type", e)
                    }
                },
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            )

            val techLists = arrayOf(
                arrayOf("android.nfc.tech.Ndef"),
                arrayOf("android.nfc.tech.NdefFormatable"),
                arrayOf("android.nfc.tech.MifareClassic"),
                arrayOf("android.nfc.tech.NfcA"),
                arrayOf("android.nfc.tech.NfcB"),
                arrayOf("android.nfc.tech.NfcF"),
                arrayOf("android.nfc.tech.NfcV"),
                arrayOf("android.nfc.tech.IsoDep"),
            )

            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
        }
    }

    fun handleIntent(intent: Intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            val tagIdString = tag?.id?.joinToString(":") { String.format("%02X", it) } ?: "Unknown ID"

            /*val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)?.toList()
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    ?.mapNotNull { it as? NdefMessage }
            }

            val ndefText = messages?.flatMap { it.records.toList() }
                ?.joinToString("\n") { String(it.payload) }
                ?: ""*/


            val result = tagIdString //+"\nNDEF:\n$ndefText"
            onTagRead?.invoke(result)
        }
    }

    actual fun stopReading() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    actual fun writeTag(data: String, onComplete: (Boolean) -> Unit) {
        onComplete(false)
    }

}