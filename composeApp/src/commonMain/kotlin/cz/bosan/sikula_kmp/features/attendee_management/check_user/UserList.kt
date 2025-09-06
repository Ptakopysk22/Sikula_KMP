package cz.bosan.sikula_kmp.features.attendee_management.check_user

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
import androidx.compose.foundation.lazy.itemsIndexed
import cz.bosan.sikula_kmp.core.presentation.components.formatDate
import cz.bosan.sikula_kmp.managers.user_manager.User
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_assign_user_to_camp
import sikula_kmp.composeapp.generated.resources.description_still_a_child
import sikula_kmp.composeapp.generated.resources.without_name
import sikula_kmp.composeapp.generated.resources.without_nickname

@Composable
fun UserList(
    users: List<User>,
    onUserClick: (User) -> Unit,
    onlyChildren: Boolean,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(
            items = users,
            key = { _, record -> record.id }
        ) { index, user ->
            val isLastItem = index == users.lastIndex
            UserListItem(
                user = user,
                modifier = Modifier.widthIn(700.dp).fillMaxWidth().padding(horizontal = 20.dp)
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
                onClick = { onUserClick(user) },
                onlyChildren = onlyChildren
            )
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    onClick: () -> Unit,
    onlyChildren: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.clickable(onClick = onClick).height(47.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 2.dp).fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        )
        {
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.nickName ?: stringResource(Res.string.without_nickname),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = user.name ?: stringResource(Res.string.without_name),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatDate(user.birthDate),
                    style = MaterialTheme.typography.titleSmall,
                )
                if (!onlyChildren) {
                    Text(
                        text = user.email ?: stringResource(Res.string.description_still_a_child),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.description_assign_user_to_camp),
                modifier = Modifier.size(36.dp)
            )
        }

    }
}