package cz.bosan.sikula_kmp.features.points_management.crew_points

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.pointDisciplines
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_item_detail

@Composable
fun PointRecordListCrew(
    records: List<PointRecord>,
    onRecordClick: (Discipline) -> Unit,
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
            key = { _, record -> record.disciplineId }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            TeamRecordListCrewItem(
                record = record,
                discipline = pointDisciplines.find { it.id == record.disciplineId }
                    ?: Discipline.Team.UNKNOWN_DISCIPLINE,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = { onRecordClick(it) },
            )
        }
    }
}

@Composable
fun TeamRecordListCrewItem(
    record: PointRecord,
    discipline: Discipline,
    onClick: (Discipline) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .clickable(onClick = { onClick(discipline) })
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(end = 4.dp).fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = getDisciplineName(discipline),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (record.value == null) "-" else record.value.toString(),
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