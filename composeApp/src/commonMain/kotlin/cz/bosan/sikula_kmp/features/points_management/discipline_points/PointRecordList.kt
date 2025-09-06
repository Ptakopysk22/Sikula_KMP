package cz.bosan.sikula_kmp.features.points_management.discipline_points

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.core.presentation.components.GroupCircleCrew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.unknown_crew

@Composable
fun PointRecordListDiscipline(
    records: List<PointRecord>,
    crews: List<Crew>,
    groups: List<Group>,
    onRecordClick: (Crew) -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(
            items = records,
            key = { _, record -> record.crewId }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            TeamRecordListDisciplineItem(
                record = record,
                crew = crews.find { it.id == record.crewId }
                    ?: Crew(
                        id = 0,
                        groupId = 0,
                        name = stringResource(Res.string.unknown_crew),
                        color = Color.Black
                    ),
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = { onRecordClick(it) },
                groups = groups
            )
        }
    }
}


@Composable
fun TeamRecordListDisciplineItem(
    record: PointRecord,
    crew: Crew,
    groups: List<Group>,
    onClick: (Crew) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(crew) })
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                GroupCircleCrew(
                    crew = crew,
                    groups = groups,
                   modifier = Modifier.padding(start = 4.dp)
                )
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = crew.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (record.value == null) stringResource(Res.string.empty_value) else record.value.toString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(Res.string.description_item_detail),
                        modifier = Modifier.size(36.dp).offset(x = (4).dp)
                    )
            }
        }
    }
}