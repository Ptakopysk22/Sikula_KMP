package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.runtime.Composable
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_unknown_date_time
import sikula_kmp.composeapp.generated.resources.unknown_birthdate

@Composable
fun formatDate(localDate: LocalDate?): String {
    val format = LocalDate.Format {
        dayOfMonth()
        chars(".")
        monthNumber()
        chars(".")
        year()
    }
    return localDate?.let { format.format(it) } ?: stringResource(Res.string.unknown_birthdate)
}

@Composable
fun formatDateTime(localDateTime: LocalDateTime?): String {
    val format = LocalDateTime.Format {
        dayOfMonth()
        chars(".")
        monthNumber()
        chars(".")
        year()
        chars(" ")
        hour()
        chars(":")
        minute()
        chars(":")
        second()
    }
    return localDateTime?.let { format.format(it) } ?: stringResource(Res.string.description_unknown_date_time)
}

@Composable
fun formatTrailTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "$minutes:${secs.toString().padStart(2, '0')}"
}

