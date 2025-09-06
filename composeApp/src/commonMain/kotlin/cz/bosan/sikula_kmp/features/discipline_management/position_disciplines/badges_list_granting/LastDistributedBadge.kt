package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.OutlinedBox
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.last_grant_badge
import sikula_kmp.composeapp.generated.resources.to_return

@Composable
fun LastDistributedBadge(
    children: List<Child>,
    record: BadgeRecord?,
    badgeIdToDisciplineId: Map<Int, Int>,
    onUpdateRecord: (BadgeRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val disciplineId = badgeIdToDisciplineId[record?.badgeId]
    val discipline = getDisciplineById(disciplineId.toString())

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedBox(
            title = stringResource(Res.string.last_grant_badge),
            content = {
                if (record == null) {
                    Spacer(modifier = modifier.height(47.dp))
                } else {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .height(47.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (record.isRemoved) Color.Red else Color.Transparent
                        ),
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
                                text = children.find { it.id == record.competitorId }?.nickName ?: "",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = getDisciplineName(discipline),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            PrimaryButton(
                                content = { Text(text = stringResource(Res.string.to_return)) },
                                enabled = true,
                                onClick = { onUpdateRecord(record) },
                            )
                        }
                    }
                }
            }
        )
    }
}