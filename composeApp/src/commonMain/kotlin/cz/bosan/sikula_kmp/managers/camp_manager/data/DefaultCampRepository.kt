package cz.bosan.sikula_kmp.managers.camp_manager.data

import cz.bosan.sikula_kmp.managers.camp_manager.domain.Camp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Group
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew

class DefaultCampRepository(
    private val campDataSource: CampDataSource,
    private val crewDao: CrewDao,
) : CampRepository {
    override suspend fun getCamps(): Result<List<Camp>, DataError.Remote> {
        return campDataSource.getCamps().map { dto -> dto.map { it.toCamp() } }
    }

    override suspend fun getGroups(campId: Int): Result<List<Group>, DataError.Remote> {
        return campDataSource.getGroups(campId).map { dto -> dto.map { it.toGroup() } }
    }

    override suspend fun getCrews(
        campId: Int,
        groupId: Int?,
        isBoatRaceMaster: Boolean,
    ): Result<List<Crew>, DataError.Remote> {
        val crews = campDataSource.getGroupCrews(campId, groupId)
            .map { dto -> dto.map { it.toCrew() } }.onSuccess { crewsResult ->
                if (isBoatRaceMaster && campId != null) {
                    crewDao.insertOrUpdateCrews(crewsResult.map { it.toCrewEntity(campId) })
                }
            }
        return crews
    }

    override suspend fun getCrewsLocal(
        campId: Int,
    ): Result<List<Crew>, DataError.Local> {
        return try {
            val crews = crewDao.getLocalCrews(campId).map { dto -> dto.toCrew() }
            Result.Success(crews)
        /*} catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)*/
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }


}