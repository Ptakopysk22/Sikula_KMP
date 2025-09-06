package cz.bosan.sikula_kmp.features.discipline_management.components

import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord
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
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.best_score
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.empty_duration
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.unknown_crew

@Composable
fun TeamRecordList(
    records: List<TeamDisciplineRecord>,
    crews: List<Crew>,
    onRecordClick: (Crew) -> Unit,
    onRecordChange: (TeamDisciplineRecord, String?, String) -> Unit,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    showTimePickers: SnapshotStateMap<Int, Boolean>,
    onShowTimePickerChange: (Int) -> Unit,
    onUpdateTimeClick: (Int) -> Unit,
    onUpdateCountsForImprovement: (TeamDisciplineRecord, Boolean) -> Unit,
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
            key = { _, record -> record.id!! }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            TeamRecordListItem(
                record = record,
                crew = crews.find { it.id == record.crewId }
                    ?: Crew(
                        id = 0,
                        groupId = 0,
                        name = stringResource(Res.string.unknown_crew),
                        color = Color.Black
                    ),
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = { onRecordClick(it) },
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                discipline = discipline,
                onRecordValueChange = { value ->
                    onRecordChange(
                        record,
                        value,
                        record.comment
                    )
                },
                onRecordCommentChange = { comment ->
                    onRecordChange(
                        record,
                        record.value,
                        comment
                    )
                },
                showTimePicker = showTimePickers[record.id] ?: false,
                onShowTimePickerChange = { onShowTimePickerChange(record.id!!) },
                onUpdateTimeClick = { onUpdateTimeClick(record.id!!) },
                onUpdateCountsForImprovement = { onUpdateCountsForImprovement(record, it) },
            )
        }
    }
}

@Composable
fun TeamRecordListItem(
    record: TeamDisciplineRecord,
    crew: Crew,
    onClick: (Crew) -> Unit,
    onRecordValueChange: (String?) -> Unit,
    onRecordCommentChange: (String) -> Unit,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    showTimePicker: Boolean,
    onShowTimePickerChange: () -> Unit,
    onUpdateTimeClick: () -> Unit,
    onUpdateCountsForImprovement: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(crew) })
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = if (record.isUploaded == false) Color.Red else Color.Transparent
            ),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(end = 4.dp).fillMaxWidth()
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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = crew.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (record.improvementsAndRecords?.isRecord == true) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Sharp.Star),
                                contentDescription = stringResource(Res.string.best_score),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                if (discipline == Discipline.Team.BOAT_RACE && record.improvementsAndRecords?.countsForImprovements == false && !isExpanded || (discipline == Discipline.Team.BOAT_RACE && isExpanded && enabledUpdateRecords != true)) {
                    IconCheckBox(
                        isChecked = record.improvementsAndRecords?.countsForImprovements,
                        onCheckedChange = { isExpanded = !isExpanded },
                        discipline = discipline
                    )
                }
                if (enabledUpdateRecords == true && isExpanded) {
                    if (discipline == Discipline.Team.BOAT_RACE) {
                        IconCheckBox(
                            isChecked = record.improvementsAndRecords?.countsForImprovements,
                            onCheckedChange = { onUpdateCountsForImprovement(it) },
                            discipline = discipline
                        )
                    }
                    RecordingElement(
                        discipline = discipline,
                        clickedItemName = record.value.toString(),
                        onValueChange = { onRecordValueChange(it) },
                        showCrossIcon = true,
                        hideKeyboardAfterCheck = true,
                        showTimePicker = showTimePicker,
                        onShowTimePickerChange = onShowTimePickerChange,
                        onUpdateTimeClick = onUpdateTimeClick,
                        chooseFromResultOption = false
                    )
                } else {
                    Row(
                        modifier = Modifier.padding(end = 20.dp)
                            .clickable { isExpanded = !isExpanded }) {
                        Text(
                            text = if (discipline == Discipline.Team.BOAT_RACE && record.value != null) {
                                formatTrailTime(record.value.toInt())
                            } else if (discipline == Discipline.Team.BOAT_RACE) {
                                stringResource(Res.string.empty_duration)
                            } else {
                                if (record.value == null) stringResource(Res.string.empty_value) else record.value.toString()
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (discipline == Discipline.Team.BOAT_RACE) {
                            Text(
                                text = " (${
                                    record.improvementsAndRecords?.improvementString ?: stringResource(
                                        Res.string.empty_value
                                    )
                                })",
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
            RecordDetailTeamDiscipline(
                record = record,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                onSaveComment = onRecordCommentChange
            )
        }
    }
}