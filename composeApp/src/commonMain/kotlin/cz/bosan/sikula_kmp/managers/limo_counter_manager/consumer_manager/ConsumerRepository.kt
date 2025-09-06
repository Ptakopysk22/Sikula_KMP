package cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader

class ConsumerRepository(
    private val consumerDataSource: ConsumerDataSource
) {

    suspend fun getConsumers(
        campId: Int,
    ): Result<List<ConsumerLeader>, DataError.Remote> {
        return consumerDataSource.getConsumers(campId)
            .map { dto -> dto.map { it.toConsumerLeader(campId) } }
    }

    suspend fun getConsumerLeader(
        campId: Int,
        consumerId: Int
    ): Result<ConsumerLeader, DataError.Remote> {
        return consumerDataSource.getConsumer(campId, consumerId).map { it.toConsumerLeader(campId) }
    }

    suspend fun assignConsumer(campId: Int, leader: Leader): Result<Unit, DataError.Remote> {
        return consumerDataSource.assignConsumer(
            campId = campId,
            userId = leader.id
        )
    }

    suspend fun updateConsumerTag(
        campId: Int,
        consumerId: Int,
        tag: String?
    ): Result<Unit, DataError.Remote> {
        return consumerDataSource.updateConsumerTag(
            campId = campId,
            userId = consumerId,
            tag = tag
        )
    }
}