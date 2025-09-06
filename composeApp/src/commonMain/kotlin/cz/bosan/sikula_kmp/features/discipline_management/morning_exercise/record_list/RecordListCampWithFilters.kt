package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.core.presentation.components.GroupCategoryFilterRow
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader

@Composable
fun RecordListCampWithFilters(
    errorMessage: UiText?,
    warningMessage: UiText?,
    selectedFilterCriteria: FilterCriteria,
    onFilterCriteriaSelected: (FilterCriteria) -> Unit,
    onFilterItemSelected: (SelectableItem?) -> Unit,
    onRecordClick: (Child) -> Unit,
    groups: List<Group>,
    selectedGroup: Group?,
    crews: List<Crew>,
    trailCategories: List<TrailCategory>,
    selectedTrailCategory: TrailCategory?,
    children: List<Child>,
    records: List<IndividualDisciplineRecord>,
    leaders: List<Leader>,
    enabledUpdateRecords: Boolean?,
    discipline: Discipline,
    onRecordValueChange: (IndividualDisciplineRecord, String?, String) -> Unit,
    dayRecordStates: List<Pair<Group, DayRecordsState>> = emptyList(),
    showTimePickers: SnapshotStateMap<Int, Boolean>,
    onShowTimePickerChange: (Int) -> Unit,
    onUpdateTimeClick: (Int) -> Unit,
    onUpdateCountsForImprovement: (IndividualDisciplineRecord, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        errorMessage != null -> {
            Message(text = errorMessage.asString(), messageTyp = MessageTyp.ERROR)
        }

        else -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Top
            ) {
                GroupCategoryFilterRow(
                    selectedFilterCriteria = selectedFilterCriteria,
                    onCriteriaSelected = { onFilterCriteriaSelected(it) },
                    groups = groups,
                    trailCategories = trailCategories,
                    selectedGroup = selectedGroup,
                    selectedTrailCategory = selectedTrailCategory,
                    onFilterItemSelected = { onFilterItemSelected(it) },
                    dayRecordStates = dayRecordStates,
                    showDayStateRecords = true
                )
                if (warningMessage != null) {
                    Message(text = warningMessage.asString(), messageTyp = MessageTyp.WARNING)
                } else {
                    RecordListCamp(
                        records = records,
                        children = children,
                        onRecordClick = { onRecordClick(it) },
                        groups = groups,
                        crews = crews,
                        trailCategories = trailCategories,
                        leaders = leaders,
                        enabledUpdateRecords = enabledUpdateRecords,
                        discipline = discipline,
                        onRecordValueChange = { record, value, comment ->
                            onRecordValueChange(record, value, comment)
                        },
                        showTimePickers = showTimePickers,
                        onShowTimePickerChange = onShowTimePickerChange,
                        onUpdateTimeClick = onUpdateTimeClick,
                        onUpdateCountsForImprovement = onUpdateCountsForImprovement,
                    )
                }
            }
        }
    }
}