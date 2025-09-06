package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.TrailCategory
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role

class ChildRepository(
    private val childDataSource: ChildDataSource,
    private val childDao: ChildDao,
    private val trailCategoryDao: TrailCategoryDao,
) {

    suspend fun getTrailCategories(
        campId: Int,
        role: Role,
    ): Result<List<TrailCategory>, DataError.Remote> {
        val trailCategoriesResult = childDataSource.getTrailCategories(campId = campId)
            .map { dto -> dto.map { it.toTrailCategory() } }.onSuccess { trailCategories ->
                if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
                    trailCategoryDao.insertOrUpdateTrailCategories(trailCategories.map {
                        it.toTrailCategoryEntity(
                            campId
                        )
                    })
                }
            }
        return trailCategoriesResult
    }

    suspend fun getTrailCategoriesLocally(campId: Int): List<TrailCategory> {
        return trailCategoryDao.getLocalTrailCategories(campId).map { it.toTrailCategory() }
    }

    suspend fun getGroupChildrenLocally(campId: Int, groupId: Int): List<Child> {
        return childDao.getLocalChildren(campId, groupId).map { it.toChild() }
    }

    suspend fun getCampsChildren(
        campId: Int,
        groupId: Int? = null,
        crewId: Int? = null,
        groupIdForSavingLocally: Int? = null,
        role: Role,
    ): Result<List<Child>, DataError.Remote> {
        val childrenResult = childDataSource.getCampsChildren(campId, groupId, crewId)
            .map { dto -> dto.map { it.toChild() } }.onSuccess { children ->
                if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER) {
                    val groupChildren = children.filter { it.groupId == groupIdForSavingLocally }
                    childDao.insertOrUpdateChildren(groupChildren.map { it.toChildEntity(campId) })
                }
            }
        return childrenResult
    }


    suspend fun getChild(campId: Int, childId: Int): Result<Child, DataError.Remote> {
        return childDataSource.getChild(campId, childId).map { it.toChild() }
    }

    suspend fun assignAttendee(
        campId: Int,
        userId: Int,
        child: Child
    ): Result<Unit, DataError.Remote> {
        return childDataSource.assignAttendee(campId, userId, child)
    }

}