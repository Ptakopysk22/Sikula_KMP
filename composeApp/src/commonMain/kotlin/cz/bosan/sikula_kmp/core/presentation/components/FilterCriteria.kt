package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.runtime.Composable
import cz.bosan.sikula_kmp.core.presentation.components.FilterCriteria.*
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.groups
import sikula_kmp.composeapp.generated.resources.trail_categories

enum class FilterCriteria {
    GROUPS,
    TRAIL_CATEGORIES
}

@Composable
fun getCriteriaName(criteria: FilterCriteria): String {
    return when (criteria) {
        GROUPS -> stringResource(Res.string.groups)
        TRAIL_CATEGORIES -> stringResource(Res.string.trail_categories)
    }
}