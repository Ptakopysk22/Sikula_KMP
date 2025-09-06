package cz.bosan.sikula_kmp.managers.leader_manager.data

import cz.bosan.sikula_kmp.core.data.TokenHolder
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Camp
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Occupation
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.server_manager.TokenInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import kotlin.math.*

class DefaultLeaderRepository(
    private val leaderDataSource: LeaderDataSource,
    private val currentLeaderDao: CurrentLeaderDao,
    private val leaderDao: LeaderDao,
) : LeaderRepository {

    override suspend fun getCurrentLeaderRemote(mail: String): Result<Leader, DataError.Remote> {
        return leaderDataSource
            .getCurrentLeaderRemote(mail = mail)
            .map { dto -> dto.toLeader() }
    }

    override suspend fun getCurrentCampId(): Int? {
        return currentLeaderDao.getCurrentCampId()
    }

    override suspend fun getCampDuration(): Int {
        return round(currentLeaderDao.getCampDuration()).toInt() + 1
    }

    override suspend fun setCurrentLeader(currentLeader: CurrentLeader) {
        currentLeaderDao.setCurrentLeadersCamp(
            CurrentLeaderEntity(
                campId = currentLeader.camp.id,
                campStartDate = currentLeader.camp.startDate,
                campsEndDate = currentLeader.camp.endDate,
                campName = currentLeader.camp.name,
                leaderId = currentLeader.leader.id,
                name = currentLeader.leader.name,
                nickName = currentLeader.leader.nickName,
                email = currentLeader.leader.mail,
                backendToken = currentLeader.tokenInfo.backendToken,
                backendTokenExpiration = currentLeader.tokenInfo.backendTokenExpiration,
                refreshToken = currentLeader.tokenInfo.refreshToken,
                refreshTokenExpiration = currentLeader.tokenInfo.refreshTokenExpiration,
                groupId = currentLeader.leader.groupId
                    ?: currentLeader.leader.occupations.find { it.campId == currentLeader.camp.id }?.groupId,
                role = if (currentLeader.leader.role == Role.NO_ROLE) {
                    currentLeader.leader.occupations.find { it.campId == currentLeader.camp.id }?.role
                        ?: Role.NO_ROLE
                } else {
                    currentLeader.leader.role
                },
                isActive = if (!currentLeader.leader.isActive) {
                    currentLeader.leader.occupations.find { it.campId == currentLeader.camp.id }?.isActive
                        ?: false
                } else {
                    currentLeader.leader.isActive
                },
                positions = currentLeader.leader.positions.ifEmpty {
                    currentLeader.leader.occupations.find { it.campId == currentLeader.camp.id }?.positions.orEmpty()
                },
                imageUrl = currentLeader.imageUrl,
                bankAccount = currentLeader.leader.bankAccount
            )
        )
    }

    override suspend fun deleteCurrentLeader() {
        TokenHolder.clear()
        currentLeaderDao.deleteCurrentLeadersCamp()
    }

    override suspend fun getCurrentCampDay(): Int {
        val entity = currentLeaderDao.getCurrentLeaderEntity() ?: return 1
        val campDuration = getCampDuration()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        return when {
            today < entity.campStartDate -> 1
            today > entity.campsEndDate -> campDuration
            else -> entity.campStartDate.daysUntil(today) + 1
        }
    }

    override suspend fun getCurrentLeaderLocal(): CurrentLeader {
        val entity = currentLeaderDao.getCurrentLeaderEntity()
        if (entity == null) {
            return CurrentLeader.EMPTY
        } else {
            return CurrentLeader(
                leader = Leader(
                    id = entity.leaderId ?: error("Leader ID cannot be null"),
                    name = entity.name ?: "",
                    nickName = entity.nickName ?: "",
                    mail = entity.email ?: "",
                    role = entity.role,
                    positions = entity.positions,
                    birthDate = null,
                    isActive = entity.isActive,
                    groupId = entity.groupId,
                    bankAccount = entity.bankAccount,
                    occupations = emptyList()
                ),
                camp = Camp(
                    id = entity.campId ?: error("Camp ID cannot be null"),
                    startDate = entity.campStartDate,
                    endDate = entity.campsEndDate,
                    name = entity.campName
                ),
                imageUrl = entity.imageUrl,
                tokenInfo = TokenInfo(
                    backendToken = entity.backendToken,
                    backendTokenExpiration = entity.backendTokenExpiration,
                    refreshToken = entity.refreshToken,
                    refreshTokenExpiration = entity.refreshTokenExpiration
                )
            )
        }
    }

    override suspend fun getCurrentLeaderLocalFlow(): Flow<CurrentLeader?> {
        return currentLeaderDao.getCurrentLeaderEntityFlow()
            .map { entity ->
                entity?.let {
                    CurrentLeader(
                        leader = Leader(
                            id = it.leaderId ?: error("Leader ID cannot be null"),
                            name = it.name ?: "",
                            nickName = it.nickName ?: "",
                            mail = it.email ?: "",
                            role = it.role,
                            positions = it.positions,
                            birthDate = null,
                            isActive = it.isActive,
                            groupId = it.groupId,
                            bankAccount = it.bankAccount,
                            occupations = emptyList()
                        ),
                        camp = Camp(
                            id = it.campId ?: error("Camp ID cannot be null"),
                            startDate = it.campStartDate,
                            endDate = it.campsEndDate,
                            name = it.campName
                        ),
                        imageUrl = it.imageUrl,
                        tokenInfo = TokenInfo(
                            backendToken = entity.backendToken,
                            backendTokenExpiration = entity.backendTokenExpiration,
                            refreshToken = entity.refreshToken,
                            refreshTokenExpiration = entity.refreshTokenExpiration
                        )
                    )
                }
            }
    }

    override suspend fun getCampsLeaders(
        campId: Int,
        role: Role,
        isBoatRaceMaster: Boolean,
    ): Result<List<Leader>, DataError.Remote> {
        val leadersResult = leaderDataSource.getCampsLeaders(campId = campId)
            .map { dto -> dto.map { it.toLeaderSingleOccupation() } }.onSuccess { leaders ->
                if (role == Role.HEAD_GROUP_LEADER || role == Role.CHILD_LEADER || isBoatRaceMaster) {
                    leaderDao.insertOrUpdateLeaders(leaders.map { it.toLeaderEntity(campId) })
                }
            }
        return leadersResult
    }

    override suspend fun getCampsLeadersLocally(campId: Int): List<Leader> {
        return leaderDao.getLocalLeaders(campId = campId).map { it.toLeader() }
    }

    override suspend fun getLeader(leaderId: Int, campId: Int): Result<Leader, DataError.Remote> {
        return leaderDataSource
            .getLeader(campId = campId, leaderId = leaderId)
            .map { dto -> dto.toLeaderSingleOccupation() }
    }

    override suspend fun assignAttendee(
        campId: Int,
        userId: Int,
        occupation: Occupation
    ): Result<Unit, DataError.Remote> {
        return leaderDataSource.assignAttendee(campId, userId, occupation)
    }


}