package dev.nikdekur.minelib.v1_12_R1.nms

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.utils.requireMainThread
import dev.nikdekur.minelib.v1_12_R1.ext.newNMSInstance
import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.nms.protocol.InjectProtocolModule
import dev.nikdekur.minelib.v1_12_R1.pentity.ServerPersonalEntityManagerImpl
import dev.nikdekur.ndkore.ext.r_GetField
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import net.minecraft.server.v1_12_R1.NBTTagByte
import net.minecraft.server.v1_12_R1.NBTTagByteArray
import net.minecraft.server.v1_12_R1.NBTTagCompound
import net.minecraft.server.v1_12_R1.NBTTagDouble
import net.minecraft.server.v1_12_R1.NBTTagFloat
import net.minecraft.server.v1_12_R1.NBTTagInt
import net.minecraft.server.v1_12_R1.NBTTagIntArray
import net.minecraft.server.v1_12_R1.NBTTagLong
import net.minecraft.server.v1_12_R1.NBTTagShort
import net.minecraft.server.v1_12_R1.NBTTagString
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// Class is found in Runtime so there is no usage of it in the code
@Suppress("ClassName", "unused")
object VersionAdapter_1_12_R1 : VersionAdapter {

    override fun init(plugin: MineLib) {
        plugin.registerComponent(ServerPersonalEntityManagerImpl(plugin))
        plugin.registerComponent(InjectProtocolModule(plugin))
    }

    override fun expandBB(entity: Entity, x: Float, y: Float, z: Float) {
        val entity = entity.nms
        val boundingBox = entity.boundingBox

        val x0 = boundingBox.a
        val y0 = boundingBox.b
        val z0 = boundingBox.c
        val x1 = boundingBox.d
        val y1 = boundingBox.e
        val z1 = boundingBox.f
        val aabb = AxisAlignedBB(
            x0 - x,
            y0,
            z0 - z,

            x1 + x,
            y1 + y,
            z1 + z
        )

        entity.a(aabb)
    }


    private inline fun <T> ItemStack.editMetaWithNMS(readMode: Boolean, func: (net.minecraft.server.v1_12_R1.ItemStack) -> T): T {
        val itemStack = CraftItemStack.asNMSCopy(this)
        val res = func(itemStack)
        if (!readMode) {
            this.itemMeta = CraftItemStack.getItemMeta(itemStack)
        }
        return res
    }


    private inline fun <T> ItemStack.editNBT(readMode: Boolean, crossinline func: (NBTTagCompound) -> T): T {
        return editMetaWithNMS(readMode) {
            val tag = if (it.hasTag()) {
                it.tag!!
            } else {
                it.tag = NBTTagCompound()
                it.tag
            }!!

            func(tag)
        }
    }
    private inline fun ItemStack.editNBT(crossinline func: (NBTTagCompound) -> Any?) = editNBT(false, func)

    override fun setTag(item: ItemStack, tag: String, value: Any) {
        item.editNBT {
            when (value) {
                is String -> it.setString(tag, value)
                is Byte -> it.setByte(tag, value)
                is ByteArray -> it.setByteArray(tag, value)
                is Short -> it.setShort(tag, value)
                is Int -> it.setInt(tag, value)
                is IntArray -> it.setIntArray(tag, value)
                is Long -> it.setLong(tag, value)
                is Float -> it.setFloat(tag, value)
                is Double -> it.setDouble(tag, value)
                is Boolean -> it.setBoolean(tag, value)
            }
        }

    }

    override fun getTag(item: ItemStack, tag: String): Any? {
        return item.editNBT(true) {
            when (val tagRaw = it[tag]) {
                is NBTTagString -> it.getString(tag)
                is NBTTagByte -> it.getByte(tag)
                is NBTTagByteArray -> it.getByteArray(tag)
                is NBTTagShort -> it.getShort(tag)
                is NBTTagInt -> it.getInt(tag)
                is NBTTagIntArray -> it.getIntArray(tag)
                is NBTTagFloat -> it.getFloat(tag)
                is NBTTagDouble -> it.getDouble(tag)
                is NBTTagLong -> it.getLong(tag)
                null -> null
                else -> throw UnsupportedOperationException("Path: $tag | Type: ${tagRaw.javaClass.name}")
            }
        }
    }

    override fun removeTag(item: ItemStack, tag: String) {
        item.editNBT { it.remove(tag) }
    }

    override fun hasTag(item: ItemStack, tag: String): Boolean {
        return item.editNBT(true) { it.hasKey(tag) }
    }

    override fun getTags(item: ItemStack): Collection<String> {
        return item.editNBT(true) { it.c() }
    }

    override fun getTagsMap(item: ItemStack): Map<String, Any> {
        return item.editNBT(true) {
            @Suppress("UNCHECKED_CAST")
            it.r_GetField("map").value as Map<String, Any>
        }
    }

    override fun setWalkSpeed(player: Player, speed: Float) {
        requireMainThread("tracker update")
        val handle = player.nms
        handle.abilities.walkSpeed = speed / 2f
        handle.updateAbilities()
    }

    override fun setFlySpeed(player: Player, speed: Float) {
        requireMainThread("tracker update")
        val handle = player.nms
        handle.abilities.flySpeed = speed / 2f
        handle.updateAbilities()
    }

    override fun <T : Entity> createEntity(world: World, type: EntityType): T {
        @Suppress("UNCHECKED_CAST")
        return type.newNMSInstance(world.nms).bukkitEntity as T
    }
}