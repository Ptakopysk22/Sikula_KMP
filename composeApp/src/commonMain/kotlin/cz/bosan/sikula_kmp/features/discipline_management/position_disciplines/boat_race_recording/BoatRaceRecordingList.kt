package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording

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
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.features.discipline_management.components.AlertDialogRestartChild
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButton
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButtonState
import cz.bosan.sikula_kmp.features.discipline_management.components.TimerComponent
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.arrow_counter_clockwise
import sikula_kmp.composeapp.generated.resources.description_restart
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.zero_time

@Composable
fun BoatRaceRecordingList(
    crews: List<Crew>,
    crewTimers: List<CrewTimer>,
    finishedRecords: Map<TeamDisciplineRecord, Instant>,
    scrollState: LazyListState = rememberLazyListState(),
    onStartRecording: (Crew) -> Unit,
    onStopRecording: (Crew) -> Unit,
    onContinueRecording: (Crew, TeamDisciplineRecord) -> Unit,
    onRestart: (Crew) -> Unit,
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
            items = crews,
            key = { _, crew -> crew.id }
        ) { index, crew ->
            val isLastItem = index == crews.lastIndex

            BoatRaceRecordingListItem(
                crew = crew,
                crewTimer = crewTimers.find { it.crew == crew },
                finishedRecords = finishedRecords,
                onStartRecording = { onStartRecording(crew) },
                onStopRecording = { onStopRecording(crew) },
                onContinueRecording = { onContinueRecording(crew, it) },
                onRestart = { onRestart(crew) },
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
            )
        }
    }
}


@Composable
fun BoatRaceRecordingListItem(
    crew: Crew,
    crewTimer: CrewTimer?,
    finishedRecords: Map<TeamDisciplineRecord, Instant>,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onContinueRecording: (TeamDisciplineRecord) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    val startTime = crewTimer?.startTime
    val finishedEntry = finishedRecords.entries.find { it.key.crewId == crew.id }
    val finishedRecord = finishedEntry?.key
    val finishedTime = finishedRecord?.value
    val itemState = when {
        crewTimer != null -> StartStopButtonState.STOP
        finishedEntry != null -> StartStopButtonState.CONTINUE
        else -> StartStopButtonState.START
    }

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
                text = crew.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            if (crewTimer != null) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_counter_clockwise),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(Res.string.description_restart),
                    modifier = Modifier.padding(end = 20.dp).clickable { showDialog = true }
                )
            }
            StartStopButton(
                state = itemState,
                onClick = {
                    if (itemState == StartStopButtonState.START) {
                        onStartRecording()
                    } else if (itemState == StartStopButtonState.STOP) {
                        onStopRecording()
                    } else {
                        onContinueRecording(finishedRecord!!)
                    }
                },
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
            if (itemState == StartStopButtonState.STOP) {
                Text(TimerComponent(startTime = startTime!!))
            } else if (itemState == StartStopButtonState.CONTINUE) {
                Text("${formatTrailTime(finishedTime!!.toInt())} (${finishedRecord.improvementsAndRecords?.improvementString?: stringResource(Res.string.empty_value)})")
            } else {
                Text(stringResource(Res.string.zero_time))
            }
        }

    }
    if (showDialog) {
        AlertDialogRestartChild(
            itemName = crew.name,
            discipline = Discipline.Team.BOAT_RACE,
            onCancel = { showDialog = false },
            onConfirm = {
                showDialog = false
                onRestart()
            }
        )
    }
}
