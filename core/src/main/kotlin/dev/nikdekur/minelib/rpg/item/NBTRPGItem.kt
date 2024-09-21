package dev.nikdekur.minelib.rpg.item

import dev.nikdekur.minelib.ext.editNBT
import dev.nikdekur.minelib.ext.removeTag
import dev.nikdekur.minelib.ext.setTag
import dev.nikdekur.minelib.rpg.buff.MapMutableBuffsList
import dev.nikdekur.minelib.rpg.buff.RPGBuffData
import dev.nikdekur.minelib.rpg.buff.RPGRawBuffData
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.ndkore.ext.toJUUIDOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.inventory.ItemStack
import java.util.UUID

class NBTRPGItem(
    override val id: UUID,
    val json: Json,
    override val stack: ItemStack
) : RPGItem {

    val attachId = "item-${id}"

    override val buffs = object : MapMutableBuffsList() {
        override fun onBuffAdd(buff: RPGBuffData<*>) {
            this@NBTRPGItem.addBuff(buff)
        }

        override fun onBuffRemove(buff: RPGBuffData<*>) {
            this@NBTRPGItem.removeBuff(buff)
        }
    }

    init {
        stack.editNBT {
            val keys = getKeys()
            keys.forEach { key ->
                val uuid = key.toJUUIDOrNull() ?: return@forEach
                val value = getString(key)
                val buff = try {
                    json.decodeFromString<RPGRawBuffData>(value)
                } catch (e: Exception) {
                    debug("Failed to load buff $key")
                    e.printStackTrace()
                    return@forEach
                }
                debug("Adding buff $buff to item $id")
                buffs.addBuff(
                    RPGBuffData(
                        id = uuid,
                        buff = buff.buff,
                        firstGivenAt = buff.firstGivenAt,
                        parameters = buff.parameters,
                        used = buff.used
                    )
                )
            }
        }
    }


    fun addBuff(buff: RPGBuffData<*>) {
        debug("Applying buff $buff to item $id")
        val serializable = RPGRawBuffData(
            buff = buff.buff,
            firstGivenAt = buff.firstGivenAt,
            parameters = buff.parameters,
            used = buff.used
        )

        stack.setTag(
            buff.id.toString(),
            json.encodeToString(serializable)
        )
    }

    fun removeBuff(buff: RPGBuffData<*>) {
        debug("Removing buff $buff from item $id")
        stack.removeTag(buff.id.toString())
    }



    override fun apply(profile: RPGProfile) {
        profile.buffs.attach(attachId, buffs)
    }

    override fun unApply(profile: RPGProfile) {
        profile.buffs.detach(attachId)
    }

    override fun toString(): String {
        return "NBTRPGItem(stack=$stack, buffs=$buffs)"
    }
}