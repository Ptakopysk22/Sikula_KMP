package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import cz.bosan.sikula_kmp.core.presentation.components.CategorySurface
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.info
import sikula_kmp.composeapp.generated.resources.unknown_badge
import sikula_kmp.composeapp.generated.resources.unknown_child_format

@Composable
fun BadgeList(
    records: List<BadgeRecord>,
    children: List<Child>,
    onRecordClick: (Child) -> Unit,
    onRecordChange: (BadgeRecord) -> Unit,
    groups: List<Group>,
    crews: List<Crew>,
    campBadges: List<Badge>,
    trailCategories: List<TrailCategory>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
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
            BadgeListItem(
                record = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = onRecordClick,
                groups = groups,
                crews = crews,
                campBadges = campBadges,
                trailCategories = trailCategories,
                children = children,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                onRecordChange = { onRecordChange(record) },
            )
        }
    }
}

@Composable
fun BadgeListItem(
    record: BadgeRecord,
    onClick: (Child) -> Unit,
    onRecordChange: () -> Unit,
    modifier: Modifier = Modifier,
    children: List<Child>,
    groups: List<Group>,
    crews: List<Crew>,
    campBadges: List<Badge>,
    trailCategories: List<TrailCategory>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val child = children.find { it.id == record.competitorId }
        ?: Child(
            id = record.competitorId,
            name = "",
            nickName = stringResource(Res.string.unknown_child_format, record.competitorId),
            birthDate = null,
            role = ChildRole.MEMBER,
            isActive = true,
            groupId = null,
            crewId = null,
            trailCategoryId = null
        )
    val itemAlpha = if (record.isAwarded || record.toBeRemoved) 1f else 0.6f

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(child) })
            .fillMaxWidth()
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
                    .padding(end = 4.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                CategorySurface(
                    child = child,
                    groupColor = groups.find { it.id == child.groupId }?.color ?: Color.Black,
                    trailCategories = trailCategories
                )
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = child.nickName,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    Text(
                        text = crews.find { it.id == child.crewId }?.name ?: "",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Row(modifier = Modifier.padding(end = 20.dp)) {
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
                enabledUpdateRecords = enabledUpdateRecords,
                onToBeGranted = onRecordChange
            )
        }
    }
}