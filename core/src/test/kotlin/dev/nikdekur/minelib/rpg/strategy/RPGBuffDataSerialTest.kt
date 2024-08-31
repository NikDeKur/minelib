package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.inventory.InventorySlot
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.RPGService
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.buff.RPGBuffData
import dev.nikdekur.minelib.rpg.buff.buffParameters
import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.HoldInHandCondition
import dev.nikdekur.minelib.rpg.stat.RPGDamageStat
import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGRegenStat
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.service.Service
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.event.Event
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass


fun main() {
    val data1 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGDamageStat, 0.5),
        buffParameters {},
        System.currentTimeMillis()
    )

    val data2 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGHealthStat, 10.0),
        buffParameters {},
        System.currentTimeMillis()
    )

    val data3 = RPGBuffData(
        UUID.randomUUID(),
        RPGBuff(RPGRegenStat, BigInteger.TEN),
        buffParameters {},
        System.currentTimeMillis()
    )

    val json = Json {
        isLenient = true

        serializersModule = SerializersModule {
            contextual(RPGStat::class, RPGStat.Serializer(
                object : RPGService {
                    override fun registerStat(stat: RPGStat<*>) {
                        TODO("Not yet implemented")
                    }

                    override fun getStat(id: String): RPGStat<*>? {
                        return when (id) {
                            RPGDamageStat.id -> RPGDamageStat
                            RPGHealthStat.id -> RPGHealthStat
                            RPGRegenStat.id -> RPGRegenStat
                            else -> null
                        }
                    }

                    override fun registerCondition(clazz: Class<out Condition<*>>, id: String) {
                        TODO("Not yet implemented")
                    }

                    override fun getCondition(id: String): Class<out Condition<*>>? {
                        return null
                    }


                    override val bindClass: KClass<out Service<*>>
                        get() = TODO("Not yet implemented")
                    override val app: ServerPlugin
                        get() = TODO("Not yet implemented")

                }
            ))

            contextual(Event::class, object : KSerializer<Event> {
                override val descriptor = String.serializer().descriptor

                override fun serialize(encoder: Encoder, value: Event) {
                    throw NotImplementedError()
                }

                override fun deserialize(decoder: Decoder): Event {
                    throw NotImplementedError()
                }
            })
        }
    }

    val condition = HoldInHandCondition(InventorySlot.HAND)
    val serializedCondition = json.encodeToString(condition)
    println(serializedCondition)
    val deserializedCondition = json.decodeFromString<Condition<@Contextual Event>>(serializedCondition)
    println(deserializedCondition)
    println(condition == deserializedCondition)

    val serialized1 = json.encodeToString(data1)
    println(serialized1)
    val deserialized1 = json.decodeFromString<RPGBuffData>(serialized1)
    println(deserialized1)
    println(RPGDamageStat == deserialized1.buff.stat)

    val serialized2 = json.encodeToString(data2)
    println(serialized2)
    val deserialized2 = json.decodeFromString<RPGBuffData>(serialized2)
    println(deserialized2)
    println(RPGHealthStat == deserialized2.buff.stat)

    val serialized3 = json.encodeToString(data3)
    println(serialized3)
    val deserialized3 = json.decodeFromString<RPGBuffData>(serialized3)
    println(deserialized3)
    println(RPGRegenStat == deserialized3.buff.stat)
}