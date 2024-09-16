package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.item.RPGItem
import dev.nikdekur.minelib.rpg.stat.RPGStat
import org.bukkit.inventory.ItemStack

interface RPGService {

    fun registerStat(stat: RPGStat<*>)

    fun getStat(id: String): RPGStat<*>?

    fun registerCondition(clazz: Class<out Condition<*>>)

    fun getItem(stack: ItemStack): RPGItem
}


