package dev.nikdekur.minelib.rpg.item

import dev.nikdekur.minelib.rpg.buff.ActiveBuffsList
import org.bukkit.inventory.ItemStack

interface RPGItem {

    val stack: ItemStack

    val buffs: ActiveBuffsList
}