package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.stat.RPGStat
import org.bukkit.command.CommandSender
import java.util.LinkedList
import kotlin.collections.forEach

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <T : Comparable<T>> unsafeMerge(stat: RPGStat<T>, a: Any, b: Any): Any {
    return stat.plus(a as T, b as T)
}

fun formatBuffsToLore(buffs: Iterable<RPGBuff<*>>, player: CommandSender): List<String> {
    val res = LinkedList<String>()
    val stats = HashMap<RPGStat<*>, Any>()
    buffs.forEach {
        stats.compute(it.stat) { _, v ->
            if (v == null) it.value
            else unsafeMerge(it.stat, it.value, v)
        }
    }

    stats.forEach { (stat, value) ->
        val message = player.getLangMsg(
            stat.nameBuffMSG,
            "stat" to stat.getPlaceholder(player),
            "buff" to listOf(
                "value", value,
                "name", player.getLangMsg(stat.nameMSG).text
            )
        )

        res.add(message.text)
    }
    return res
}