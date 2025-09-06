package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import org.jetbrains.compose.resources.painterResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.ghost

@Composable
fun ProfilePicture(currentLeader: CurrentLeader, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AsyncImage(
            currentLeader.imageUrl,
            modifier = Modifier
                .size(37.dp)
                .clip(CircleShape),
            contentDescription = null,
            placeholder = painterResource(Res.drawable.ghost),
            error = painterResource(Res.drawable.ghost),
        )
    }

}