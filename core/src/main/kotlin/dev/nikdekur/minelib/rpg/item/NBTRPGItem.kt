package dev.nikdekur.minelib.rpg.item

import dev.nikdekur.minelib.ext.removeTag
import dev.nikdekur.minelib.ext.setTag
import dev.nikdekur.minelib.rpg.buff.MapActiveBuffsList
import dev.nikdekur.minelib.rpg.buff.RPGBuffData
import kotlinx.serialization.json.Json
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class NBTRPGItem(json: Json, type: Material) : ItemStack(type), RPGItem {

    override val stack: ItemStack
        get() = this

    override val buffs = object : MapActiveBuffsList() {
        override fun afterAddBuff(buff: RPGBuffData) {
            addBuff(buff)
        }

        override fun afterRemoveBuff(buff: RPGBuffData) {
            removeBuff(buff)
        }
    }

    fun addBuff(buff: RPGBuffData) {
        setTag(
            buff.id.toString(),
            ""
        )
    }

    fun removeBuff(buff: RPGBuffData) {
        removeTag(buff.id.toString())
    }

    override fun toString(): String {
        return "NBTRPGItem(stack=$stack, buffs=$buffs)"
    }
}