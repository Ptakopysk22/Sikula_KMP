package cz.bosan.sikula_kmp.managers.leader_manager.data

import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LeaderRoleSerializer : KSerializer<Role> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Role", PrimitiveKind.INT)

    private val roleMap = Role.entries.associateBy { it.index }

    override fun serialize(encoder: Encoder, value: Role) {
        encoder.encodeInt(value.index)
    }

    override fun deserialize(decoder: Decoder): Role {
        val value = decoder.decodeInt()
        return roleMap[value] ?: Role.NO_ROLE
    }
}

object PositionSerializer : KSerializer<Position> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Position", PrimitiveKind.INT)

    private val positionMap = Position.entries.associateBy { it.index }

    override fun serialize(encoder: Encoder, value: Position) {
        encoder.encodeInt(value.index)
    }

    override fun deserialize(decoder: Decoder): Position {
        val value = decoder.decodeInt()
        return positionMap[value] ?: Position.UNKNOWN_POSITION
    }
}

object PositionListSerializer : KSerializer<List<Position>> {
    private val listSerializer = ListSerializer(PositionSerializer)

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<Position>) {
        listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): List<Position> {
        return listSerializer.deserialize(decoder)
    }
}