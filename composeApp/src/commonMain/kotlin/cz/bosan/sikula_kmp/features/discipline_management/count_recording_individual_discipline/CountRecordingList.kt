package cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.SwipeToDismissContainer
import cz.bosan.sikula_kmp.features.discipline_management.components.RecordingElement
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.done

@Composable
fun CountRecodingList(
    discipline: Discipline,
    children: List<Child>,
    scrollState: LazyListState = rememberLazyListState(),
    onFillRecord: (String?, Child, String) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier.padding(vertical = 4.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 350.dp)
    ) {

        itemsIndexed(
            items = children,
            key = { _, record -> record.id }
        ) { index, child ->
            val isLastItem = index == children.lastIndex
            SwipeToDismissContainer(
                item = child,
                itemName = child.nickName,
                onDismiss = { childToDismiss, comment, onError ->
                    onFillRecord(null, childToDismiss, comment)
                }
            ) { childItem ->
                CountRecodingListItem(
                    child = childItem,
                    discipline = discipline,
                    modifier = Modifier
                        .widthIn(700.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                    onFillRecord = { onFillRecord(it, childItem, "") },
                    isNextRecord = index == 0,
                )
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PrimaryButton(
                    enabled = children.isEmpty(),
                    onClick = onDoneClick,
                    content = {
                        Text(stringResource(Res.string.done), style = MaterialTheme.typography.titleSmall)
                    },
                )
            }
        }
    }
}

@Composable
fun CountRecodingListItem(
    discipline: Discipline,
    child: Child,
    modifier: Modifier = Modifier,
    onFillRecord: (String?) -> Unit,
    isNextRecord: Boolean,
) {

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
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
                text = child.nickName,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            RecordingElement(
                discipline = discipline,
                clickedItemName = null,
                onValueChange = { onFillRecord(it) },
                hideKeyboardAfterCheck = false,
                isNextRecord = isNextRecord,
            )
        }
    }
}