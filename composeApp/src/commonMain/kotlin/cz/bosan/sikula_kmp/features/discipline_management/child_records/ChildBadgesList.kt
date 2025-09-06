package cz.bosan.sikula_kmp.features.discipline_management.child_records

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list.BadgeRecordDetail
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation_dot
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.info
import sikula_kmp.composeapp.generated.resources.unknown_badge

@Composable
fun ChildBadgesList(
    records: List<BadgeRecord>,
    campBadges: List<Badge>,
    leaders: List<Leader>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(
            items = records,
            key = { _, record -> record.id }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            ChildBadgesListItem(
                record = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                campBadges = campBadges,
                leaders = leaders,
            )
        }
    }
}

@Composable
fun ChildBadgesListItem(
    record: BadgeRecord,
    modifier: Modifier = Modifier,
    campBadges: List<Badge>,
    leaders: List<Leader>,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val itemAlpha = if (record.isAwarded || record.toBeRemoved) 1f else 0.6f

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f).graphicsLayer(alpha = itemAlpha),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            border = BorderStroke(
                width = 1.dp,
                color = if (record.toBeRemoved || record.isRemoved) Color.Red else Color.Transparent
            ),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Row(
                    modifier = Modifier
                        .width(48.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(Res.string.camp_day_format_abbreviation_dot, record.campDay),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.padding(horizontal = 30.dp)) {
                    Text(
                        text = campBadges.find { it.id == record.badgeId }?.name ?: stringResource(Res.string.unknown_badge),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Icon(
                    painter = if (!isExpanded) painterResource(Res.drawable.info) else rememberVectorPainter(
                        Icons.Default.KeyboardArrowUp
                    ),
                    contentDescription = stringResource(Res.string.description_item_detail),
                    modifier = Modifier.size(30.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            BadgeRecordDetail(
                record = record,
                leaders = leaders,
                enabledUpdateRecords = false,
            )
        }
    }
}