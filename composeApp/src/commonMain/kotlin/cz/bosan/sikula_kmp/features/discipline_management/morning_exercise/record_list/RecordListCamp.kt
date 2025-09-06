package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.core.presentation.components.CategorySurface
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.features.discipline_management.components.IconCheckBox
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordDetail
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordingElement
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.best_score
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.empty_duration
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.unknown_child_format

@Composable
fun RecordListCamp(
    records: List<IndividualDisciplineRecord>,
    children: List<Child>,
    onRecordClick: (Child) -> Unit,
    onRecordValueChange: (IndividualDisciplineRecord, String?, String) -> Unit,
    modifier: Modifier = Modifier,
    groups: List<Group>,
    crews: List<Crew>,
    trailCategories: List<TrailCategory>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    showTimePickers: SnapshotStateMap<Int, Boolean>,
    onShowTimePickerChange: (Int) -> Unit,
    onUpdateTimeClick: (Int) -> Unit,
    onUpdateCountsForImprovement: (IndividualDisciplineRecord, Boolean) -> Unit,
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
            RecordListCampItem(
                record = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = onRecordClick,
                groups = groups,
                crews = crews,
                trailCategories = trailCategories,
                children = children,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                discipline = discipline,
                onRecordValueChange = { value ->
                    onRecordValueChange(
                        record,
                        value,
                        record.comment
                    )
                },
                onRecordCommentChange = { comment ->
                    onRecordValueChange(
                        record,
                        record.value,
                        comment
                    )
                },
                showTimePicker = showTimePickers[record.id] ?: false,
                onShowTimePickerChange = { onShowTimePickerChange(record.id!!) },
                onUpdateTimeClick = { onUpdateTimeClick(record.id!!) },
                onUpdateCountsForImprovement = { onUpdateCountsForImprovement(record, it) }
            )
        }
    }
}

@Composable
fun RecordListCampItem(
    record: IndividualDisciplineRecord,
    onClick: (Child) -> Unit,
    onRecordValueChange: (String?) -> Unit,
    onRecordCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    children: List<Child>,
    groups: List<Group>,
    crews: List<Crew>,
    trailCategories: List<TrailCategory>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    showTimePicker: Boolean,
    onShowTimePickerChange: () -> Unit,
    onUpdateTimeClick: () -> Unit,
    onUpdateCountsForImprovement: (Boolean) -> Unit,
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
                    .padding(end = 6.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = child.nickName,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        if (record.isRecord == true) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Sharp.Star),
                                contentDescription = stringResource(Res.string.best_score),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        text = crews.find { it.id == child.crewId }?.name ?: "",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                if (discipline == Discipline.Individual.TRAIL && record.countsForImprovement == false && !isExpanded || (discipline == Discipline.Individual.TRAIL && isExpanded && enabledUpdateRecords != true)) {
                    IconCheckBox(
                        isChecked = record.countsForImprovement,
                        onCheckedChange = { isExpanded = !isExpanded },
                        discipline = discipline
                    )
                }
                if (enabledUpdateRecords == true && isExpanded) {
                    if (discipline == Discipline.Individual.TRAIL) {
                        IconCheckBox(
                            isChecked = record.countsForImprovement,
                            onCheckedChange = { onUpdateCountsForImprovement(it) },
                            discipline = discipline
                        )
                    }
                    RecordingElement(
                        discipline = discipline,
                        clickedItemName = record.value.toString(),
                        onValueChange = { onRecordValueChange(it) },
                        hideKeyboardAfterCheck = true,
                        showCrossIcon = true,
                        showTimePicker = showTimePicker,
                        onShowTimePickerChange = onShowTimePickerChange,
                        onUpdateTimeClick = onUpdateTimeClick,
                    )
                } else {
                    Row(
                        modifier = Modifier.padding(end = 20.dp)
                            .clickable { isExpanded = !isExpanded }) {
                        Text(
                            text = if (discipline == Discipline.Individual.TRAIL && record.value != null && record.value != "null") {
                                formatTrailTime(record.value.toInt())
                            } else if (discipline == Discipline.Individual.TRAIL) {
                                stringResource(Res.string.empty_duration)
                            } else {
                                if (record.value == null) stringResource(Res.string.empty_value) else record.value.toString()
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (discipline == Discipline.Individual.TRAIL) {
                            Text(
                                text = " (${record.improvement ?: stringResource(Res.string.empty_value)})",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
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
            RecordDetail(
                record = record,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                onSaveComment = onRecordCommentChange
            )
        }
    }
}