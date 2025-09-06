package cz.bosan.sikula_kmp.managers.children_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.crew_master
import sikula_kmp.composeapp.generated.resources.crew_member
import sikula_kmp.composeapp.generated.resources.deputy_crew_master
import sikula_kmp.composeapp.generated.resources.ghost
import sikula_kmp.composeapp.generated.resources.role_crew_master
import sikula_kmp.composeapp.generated.resources.role_crew_member
import sikula_kmp.composeapp.generated.resources.role_deputy_crew_master
import sikula_kmp.composeapp.generated.resources.role_no_role

@Composable
fun getChildRoleName(role: ChildRole?): String {
    return when (role) {
        ChildRole.MEMBER -> Res.string.role_crew_member
        ChildRole.CREW_MASTER -> Res.string.role_crew_master
        ChildRole.DEPUTY_CREW_MASTER -> Res.string.role_deputy_crew_master
        else -> Res.string.role_no_role
    }.let { stringResource(it) }
}

@Composable
fun getChildRoleIcon(role: ChildRole?): Painter {
    return when (role) {
        ChildRole.MEMBER -> Res.drawable.crew_member
        ChildRole.CREW_MASTER -> Res.drawable.crew_master
        ChildRole.DEPUTY_CREW_MASTER -> Res.drawable.deputy_crew_master
        else -> Res.drawable.ghost
    }.let { painterResource(it) }
}