package cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline

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
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordingElement
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.last_record

@Composable
fun LastFillRecordBarTeamDiscipline(
    crew: Crew?,
    record: TeamDisciplineRecord?,
    discipline: Discipline,
    onUpdateRecord: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {

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
                        modifier = Modifier
                            .height(47.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
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
                                text = crew?.name ?: "",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RecordingElement(
                                discipline = discipline,
                                clickedItemName = record.value,
                                onValueChange = { onUpdateRecord(it) },
                            )
                        }
                    }
                }
            }
        )
    }
}