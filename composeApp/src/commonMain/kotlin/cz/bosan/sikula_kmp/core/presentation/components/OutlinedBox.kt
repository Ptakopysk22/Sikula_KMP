package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun OutlinedBox(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 3.dp).background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 9.dp, start = 7.dp, bottom = 5.dp, end = 7.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium
                ).background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
        TitleBox(title = title, modifier = Modifier.align(Alignment.TopStart).zIndex(1f))
    }
}

@Composable
fun TitleBox(title: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 19.dp).height(21.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .height(9.dp)
            ) { Text(text = title, color = Color.Transparent) }
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .height(12.dp)
            ) { Text(text = title, color = Color.Transparent) }
        }
        Text(
            text = title,
            color = Color.Black.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier.padding(vertical = 1.dp).offset(y = (-2).dp, x = 4.dp)
        )
    }
}