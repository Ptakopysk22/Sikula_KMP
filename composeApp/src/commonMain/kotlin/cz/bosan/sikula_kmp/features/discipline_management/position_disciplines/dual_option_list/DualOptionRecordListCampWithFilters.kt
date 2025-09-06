package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria
import cz.bosan.sikula_kmp.core.presentation.components.GroupCategoryFilterRow
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.data.toSelectableChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.AgilityQuests
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader

@Composable
fun DualOptionRecordListCampWithFilters(
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
    onRecordValueChange: (IndividualDisciplineRecord, String?, String) -> Unit,
    onRecordCreated: (String, Int, Int?) -> Unit,
    discipline: Discipline,
    selectedAgilityFilterCriteria: AgilityFilterCriteria,
    changeAgilityFilterCriteria: (AgilityFilterCriteria) -> Unit,
    selectedAgilityItem: SelectableItem,
    changeSelectedAgilityItem: (SelectableItem) -> Unit,
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
                    DualOptionRecordListCamp(
                        records = records,
                        children = children,
                        onRecordClick = { onRecordClick(it) },
                        groups = groups,
                        crews = crews,
                        trailCategories = trailCategories,
                        leaders = leaders,
                        enabledUpdateRecords = enabledUpdateRecords,
                        onRecordValueChange = { record, value, comment ->
                            onRecordValueChange(record, value, comment)
                        },
                        onRecordCreated = { value, competitorId, quest ->
                            onRecordCreated(
                                value,
                                competitorId,
                                quest
                            )
                        },
                        selectedAgilityFilterCriteria = selectedAgilityFilterCriteria,
                        discipline = discipline,
                        selectedAgilityItem = selectedAgilityItem
                    )
                }
            }
        }
    }
}