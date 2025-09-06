package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list

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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.components.CategorySurface
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordDetail
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordingElement
import cz.bosan.sikula_kmp.managers.children_manager.data.toSelectableChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.AgilityQuests
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.getAgilityName
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.accomplish
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.info
import sikula_kmp.composeapp.generated.resources.level
import sikula_kmp.composeapp.generated.resources.not_accomplish
import sikula_kmp.composeapp.generated.resources.unknown_agility_typ
import sikula_kmp.composeapp.generated.resources.unknown_child_format

@Composable
fun DualOptionRecordListGroup(
    records: List<IndividualDisciplineRecord>,
    children: List<Child>,
    onRecordClick: (Child) -> Unit,
    onRecordValueChange: (IndividualDisciplineRecord, String?, String) -> Unit,
    onRecordCreated: (String, Int, Int?) -> Unit,
    leaders: List<Leader>,
    trailCategories: List<TrailCategory>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    selectedAgilityFilterCriteria: AgilityFilterCriteria,
    changeAgilityFilterCriteria: (AgilityFilterCriteria) -> Unit,
    selectedAgilityItem: SelectableItem,
    changeSelectedAgilityItem: (SelectableItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        if (discipline == Discipline.Individual.AGILITY) {
            AgilityFilterRow(
                quests = AgilityQuests,
                children = children.map { it.toSelectableChild() }.sortedBy { it.name },
                selectedAgilityFilterCriteria = selectedAgilityFilterCriteria,
                changeAgilityFilterCriteria = { changeAgilityFilterCriteria(it) },
                onItemSelected = { changeSelectedAgilityItem(it) },
                selectedItem = selectedAgilityItem
            )
        }
        LazyColumn(
            modifier = modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            itemsIndexed(
                items = records,
                key = { _, record -> "${record.id}_${record.competitorId}_${record.quest}" }
            ) { index, record ->
                val isLastItem = index == records.lastIndex
                RecordListSingleGroupItem(
                    record = record,
                    child = children.find { it.id == record.competitorId }
                        ?: Child.EMPTY.copy(
                            id = record.competitorId,
                            nickName = stringResource(
                                Res.string.unknown_child_format,
                                record.competitorId
                            )
                        ),
                    modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                        .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                    onClick = { onRecordClick(it) },
                    leaders = leaders,
                    discipline = discipline,
                    trailCategories = trailCategories,
                    enabledUpdateRecords = enabledUpdateRecords,
                    onRecordValueChange = { value ->
                        if (record.id == 0) {
                            onRecordCreated(value, record.competitorId, record.quest)
                        } else {
                            onRecordValueChange(
                                record,
                                value,
                                record.comment
                            )
                        }
                    },
                    onRecordCommentChange = { comment ->
                        onRecordValueChange(
                            record,
                            record.value,
                            comment
                        )
                    },
                    selectedAgilityFilterCriteria = selectedAgilityFilterCriteria,
                    selectedAgilityItem = selectedAgilityItem
                )
            }
        }
    }
}


@Composable
fun RecordListSingleGroupItem(
    record: IndividualDisciplineRecord,
    child: Child,
    onClick: (Child) -> Unit,
    onRecordValueChange: (String) -> Unit,
    onRecordCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leaders: List<Leader>,
    trailCategories: List<TrailCategory>,
    enabledUpdateRecords: Boolean?,
    selectedAgilityFilterCriteria: AgilityFilterCriteria,
    selectedAgilityItem: SelectableItem,
    discipline: Discipline,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(child) })
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = Color.Transparent
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
                CategorySurface(
                    child = child,
                    groupColor = Color.Gray.copy(alpha = 0.2f),
                    trailCategories = trailCategories
                )
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    if (discipline == Discipline.Individual.AGILITY) {
                        if (selectedAgilityItem.id == 0) {
                            Text(
                                text = if (selectedAgilityFilterCriteria == AgilityFilterCriteria.QUESTS) getAgilityName(
                                    record.quest
                                )
                                    ?: stringResource(Res.string.unknown_agility_typ) else child.nickName,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = if (selectedAgilityFilterCriteria == AgilityFilterCriteria.CHILDREN) getAgilityName(
                                    record.quest
                                )
                                    ?: stringResource(Res.string.unknown_agility_typ) else child.nickName,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        } else {
                            Text(
                                text = if (selectedAgilityFilterCriteria == AgilityFilterCriteria.CHILDREN) getAgilityName(
                                    record.quest
                                )
                                    ?: stringResource(Res.string.unknown_agility_typ) else child.nickName,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    } else {
                        Text(
                            text = child.nickName,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                if (enabledUpdateRecords == true) {
                    if (discipline == Discipline.Individual.AGILITY) {
                        RecordingElement(
                            discipline = discipline,
                            clickedItemName = record.value,
                            onValueChange = { onRecordValueChange(it!!) },
                            showCrossIcon = false,
                            hideKeyboardAfterCheck = true,
                        )
                    } else {
                        Switcher(
                            selectedButtonIndex = if (record.value == null) null else record.value.toInt(),
                            firstLabel = stringResource(Res.string.not_accomplish),
                            secondLabel = stringResource(Res.string.accomplish),
                            onFirstClick = {
                                onRecordValueChange("0")
                            },
                            onSecondClick = { onRecordValueChange("1") },
                        )
                    }
                } else {
                    Row(modifier = Modifier.padding(end = 20.dp)) {
                        if (discipline == Discipline.Individual.AGILITY) {
                            Text(
                                text = if (record.value == null || record.value == "0") stringResource(
                                    Res.string.empty_value
                                ) else "${record.value}.${stringResource(Res.string.level)}",
                                style = MaterialTheme.typography.titleMedium
                            )

                        } else {
                            Text(
                                text = if (record.value == "1") stringResource(Res.string.accomplish) else if (record.value == "0") stringResource(
                                    Res.string.not_accomplish
                                ) else stringResource(Res.string.empty_value),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
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
            RecordDetail(
                record = record,
                leaders = leaders,
                enabledUpdateRecords = enabledUpdateRecords,
                onSaveComment = onRecordCommentChange
            )
        }
    }
}