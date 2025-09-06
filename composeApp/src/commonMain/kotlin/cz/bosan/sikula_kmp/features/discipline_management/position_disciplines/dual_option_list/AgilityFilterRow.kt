package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.domain.SelectableItem
import cz.bosan.sikula_kmp.core.presentation.components.forms.DropDownTextField
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.managers.children_manager.domain.SelectableChild
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.AgilityQuest
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.all_of_them
import sikula_kmp.composeapp.generated.resources.children
import sikula_kmp.composeapp.generated.resources.individual_discipline_agility

@Composable
fun AgilityFilterRow(
    quests: List<AgilityQuest>,
    children: List<SelectableChild>,
    selectedAgilityFilterCriteria: AgilityFilterCriteria,
    changeAgilityFilterCriteria: (AgilityFilterCriteria) -> Unit,
    onItemSelected: (SelectableItem) -> Unit,
    selectedItem: SelectableItem?,

    ) {
    var isExpand by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val childrenWithAll = listOf(SelectableChild(id = 0, name = stringResource(Res.string.all_of_them))) + children

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Switcher(
            selectedButtonIndex = if (selectedAgilityFilterCriteria == AgilityFilterCriteria.QUESTS) 0 else 1,
            firstLabel = getAgilityCriteriaName(AgilityFilterCriteria.QUESTS),
            secondLabel = getAgilityCriteriaName(AgilityFilterCriteria.CHILDREN),
            onFirstClick = {
                changeAgilityFilterCriteria(AgilityFilterCriteria.QUESTS)
                onItemSelected(quests[0])
            },
            onSecondClick = {
                changeAgilityFilterCriteria(AgilityFilterCriteria.CHILDREN)
                onItemSelected(childrenWithAll[0])
            },
            arrangement = Arrangement.Start,
            modifier = Modifier
        )
        DropDownTextField(
            expand = isExpand,
            onExpandChange = { isExpand = it },
            text = selectedItem?.name ?: stringResource(Res.string.all_of_them),
            label =
            if (selectedAgilityFilterCriteria == AgilityFilterCriteria.QUESTS) getAgilityCriteriaName(
                AgilityFilterCriteria.QUESTS
            )
            else getAgilityCriteriaName(AgilityFilterCriteria.CHILDREN),
            items = if (selectedAgilityFilterCriteria == AgilityFilterCriteria.QUESTS) quests else childrenWithAll,
            onItemClick = {
                onItemSelected(it)
                isExpand = false
            },
            keyboardController = keyboardController,
            itemToString = { it.name },
            isValid = true,
            startPadding = 16.dp,
            dropDownStartPadding = 150.dp,
            endPadding = 15.dp,
            errorMessage = "",
        )
    }
}

enum class AgilityFilterCriteria {
    CHILDREN,
    QUESTS
}

@Composable
fun getAgilityCriteriaName(criteria: AgilityFilterCriteria): String {
    return when (criteria) {
        AgilityFilterCriteria.CHILDREN -> stringResource(Res.string.children)
        AgilityFilterCriteria.QUESTS -> stringResource(Res.string.individual_discipline_agility)
    }
}