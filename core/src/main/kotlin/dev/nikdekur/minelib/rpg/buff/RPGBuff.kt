@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.i18n.msg.MSGNameHolder
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.`interface`.Snowflake
import java.util.*

open class RPGBuff<T : Comparable<T>>(
    val stat: RPGStat<T>,
    open var value: T
) : Snowflake<UUID>, MSGNameHolder {
    override val id: UUID = UUID.randomUUID()

    override val nameMSG = stat.nameBuffMSG

    override fun toString(): String {
        return "RPGBuff(type=${stat.id}, value=$value)"
    }

    inline fun copy(): RPGBuff<T> {
        return RPGBuff(stat, value)
    }

    inline fun mergeValue(value: T): T {
        return stat.plus(this.value, value)
    }
}