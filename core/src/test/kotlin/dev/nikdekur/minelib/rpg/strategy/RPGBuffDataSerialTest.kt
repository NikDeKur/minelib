package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.inventory.InventorySlot
import dev.nikdekur.minelib.plugin.TestServerPlugin
import dev.nikdekur.minelib.rpg.RPGService
import dev.nikdekur.minelib.rpg.RuntimeRPGService
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.buff.RPGBuffData
import dev.nikdekur.minelib.rpg.buff.RPGRawBuffData
import dev.nikdekur.minelib.rpg.buff.buffParameters
import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.HoldInSlotCondition
import dev.nikdekur.minelib.rpg.stat.RPGDamageStat
import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGRegenStat
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.service.get
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.math.BigInteger
import java.util.UUID

object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger(decoder.decodeString())
    }
}

fun main() {
    val data1 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGDamageStat, 0.5),
        System.currentTimeMillis()
    )

    val data2 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGHealthStat, 10.0),
        System.currentTimeMillis()
    )



    val data3 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGRegenStat, BigInteger.TEN),
        System.currentTimeMillis(),
        buffParameters {
            condition(HoldInSlotCondition(InventorySlot.MAIN_HAND))
        }
    )

    val plugin = TestServerPlugin()
    plugin.servicesManager.registerService(RuntimeRPGService(plugin), RPGService::class)

    val serialModule = SerializersModule {
        polymorphic(Condition::class) {
            subclass(HoldInSlotCondition::class)
        }

        polymorphic(Any::class) {
            subclass(HoldInSlotCondition::class)
        }

        contextual(RPGStat::class, RPGStat.Serializer(plugin.servicesManager.get()))
    }

    val json = Json {
        isLenient = true
        serializersModule = serialModule
    }

    println("----------------------------------------")

    val condition = HoldInSlotCondition(InventorySlot.MAIN_HAND)
    val serializedCondition = json.encodeToString(PolymorphicSerializer(Any::class), condition)
    println(serializedCondition)
    val deserializedCondition = json.decodeFromString(PolymorphicSerializer(Any::class),  serializedCondition)
    println(deserializedCondition)
    println(condition == deserializedCondition)

    println("----------------------------------------")

    val serialized1 = json.encodeToString(data1.toRaw())
    println(serialized1)
    val deserialized1 = json.decodeFromString<RPGRawBuffData>(serialized1)
    println(deserialized1)
    println(RPGDamageStat == deserialized1.buff.stat)

    println("----------------------------------------")

    val serialized2 = json.encodeToString(data2.toRaw())
    println(serialized2)
    val deserialized2 = json.decodeFromString<RPGRawBuffData>(serialized2)
    println(deserialized2)
    println(RPGHealthStat == deserialized2.buff.stat)

    println("----------------------------------------")

    val serialized3 = json.encodeToString(data3.toRaw())
    println(serialized3)
    val deserialized3 = json.decodeFromString<RPGRawBuffData>(serialized3)
    println(deserialized3)
    println(RPGRegenStat == deserialized3.buff.stat)

    println("----------------------------------------")
}