package cz.bosan.sikula_kmp.features.attendee_management.attendee_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.core.presentation.components.GroupCategoryFilterRow
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory

@Composable
fun ChildListWithFilters(
    warningMessage: UiText?,
    selectedFilterCriteria: FilterCriteria,
    onFilterCriteriaSelected: (FilterCriteria) -> Unit,
    onFilterItemSelected: (SelectableItem?) -> Unit,
    onChildSelected: (Child) -> Unit,
    groups: List<Group>,
    selectedGroup: Group?,
    crews: List<Crew>,
    trailCategories: List<TrailCategory>,
    selectedTrailCategory: TrailCategory?,
    children: List<Child>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
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
                    onFilterItemSelected = { onFilterItemSelected(it) }
                )
                if (warningMessage != null) {
                    Message(text = warningMessage.asString(), messageTyp = MessageTyp.WARNING)
                } else {
                    ChildList(
                        children = children,
                        onChildClick = { onChildSelected(it) },
                        scrollState = scrollState,
                        groups = groups,
                        crews = crews,
                        trailCategory = trailCategories
                    )
                }
    }
}