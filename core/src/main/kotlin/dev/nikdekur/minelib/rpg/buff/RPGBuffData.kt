
package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionsState
import dev.nikdekur.minelib.rpg.condition.MapConditionsState
import dev.nikdekur.ndkore.`interface`.Snowflake
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class RPGRawBuffData(
    val buff: RPGBuff<*>,
    val firstGivenAt: Long,
    val parameters: BuffParameters = BuffParameters(),
    val used: Long = 0
)

data class RPGBuffData<T : Comparable<T>>(
    override val id: UUID = UUID.randomUUID(),
    val buff: RPGBuff<T>,
    val firstGivenAt: Long = System.currentTimeMillis(),
    val parameters: BuffParameters = BuffParameters(),
    val used: Long = 0
) : Snowflake<UUID> {


    @Transient
    val conditionsState: ConditionsState = MapConditionsState()

    fun toRaw(): RPGRawBuffData {
        return RPGRawBuffData(buff, firstGivenAt, parameters, used)
    }
}