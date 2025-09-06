package cz.bosan.sikula_kmp.core.data

import platform.Foundation.NSDictionary
import platform.Foundation.NSURL
import platform.Foundation.dictionary
import platform.UIKit.UIApplication

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        //UIApplication.sharedApplication.openURL(nsUrl) // staré, deprecated

        // Nově použij toto:
        UIApplication.sharedApplication.openURL(nsUrl, options = NSDictionary.dictionary(), completionHandler = { success: Boolean ->
            if (success) {
                println("URL úspěšně otevřena")
            } else {
                println("Nepodařilo se otevřít URL")
            }
        })
    }
}