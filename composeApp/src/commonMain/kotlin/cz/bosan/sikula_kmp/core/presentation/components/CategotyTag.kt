package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.tag_simple_stroke
import sikula_kmp.composeapp.generated.resources.trail_category
import sikula_kmp.composeapp.generated.resources.unknown_trail_category

@Composable
fun CategoryTag(
    trailCategory: TrailCategory,
    modifier: Modifier = Modifier,
    showBigIcon: Boolean = false
) {
    val iconSize: Dp = if (showBigIcon) 50.dp else 32.dp
    val textStyle = if(showBigIcon) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.labelLarge

    Box(modifier = modifier.padding(1.dp), contentAlignment = Alignment.Center) {
        /*Icon(
            painter = painterResource(Res.drawable.tag_simple_fill),
            tint = color,
            contentDescription = "",
            modifier = Modifier.size(iconSize)
        )*/
        Icon(
            painter = painterResource(Res.drawable.tag_simple_stroke),
            tint = trailCategory.color,
            contentDescription = stringResource(Res.string.trail_category),
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = trailCategory.name,
            style = textStyle,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

@Composable
fun CategorySurface(
    child: Child,
    groupColor: Color,
    trailCategories: List<TrailCategory>
){
    Surface(
        modifier = Modifier.width(20.dp).fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
        color = groupColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = trailCategories.find { it.id == child.trailCategoryId }?.name ?: stringResource(Res.string.unknown_trail_category),
                modifier = Modifier
                    .graphicsLayer(
                        rotationZ = -90f,
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    )
                    .wrapContentSize(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}