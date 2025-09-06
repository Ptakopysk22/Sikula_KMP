package cz.bosan.sikula_kmp.managers.leader_manager.domain

import cz.bosan.sikula_kmp.managers.camp_manager.domain.Camp
import cz.bosan.sikula_kmp.managers.server_manager.TokenInfo

data class CurrentLeader(
    val leader: Leader,
    val camp: Camp,
    val imageUrl: String?,
    val tokenInfo: TokenInfo,
) {
    companion object {
        val EMPTY = CurrentLeader(
            camp = Camp.EMPTY,
            leader = Leader.EMPTY,
            imageUrl = "",
            tokenInfo = TokenInfo.EMPTY
        )
    }

}