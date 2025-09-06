package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.features.discipline_management.components.AlertDialogRestartChild
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButton
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButtonState
import cz.bosan.sikula_kmp.features.discipline_management.components.TimerComponent
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.arrow_counter_clockwise
import sikula_kmp.composeapp.generated.resources.description_restart

@Composable
fun TrailRecordingListOnTrail(
    childTimers: List<ChildTimer>,
    scrollState: LazyListState = rememberLazyListState(),
    onStopRecording: (Child) -> Unit,
    onRestart: (Child) -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier.padding(vertical = 4.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        itemsIndexed(
            items = childTimers,
            key = { _, childTimer -> childTimer.child.id }
        ) { index, childTimer ->
            val isLastItem = index == childTimers.lastIndex

            TrailRecodingListOnTrailItem(
                child = childTimer.child,
                startTime = childTimer.startTime,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onStopRecording = { onStopRecording(childTimer.child) },
                onRestart = { onRestart(childTimer.child) }
            )
        }
    }
}


@Composable
fun TrailRecodingListOnTrailItem(
    child: Child,
    startTime: Instant,
    onStopRecording: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .height(47.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 2.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            Text(
                text = child.nickName,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(Res.drawable.arrow_counter_clockwise),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(Res.string.description_restart),
                modifier = Modifier.padding(end = 20.dp).clickable { showDialog = true }
            )
            StartStopButton(
                state = StartStopButtonState.STOP,
                onClick = { onStopRecording() },
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 2.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Text(text = TimerComponent(startTime = startTime))
        }

    }
    if (showDialog) {
        AlertDialogRestartChild(
            itemName = child.nickName,
            discipline = Discipline.Individual.TRAIL,
            onCancel = { showDialog = false },
            onConfirm = {
                showDialog = false
                onRestart()
            }
        )
    }
}

