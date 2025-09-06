package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    circleCount: Int = 5,
    durationMillis: Int = 2500
) {
    val delaysBetweenWaves = remember {
        List(circleCount - 1) { Random.nextInt(200, 1000) }
    }

    val cumulativeDelays = remember(delaysBetweenWaves) {
        buildList {
            add(0)
            delaysBetweenWaves.runningFold(0) { acc, delay -> acc + delay }.drop(1).forEach { add(it) }
        }
    }

    val totalCycleDuration = remember(cumulativeDelays) {
        (cumulativeDelays.maxOrNull() ?: 0) + durationMillis
    }

    val transition = rememberInfiniteTransition(label = "pulse_base")
    val globalProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = totalCycleDuration.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(totalCycleDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_progress"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        for (i in 0 until circleCount) {
            val waveStart = cumulativeDelays[i]
            val localProgress = ((globalProgress - waveStart) / durationMillis).coerceIn(0f, 1f)

            if (localProgress < 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = localProgress
                            scaleY = localProgress
                            alpha = 1f - localProgress
                        }
                        .border(
                            width = 8.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

