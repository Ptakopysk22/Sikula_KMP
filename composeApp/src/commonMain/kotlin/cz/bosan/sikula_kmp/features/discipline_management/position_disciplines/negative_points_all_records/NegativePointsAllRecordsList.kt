package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
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
import cz.bosan.sikula_kmp.features.discipline_management.components.IconCheckBox
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordDetail
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.best_score
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation_dot
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.unknown_child_format

@Composable
fun NegativePointsAllRecordsList(
    records: List<IndividualDisciplineRecord>,
    children: List<Child>,
    onRecordClick: (Child) -> Unit,
    onUpdateWorkedOff: (IndividualDisciplineRecord, Boolean) -> Unit,
    crews: List<Crew>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(
            items = records,
            key = { _, record -> record.id!! }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            NegativePointsAllRecordsListItem(
                record = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = onRecordClick,
                crews = crews,
                children = children,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                onUpdateWorkedOff = { onUpdateWorkedOff(record, it) },
            )
        }
    }
}

@Composable
fun NegativePointsAllRecordsListItem(
    record: IndividualDisciplineRecord,
    onClick: (Child) -> Unit,
    modifier: Modifier = Modifier,
    children: List<Child>,
    crews: List<Crew>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    onUpdateWorkedOff: (Boolean) -> Unit,
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

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(child) })
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
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
                Row(
                    modifier = Modifier
                        .width(48.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(
                            Res.string.camp_day_format_abbreviation_dot,
                            record.campDay
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
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
                        if (record.isRecord == true) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Default.Star),
                                contentDescription = stringResource(Res.string.best_score),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Text(
                        text = crews.find { it.id == child.crewId }?.name ?: "",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                IconCheckBox(
                    isChecked = record.workedOff,
                    onCheckedChange = {
                        if (enabledUpdateRecords == true) {
                            onUpdateWorkedOff(it)
                        }
                    },
                    discipline = Discipline.Individual.NEGATIVE_POINTS
                )
                Row(
                    modifier = Modifier.padding(end = 20.dp)
                        .clickable { isExpanded = !isExpanded }) {
                    Text(
                        text = if (record.value == null) stringResource(Res.string.empty_value) else record.value.replace(
                            ".",
                            ","
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
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
            RecordDetail(
                record = record,
                leaders = leaders,
                enabledUpdateRecords = false,
            )
        }
    }
}