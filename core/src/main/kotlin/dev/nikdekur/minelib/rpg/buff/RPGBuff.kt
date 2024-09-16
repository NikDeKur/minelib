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
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

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
        element("stat", buildClassSerialDescriptor("stat"))
        element("value", buildClassSerialDescriptor("value"))
    }

    override fun serialize(encoder: Encoder, value: RPGBuff<*>) {
        val statSerializer = encoder.serializersModule.getContextual(RPGStat::class)
            ?: error("No serializer found for RPGStat")

        @Suppress("kotlin:S6530", "UNCHECKED_CAST")
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, statSerializer, value.stat)
            val valueSerializer = value.stat.valueSerializer() as KSerializer<CompAny>
            val value = value.value as CompAny
            encodeSerializableElement(descriptor, 1, valueSerializer, value)
        }
    }





    override fun deserialize(decoder: Decoder): RPGBuff<*> {
        val statSerializer = decoder.serializersModule.getContextual(RPGStat::class)
            ?: error("No serializer found for RPGStat")

        return decoder.decodeStructure(descriptor) {
            var stat: RPGStat<CompAny>? = null
            var value: CompAny? = null

            @Suppress("kotlin:S6530", "UNCHECKED_CAST")
            @OptIn(ExperimentalSerializationApi::class)
            if (decodeSequentially()) { // sequential decoding protocol
                stat = decodeSerializableElement(descriptor, 0, statSerializer) as RPGStat<CompAny>
                value = decodeSerializableElement(descriptor, 1, stat.valueSerializer())
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> stat = decodeSerializableElement(descriptor, 0, statSerializer) as RPGStat<CompAny>
                    1 -> value = decodeSerializableElement(descriptor, 1, stat!!.valueSerializer())
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            RPGBuff(stat!!, value!!)
        }
    }
}