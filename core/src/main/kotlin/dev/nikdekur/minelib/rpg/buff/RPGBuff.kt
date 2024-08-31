@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.i18n.msg.MSGNameHolder
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.ext.CompAny
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(RPGBuffSerializer::class)
open class RPGBuff<T : Comparable<T>>(
    @Contextual val stat: RPGStat<T>,
    open var value: T
) : MSGNameHolder, Cloneable {

    override val nameMSG = stat.nameBuffMSG


    override fun toString(): String {
        return "RPGBuff(type=${stat.id}, value=$value)"
    }

    override fun clone(): RPGBuff<T> {
        return RPGBuff(stat, value)
    }

}


object RPGBuffSerializer : KSerializer<RPGBuff<*>> {
    override val descriptor = buildClassSerialDescriptor("RPGBuff") {
        element("stat", PrimitiveSerialDescriptor("stat", PrimitiveKind.STRING))
        element("value", buildClassSerialDescriptor("value"))
    }

    override fun deserialize(decoder: Decoder): RPGBuff<*> {
        val statSerializer = decoder.serializersModule.getContextual(RPGStat::class)
            ?: error("No serializer found for RPGStat")
        val stat = statSerializer.deserialize(decoder) as RPGStat<CompAny>
        println(stat)
        val valueSerializer = stat.valueSerializer()
        println(valueSerializer.descriptor.kind)
        val value = valueSerializer.deserialize(decoder)
        println(value)
        return RPGBuff(stat, value)
    }

    override fun serialize(encoder: Encoder, value: RPGBuff<*>) {
        val statSerializer = encoder.serializersModule.getContextual(RPGStat::class)
            ?: error("No serializer found for RPGStat")
        statSerializer.serialize(encoder, value.stat)
        val valueSerializer = value.stat.valueSerializer() as KSerializer<CompAny>
        val value = value.value as CompAny
        valueSerializer.serialize(encoder, value)
    }
}