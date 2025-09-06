package cz.bosan.sikula_kmp.managers.leader_manager.domain

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface LeaderRepository {
    suspend fun getCurrentLeaderRemote(mail: String): Result<Leader, DataError.Remote>
    suspend fun getCurrentCampId(): Int?
    suspend fun getCampDuration(): Int
    suspend fun setCurrentLeader(currentLeader: CurrentLeader)
    suspend fun deleteCurrentLeader()
    suspend fun getCurrentCampDay(): Int
    suspend fun getCurrentLeaderLocal(): CurrentLeader
    suspend fun getCurrentLeaderLocalFlow(): Flow<CurrentLeader?>
    suspend fun getCampsLeaders(campId: Int, role: Role, isBoatRaceMaster: Boolean = false): Result<List<Leader>, DataError.Remote>
    suspend fun getCampsLeadersLocally(campId: Int): List<Leader>
    suspend fun getLeader(leaderId: Int, campId: Int):Result<Leader, DataError.Remote>
    suspend fun assignAttendee(campId: Int, userId: Int, occupation: Occupation): Result<Unit, DataError.Remote>

}