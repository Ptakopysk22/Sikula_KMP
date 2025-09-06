package cz.bosan.sikula_kmp.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.presentation.components.ContainerBox
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.features.discipline_management.components.DayStateCircleIcon
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DisciplineState
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.your_tasks

@Composable
fun RemainderListBox(
    disciplineStates: Map<DisciplineState, Int>,
    onClick: (Discipline, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerBox(
        modifier = modifier,
        title = stringResource(Res.string.your_tasks),
        content = {
            if (disciplineStates.isEmpty()) {
                Message(
                    text = Warning.Common.EMPTY_LIST.toUiText().asString(),
                    messageTyp = MessageTyp.WARNING
                )
            } else {
                RemainderList(
                    disciplineStates = disciplineStates,
                    onClick = onClick,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )
}

@Composable
fun RemainderList(
    disciplineStates: Map<DisciplineState, Int>,
    scrollState: LazyListState = rememberLazyListState(),
    onClick: (Discipline, Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(top = 4.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {

        itemsIndexed(
            items = disciplineStates.toList(),
            key = { _, record -> record.first.discipline.id }
        ) { index, (disciplineState, campDay) ->
            val isLastItem = index == disciplineStates.size - 1
            RemainderListItem(
                disciplineState = disciplineState,
                onClick = { onClick(disciplineState.discipline, campDay) },
                modifier = Modifier
                    .widthIn(700.dp)
                    .fillMaxWidth()
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
            )
        }
    }
}

@Composable
fun RemainderListItem(
    disciplineState: DisciplineState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.clickable(onClick = onClick)
            .height(47.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
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
            Text(
                text = getDisciplineName(disciplineState.discipline),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            DayStateCircleIcon(
                dayRecordsState = disciplineState.dayRecordsState!!,
                isGray = true,
            )
        }
    }
}