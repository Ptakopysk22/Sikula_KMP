package cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager

import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader

data class Consumer(
    val consumerId: Int,
    val tag: String?,
    val credit: Double
) {
    companion object {
        val EMPTY = Consumer(
            consumerId = 0,
            tag = null,
            credit = 0.0
        )
    }
}

data class ConsumerLeader(
    val consumer: Consumer,
    val leader: Leader,
) {
    companion object {
        val EMPTY = ConsumerLeader(
            consumer = Consumer.EMPTY,
            leader = Leader.EMPTY
        )
    }
}


