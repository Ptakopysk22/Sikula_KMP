package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.components.forms.FilterDropdown
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState

@Composable
fun GroupCategoryFilterRow(
    selectedFilterCriteria: FilterCriteria,
    onCriteriaSelected: (FilterCriteria) -> Unit,
    groups: List<Group>,
    trailCategories: List<TrailCategory>,
    selectedGroup: SelectableItem?,
    selectedTrailCategory: SelectableItem?,
    onFilterItemSelected: (SelectableItem?) -> Unit,
    dayRecordStates: List<Pair<Group, DayRecordsState>> = emptyList(),
    showDayStateRecords: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterDropdown(
            selectedCriteria = selectedFilterCriteria,
            onCriteriaSelected = { onCriteriaSelected(it) }
        )
        FilterBar(
            items = if (selectedFilterCriteria == FilterCriteria.GROUPS) {
                groups
            } else {
                trailCategories
            },
            clickedItemName = if (selectedFilterCriteria == FilterCriteria.GROUPS) {
                selectedGroup?.name ?: "all"
            } else {
                selectedTrailCategory?.name ?: "all"
            },
            onItemClick = { onFilterItemSelected(it) },
            dayRecordStates = dayRecordStates,
            showDayStateRecords = showDayStateRecords
        )
    }

}