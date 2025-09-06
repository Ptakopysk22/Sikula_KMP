package cz.bosan.sikula_kmp.features.attendee_management.attendee_list

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.graphics.graphicsLayer
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleIcon
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleName
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail

@Composable
fun LeaderList(
    leaders: List<Leader>,
    onLeaderClick: (Leader) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    groups: List<Group>,
) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(
            items = leaders.sortedBy { !it.isActive },
            key = { _, record -> record.id }
        ) { index, leader ->
            val isLastItem = index == leaders.lastIndex
            LeaderListItem(
                leader = leader,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = { onLeaderClick(leader) },
                groups = groups,
            )
        }
    }
}

@Composable
fun LeaderListItem(
    leader: Leader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    groups: List<Group>,
) {
    val itemAlpha = if (leader.isActive) 1f else 0.6f

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .clickable(onClick = onClick)
            .height(47.dp)
            .graphicsLayer(alpha = itemAlpha),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (leader.isActive) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 2.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            Icon(
                painter = getRoleIcon(leader.role),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = getRoleName(leader.role),
                modifier = Modifier.size(33.dp)
            )
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = leader.nickName,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = leader.mail,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.description_item_detail),
                modifier = Modifier.size(36.dp)
            )
        }

    }
}