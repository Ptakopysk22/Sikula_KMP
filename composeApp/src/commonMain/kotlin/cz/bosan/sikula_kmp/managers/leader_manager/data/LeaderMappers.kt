package cz.bosan.sikula_kmp.managers.leader_manager.data

import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Occupation
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role

fun LeaderDto.toLeader(): Leader {
    return Leader(
        id = id,
        name = name,
        nickName = nickName,
        mail = mail,
        birthDate = birthDate,
        role = Role.NO_ROLE,
        isActive = false,
        groupId = null,
        bankAccount = bankAccount,
        occupations = occupations.map {
            Occupation(
                campId = it.campId,
                role = it.role,
                isActive = it.isActive,
                groupId = it.groupId,
                positions = it.positionIds
            )
        }
    )
}

fun LeaderDto.toLeaderSingleOccupation(): Leader {
    return Leader(
        id = id,
        name = name,
        nickName = nickName,
        mail = mail,
        birthDate = birthDate,
        isActive = occupations[0].isActive,
        groupId = occupations[0].groupId,
        bankAccount = bankAccount,
        occupations = occupations.map {
            Occupation(
                campId = it.campId,
                role = it.role,
                isActive = it.isActive,
                groupId = it.groupId,
                positions = it.positionIds
            )
        },
        role = occupations[0].role,
        positions = occupations[0].positionIds
    )
}

fun LeaderEntity.toLeader(): Leader {
    return Leader(
        id = id,
        name = name ?: "",
        nickName = nickName ?: "",
        mail = "",
        role = role,
        positions = emptyList(),
        birthDate = null,
        isActive = true,
        groupId = groupId,
        bankAccount = null,
        occupations = emptyList()
    )
}

fun Leader.toLeaderEntity(campId: Int): LeaderEntity {
    return LeaderEntity(
        id = id,
        name = name,
        nickName = nickName,
        role = role,
        groupId = groupId,
        campId = campId,
    )
}