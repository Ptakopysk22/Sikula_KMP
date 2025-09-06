package cz.bosan.sikula_kmp.features.points_management.crew_discipline_points

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation_dot
import sikula_kmp.composeapp.generated.resources.empty_value

@Composable
fun PointRecordListCrewDiscipline(
    records: List<PointRecord>,
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
            key = { _, record -> record.campDay }
        ) { index, record ->
            val isLastItem = index == records.lastIndex
            PointRecordListCrewDisciplineItem(
                record = record,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
            )
        }
    }
}

@Composable
fun PointRecordListCrewDisciplineItem(
    record: PointRecord,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(47.dp).zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text(
                    text = stringResource(Res.string.camp_day_format_abbreviation_dot, record.campDay),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (record.value == null) stringResource(Res.string.empty_value) else record.value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
    }
}