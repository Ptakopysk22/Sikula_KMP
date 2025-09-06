package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ChildRoleSerializer : KSerializer<ChildRole> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ChildRole", PrimitiveKind.INT)

    private val roleMap = ChildRole.entries.associateBy { it.index }

    override fun serialize(encoder: Encoder, value: ChildRole) {
        encoder.encodeInt(value.index)
    }

    override fun deserialize(decoder: Decoder): ChildRole {
        val value = decoder.decodeInt()
        return roleMap[value] ?: ChildRole.MEMBER
    }
}