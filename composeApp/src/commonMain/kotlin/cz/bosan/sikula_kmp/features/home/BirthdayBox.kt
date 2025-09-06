package cz.bosan.sikula_kmp.features.home

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
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.ContainerBox
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.user_manager.BirthdayUser
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.birthday_celebration
import sikula_kmp.composeapp.generated.resources.camp_day_format_abbreviation_dot
import sikula_kmp.composeapp.generated.resources.unknown_birthday_user

@Composable
fun BirthdayBox(
    birthdayUsers: List<BirthdayUser>,
    errorMessage: UiText?,
    modifier: Modifier = Modifier
) {
    ContainerBox(
        modifier = modifier,
        title = stringResource(Res.string.birthday_celebration),
        content = {
            if (errorMessage != null) {
                Message(
                    text = errorMessage.asString(),
                    messageTyp = MessageTyp.ERROR
                )
            } else {
                if (birthdayUsers.isEmpty()) {
                    Message(
                        text = Warning.Common.EMPTY_LIST.toUiText().asString(),
                        messageTyp = MessageTyp.WARNING
                    )
                } else {
                    BirthDayUserList(
                        birthdayUsers = birthdayUsers,
                    )
                }
            }
        })
}

@Composable
fun BirthDayUserList(
    birthdayUsers: List<BirthdayUser>,
    scrollState: LazyListState = rememberLazyListState(),
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
            items = birthdayUsers,
            key = { _, record -> record.user.id }
        ) { index, birthdayUser ->
            val isLastItem = index == birthdayUsers.lastIndex
            BirthDayUserListItem(
                birthdayUser = birthdayUser,
                modifier = Modifier
                    .widthIn(700.dp)
                    .fillMaxWidth()
                    .then(if (isLastItem) Modifier.padding(bottom = 16.dp) else Modifier),
            )
        }
    }
}

@Composable
fun BirthDayUserListItem(
    birthdayUser: BirthdayUser,
    modifier: Modifier = Modifier,
) {

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.height(47.dp),
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
                text = stringResource(
                    Res.string.camp_day_format_abbreviation_dot,
                    birthdayUser.campDay
                ),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = birthdayUser.user.nickName
                    ?: stringResource(Res.string.unknown_birthday_user),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}