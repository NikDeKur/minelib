package dev.nikdekur.minelib.rpg.item

import dev.nikdekur.minelib.rpg.buff.MutableBuffsList
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.ndkore.`interface`.Unique
import org.bukkit.inventory.ItemStack
import java.util.UUID

interface RPGItem : Unique<UUID> {

    val stack: ItemStack

    val buffs: MutableBuffsList

    fun apply(profile: RPGProfile)
    fun unApply(profile: RPGProfile)
}