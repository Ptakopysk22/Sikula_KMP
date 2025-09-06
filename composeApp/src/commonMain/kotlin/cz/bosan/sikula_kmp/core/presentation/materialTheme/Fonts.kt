package cz.bosan.sikula_kmp.core.presentation.materialTheme

import sikula_kmp.composeapp.generated.resources.Outfit_Bold
import sikula_kmp.composeapp.generated.resources.Outfit_Light
import sikula_kmp.composeapp.generated.resources.Outfit_Medium
import sikula_kmp.composeapp.generated.resources.Outfit_Regular
import sikula_kmp.composeapp.generated.resources.Res
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

@Composable
fun OutfitFontFamily() = FontFamily(
    Font(Res.font.Outfit_Light, weight = FontWeight.Light),
    Font(Res.font.Outfit_Regular, weight = FontWeight.Normal),
    Font(Res.font.Outfit_Medium, weight = FontWeight.Medium),
    Font(Res.font.Outfit_Bold, weight = FontWeight.Bold),
)

@Composable
fun OutfitTypography() = Typography().run {

    val fontFamily = OutfitFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily, fontSize = 28.sp, fontWeight = FontWeight.Medium),
        titleMedium = titleMedium.copy(fontFamily = fontFamily, fontSize = 20.sp, fontWeight = FontWeight.Normal),
        titleSmall = titleSmall.copy(fontFamily = fontFamily, fontSize = 15.sp, fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily, fontSize = 13.sp, fontWeight = FontWeight.Normal),
        labelMedium = labelMedium.copy(fontFamily = fontFamily, fontSize = 13.sp, fontWeight = FontWeight.Light),
        labelSmall = labelSmall.copy(fontFamily = fontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    )
}