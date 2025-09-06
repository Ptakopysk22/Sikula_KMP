package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.formatDateTime
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.badge_state_format
import sikula_kmp.composeapp.generated.resources.badge_state_granted
import sikula_kmp.composeapp.generated.resources.badge_state_removed
import sikula_kmp.composeapp.generated.resources.badge_state_to_be_granted
import sikula_kmp.composeapp.generated.resources.badge_state_to_be_removed
import sikula_kmp.composeapp.generated.resources.date_and_time_format
import sikula_kmp.composeapp.generated.resources.id
import sikula_kmp.composeapp.generated.resources.id_format
import sikula_kmp.composeapp.generated.resources.processed_by_format
import sikula_kmp.composeapp.generated.resources.unknown_leader
import sikula_kmp.composeapp.generated.resources.unknown_state

@Composable
fun BadgeRecordDetail(
    record: BadgeRecord,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean? = false,
    onToBeGranted: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    val refereeNickname: String =
        leaders.find { it.id == record.refereeId }?.nickName ?: stringResource(
            Res.string.id,
            record.refereeId ?: stringResource(Res.string.unknown_leader)
        )
    val badgeState: String = if (record.isAwarded) {
        stringResource(Res.string.badge_state_granted)
    } else if (record.toBeAwarded) {
        stringResource(Res.string.badge_state_to_be_granted)
    } else if (record.isRemoved) {
        stringResource(Res.string.badge_state_removed)
    } else if (record.toBeRemoved) {
        stringResource(Res.string.badge_state_to_be_removed)
    } else {
        stringResource(Res.string.unknown_state)
    }
    val itemAlpha = if (record.isAwarded || record.toBeRemoved) 1f else 0.6f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-10).dp)
            .graphicsLayer(alpha = itemAlpha),
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 2.dp, end = 10.dp, bottom = 4.dp)) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.id_format, record.id),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(Res.string.badge_state_format, badgeState),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(Res.string.processed_by_format, refereeNickname),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(
                    Res.string.date_and_time_format,
                    formatDateTime(record.timeStamp)
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (enabledUpdateRecords == true && (record.isAwarded || record.isRemoved)) {
            Row(
                modifier = modifier.fillMaxSize().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                PrimaryButton(
                    content = {
                        Text(
                            text = if (record.isAwarded) stringResource(Res.string.badge_state_to_be_granted)
                            else stringResource(Res.string.badge_state_to_be_removed),
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    onClick = {
                        onToBeGranted()
                    },
                    enabled = true,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}
