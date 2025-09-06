package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun TimerComponent(startTime: Instant): String {
    var currentTime by remember { mutableStateOf(Clock.System.now()) }

    LaunchedEffect(startTime) {
        while (true) {
            delay(1000)
            currentTime = Clock.System.now()
        }
    }

    val elapsedSeconds = currentTime.epochSeconds - startTime.epochSeconds
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}