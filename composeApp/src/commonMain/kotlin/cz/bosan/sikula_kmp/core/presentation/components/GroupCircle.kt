package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child

@Composable
fun GroupCircle(
    child: Child,
    groups: List<Group>,
    crews: List<Crew>,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(33.dp)
            .border(
                2.dp,
                groups.find { it.id == child.groupId }?.color ?: Color.Black,
                CircleShape
            )
            .padding(2.dp)
    ) {
        Text(
            text = groups.find { it.id == child.groupId }?.name ?: "",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun GroupCircleCrew(
    crew: Crew,
    groups: List<Group>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(33.dp)
            .border(
                2.dp,
                groups.find { it.id == crew.groupId }?.color ?: Color.Black,
                CircleShape
            )
            .padding(2.dp)
    ) {
        Text(
            text = groups.find { it.id == crew.groupId }?.name ?: "",
            style = MaterialTheme.typography.titleSmall
        )
    }
}