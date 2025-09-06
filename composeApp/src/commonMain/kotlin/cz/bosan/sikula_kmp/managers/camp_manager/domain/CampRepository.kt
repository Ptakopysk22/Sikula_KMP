package cz.bosan.sikula_kmp.managers.camp_manager.domain

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result

interface CampRepository {
    suspend fun getCamps(): Result<List<Camp>, DataError.Remote>
    suspend fun getGroups(campId: Int): Result<List<Group>, DataError.Remote>
    suspend fun getCrews(campId: Int, groupId: Int?, isBoatRaceMaster: Boolean = false): Result<List<Crew>, DataError.Remote>
    suspend fun getCrewsLocal(campId: Int): Result<List<Crew>, DataError.Local>
}