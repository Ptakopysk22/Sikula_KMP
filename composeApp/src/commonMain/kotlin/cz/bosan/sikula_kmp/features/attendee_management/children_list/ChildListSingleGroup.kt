package cz.bosan.sikula_kmp.features.attendee_management.children_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.CategoryTag
import cz.bosan.sikula_kmp.core.presentation.getScreenWidth
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.children_manager.presentation.getChildRoleIcon
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail
import kotlin.math.max
import androidx.compose.material3.Icon
import cz.bosan.sikula_kmp.managers.children_manager.presentation.getChildRoleName

@Composable
fun ChildListSingleGroup(
    children: List<Child>,
    crews: List<Crew>,
    onChildClick: (Child) -> Unit,
    modifier: Modifier = Modifier,
    trailCategory: List<TrailCategory>,
    rowScrollState: LazyListState = rememberLazyListState(),
) {
    val screenWidth = getScreenWidth()
    val crewWidth = (screenWidth * 0.95f) / max(1, crews.size)

    LazyRow(
        modifier = modifier.padding(top = 8.dp),
        state = rowScrollState,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        itemsIndexed(crews, key = { _, crew -> crew.id }) { index, crew ->
            val childrenInCrew = children.filter { it.crewId == crew.id }
                .sortedBy { it.role }
                .sortedBy { !it.isActive }
            Row(
                modifier = Modifier.width(crewWidth),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = crew.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(childrenInCrew, key = { it.id }) { child ->
                            ChildListSingleGroupItem(
                                child = child,
                                modifier = Modifier
                                    .width(crewWidth)
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp),
                                onClick = { onChildClick(child) },
                                trailCategory = trailCategory,
                            )
                        }
                    }
                }
                if (index < crews.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight().padding(bottom = 50.dp)
                            .width(1.5.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    }
}

@Composable
fun ChildListSingleGroupItem(
    child: Child,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailCategory: List<TrailCategory>,
) {
    val itemAlpha = if (child.isActive) 1f else 0.6f

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .clickable(onClick = onClick)
            .height(47.dp)
            .graphicsLayer(alpha = itemAlpha),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (child.isActive) 2.dp else 0.dp
    ) {
        Row(
            modifier = modifier
                .padding(start = 5.dp, end = 0.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Icon(
                painter = getChildRoleIcon(child.role),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = getChildRoleName(child.role),
                modifier = Modifier.size(33.dp).padding(horizontal = 2.dp),
            )
            Column(
                modifier = modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = child.nickName,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            trailCategory.find { it.id == child.trailCategoryId }?.let {
                CategoryTag(trailCategory = it, modifier = Modifier.offset(x = (12.dp)))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.description_item_detail),
                modifier = Modifier.size(36.dp).offset(x = (4).dp)
            )
        }
    }
}
