package cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager

import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role

fun ConsumerLeaderDto.toConsumerLeader(campId: Int): ConsumerLeader {
    return ConsumerLeader(
        consumer = Consumer(
            consumerId = consumerId,
            tag = tag,
            credit = credit ?: 0.0
        ),
        leader = Leader(
            id = userId,
            name = name,
            nickName = nickName,
            mail = mail,
            role = Role.NO_ROLE,
            positions = emptyList(),
            birthDate = birthDate,
            isActive = occupations.find { it.campId == campId }?.isActive?: true,
            groupId = null,
            bankAccount = bankAccount,
            occupations = emptyList()
        )
    )
}