package cz.bosan.sikula_kmp.features.about_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.cpu
import sikula_kmp.composeapp.generated.resources.database
import sikula_kmp.composeapp.generated.resources.device_mobile
import sikula_kmp.composeapp.generated.resources.flask
import sikula_kmp.composeapp.generated.resources.globe
import sikula_kmp.composeapp.generated.resources.layout
import sikula_kmp.composeapp.generated.resources.paint_brush
import sikula_kmp.composeapp.generated.resources.presentation_chart

data class Developer(
    val name: String,
    val nickName: String,
    val developerRole: DeveloperRole,
    val description: String
)

@Composable
fun getDeveloperIcon(developerRole: DeveloperRole): Painter {
    return when (developerRole) {
        DeveloperRole.PROJECT_MANAGER -> Res.drawable.cpu
        DeveloperRole.BACKEND_DEVELOPER -> Res.drawable.database
        DeveloperRole.UI_UX_DESIGNER -> Res.drawable.layout
        DeveloperRole.FRONT_END_SENIOR -> Res.drawable.device_mobile
        DeveloperRole.TESTER -> Res.drawable.flask
        DeveloperRole.WEB_SUPPORT -> Res.drawable.globe
        DeveloperRole.GRAPHIC -> Res.drawable.paint_brush
        DeveloperRole.REPORTING -> Res.drawable.presentation_chart
    }.let { painterResource(it) }
}

enum class DeveloperRole{
    PROJECT_MANAGER,
    BACKEND_DEVELOPER,
    UI_UX_DESIGNER,
    FRONT_END_SENIOR,
    TESTER,
    WEB_SUPPORT,
    GRAPHIC,
    REPORTING
}