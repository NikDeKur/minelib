package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ornament.i18n.withPlaceholders
import org.bukkit.command.CommandSender
import java.util.*

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <T : Comparable<T>> unsafeMerge(stat: RPGStat<T>, a: Any, b: Any): Any {
    return stat.plus(a as T, b as T)
}

fun I18nService.formatBuffsToLore(buffs: Iterable<RPGBuff<*>>, player: CommandSender): List<String> {
    val res = LinkedList<String>()
    val stats = HashMap<RPGStat<*>, Any>()
    buffs.forEach {
        stats.compute(it.stat) { _, v ->
            if (v == null) it.value
            else unsafeMerge(it.stat, it.value, v)
        }
    }

    stats.forEach { (stat, value) ->
        val message = getLangMsg(
            key = stat.nameBuff.withPlaceholders(
                "stat" to stat,
                "buff" to listOf(
                    "value", value,
                    "name", getLangMsg(key = stat.name, sender = player).text
                )
            ),
            sender = player
        )

        res.add(message.text)
    }
    return res
}