package cz.bosan.sikula_kmp.features.about_app

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.role

@Composable
fun DeveloperList(
    developers: List<Developer>,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        itemsIndexed(
            items = developers,
            key = { _, developer -> developer.name }
        ) { index, developer ->
            val isLastItem = index == developers.lastIndex
            DeveloperListItem(
                developer = developer,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth()
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
            )
        }
    }
}

@Composable
fun DeveloperListItem(
    developer: Developer,
    modifier: Modifier = Modifier,
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
            Icon(
                painter = getDeveloperIcon(developer.developerRole),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(Res.string.role),
                modifier = Modifier.size(33.dp)
            )
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${developer.name} (${developer.nickName})",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = developer.description,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

        }

    }
}