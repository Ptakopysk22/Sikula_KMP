package cz.bosan.sikula_kmp.features.discipline_management.child_records

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.BackButtonRow
import cz.bosan.sikula_kmp.core.presentation.components.CategoryTag
import cz.bosan.sikula_kmp.managers.children_manager.data.LightChild
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildRecordsTopBar(
    onBackClick: () -> Unit,
    child: LightChild,
    trailCategories: List<TrailCategory>,
    modifier: Modifier = Modifier,
) {

    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    BackButtonRow(
                        onBackClick = onBackClick,
                        keyboardController = null,
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    trailCategories.find { it.id == child.trailCategoryId }?.let {
                        CategoryTag(trailCategory = it, showBigIcon = true)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(133.dp)
                        .height(43.dp)
                        .border(4.dp, color = Color.Black, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = child.nickname,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
    )
}