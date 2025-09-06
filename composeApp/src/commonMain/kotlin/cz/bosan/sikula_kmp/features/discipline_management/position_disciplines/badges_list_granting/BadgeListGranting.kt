package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.Badge
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.domain.BadgeRecord
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getDisciplineById
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail

@Composable
fun BadgeListGranting(
    records: List<BadgesDisciplineToBeAwarded>,
    children: List<Child>,
    onBadgeGrant: (BadgeRecord) -> Unit,
    crews: List<Crew>,
    campBadges: List<Badge>,
    modifier: Modifier = Modifier,
) {
    val sortedRecords = records.map { record ->
        val discipline = getDisciplineById(record.disciplineId.toString())
        val name = getDisciplineName(discipline)
        val summary = record.badgesSummaryList

        BadgesDisciplineToBeAwardedWithName(record, discipline, name, summary)
    }.sortedBy { it.name }
        .map { RecordWithName ->
            BadgesDisciplineToBeAwarded(
                disciplineId = RecordWithName.discipline.id,
                records = RecordWithName.record.records,
                badgesSummaryList = RecordWithName.summary
            )
        }

    LazyColumn(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(
            items = sortedRecords,
            key = { _, records -> records.disciplineId }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            BadgeListGrantingItem(
                records = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                crews = crews,
                campBadges = campBadges,
                children = children,
                onBadgeGrant = { onBadgeGrant(it) },
            )
        }
    }
}

@Composable
fun BadgeListGrantingItem(
    records: BadgesDisciplineToBeAwarded,
    onBadgeGrant: (BadgeRecord) -> Unit,
    children: List<Child>,
    crews: List<Crew>,
    campBadges: List<Badge>,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val discipline = getDisciplineById(records.disciplineId.toString())


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                        .background(
                            color = discipline.getColor(),
                            shape = CircleShape
                        )

                )
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = getDisciplineName(discipline),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Row(
                    modifier = Modifier.padding(end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (badge in records.badgesSummaryList) {
                        val badgeName = campBadges.find { it.id == badge.badgeId }?.name ?: "?"
                        val cleanedName =
                            badgeName.replace(getDisciplineName(discipline), "").trim()
                        val semiColumn: String = if(cleanedName == "") "" else ":"
                        Text(
                            text = "$cleanedName$semiColumn ${badge.countOfBadges}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }

                Icon(
                    painter = if (!isExpanded) rememberVectorPainter(
                        Icons.Default.KeyboardArrowDown
                    ) else rememberVectorPainter(
                        Icons.Default.KeyboardArrowUp
                    ),
                    contentDescription = stringResource(Res.string.description_item_detail),
                    modifier = Modifier.size(30.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            BadgeGrantingDetail(
                records = records.records,
                children = children,
                crews = crews,
                campBadges = campBadges,
                discipline = discipline,
                onBadgeGrant = { onBadgeGrant(it) },
            )
        }
    }
}