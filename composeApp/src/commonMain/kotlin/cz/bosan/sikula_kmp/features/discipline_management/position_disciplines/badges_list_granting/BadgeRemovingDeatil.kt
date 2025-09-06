package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.remove
import sikula_kmp.composeapp.generated.resources.unknown_child
import sikula_kmp.composeapp.generated.resources.unknown_crew
import sikula_kmp.composeapp.generated.resources.unknown_level

@Composable
fun BadgeRemovingDetail(
    records: List<BadgeRecord>,
    children: List<Child>,
    crews: List<Crew>,
    campBadges: List<Badge>,
    onBadgeGrant: (BadgeRecord) -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-10).dp),
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            for (record in records) {
                val child = children.find { it.id == record.competitorId }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.height(47.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    )
                    {
                        Column(
                            modifier = Modifier.fillMaxHeight().weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = child?.nickName ?: stringResource(Res.string.unknown_child),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = crews.find { it.id == child?.crewId }?.name
                                    ?: stringResource(Res.string.unknown_crew),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        Text(
                            text = campBadges.find { it.id == record.badgeId }?.name
                                ?: stringResource(Res.string.unknown_level),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        PrimaryButton(
                            enabled = true,
                            onClick = { onBadgeGrant(record) },
                            content = {
                                Text(text = stringResource(Res.string.remove))
                            }
                        )
                    }
                }
            }
        }
    }
}
