package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.OutlinedBox
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButton
import cz.bosan.sikula_kmp.features.discipline_management.components.StartStopButtonState
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.last_record
import sikula_kmp.composeapp.generated.resources.unknown_child

@Composable
fun LastTrailRecordBar(
    children: List<Child>,
    record: IndividualDisciplineRecord?,
    onContinueRecording: (Child, IndividualDisciplineRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val child = record?.competitorId.let { childId -> children.find { it.id == childId } }

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedBox(
            title = stringResource(Res.string.last_record),
            content = {
                if (record == null) {
                    Spacer(modifier = modifier.height(47.dp))
                } else {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        modifier = modifier
                            .height(47.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 15.dp
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
                                text = child?.nickName ?: stringResource(Res.string.unknown_child),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.weight(1f))
                            record.improvement?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                            StartStopButton(
                                state = StartStopButtonState.CONTINUE,
                                onClick = { onContinueRecording(child!!, record) },
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
                            record.value?.let {
                                Text(text = formatTrailTime(it.toInt()))
                            }
                        }
                    }
                }
            }
        )
    }
}