@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.ext.r_SetField
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*


inline fun ItemStack?.isEmpty(): Boolean {
    if (this == null) return true
    if (type == Material.AIR) return true
    if (amount == 0) return true
    if (itemMeta == null) return true
    return false
}

inline fun ItemStack.setStackAmount(amount: Int): ItemStack {
    setAmount(amount)
    return this
}



inline fun ItemStack.editMetaWithNMS(readMode: Boolean, func: (net.minecraft.server.v1_12_R1.ItemStack) -> Any?): Any? {
    val itemStack = CraftItemStack.asNMSCopy(this)
    val res = func(itemStack)
    if (!readMode) {
        this.setItemMeta(CraftItemStack.getItemMeta(itemStack))
    }
    return res
}
inline fun ItemStack.editMetaWithNMS(func: (net.minecraft.server.v1_12_R1.ItemStack) -> Any?) = editMetaWithNMS(false, func)


inline fun ItemStack.editNBT(readMode: Boolean, crossinline func: (NBTTagCompound) -> Any?): Any? {
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
inline fun ItemStack.editNBT(crossinline func: (NBTTagCompound) -> Any?) = editNBT(false, func)

fun ItemStack.setTag(tag: String, value: Any): ItemStack {
    editNBT {
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
    return this
}


fun ItemStack.getTag(tag: String): Any? {
    return editNBT(true) {
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


fun ItemStack.getStringTag(tag: String): String? {
    val tagValue = getTag(tag)
    return if (tagValue is String?)
        return if (tagValue.isNullOrEmpty())
            null
        else
            tagValue
    else
        tagValue.toString()
}

fun ItemStack.getByteTag(tag: String): Byte? {
    val tagValue = getTag(tag)
    return if (tagValue is Byte?) tagValue else null
}

fun ItemStack.getByteArrayTag(tag: String): ByteArray? {
    val tagValue = getTag(tag)
    return if (tagValue is ByteArray?) tagValue else null
}

fun ItemStack.getShortTag(tag: String): Short? {
    val tagValue = getTag(tag)
    return if (tagValue is Short?) tagValue else null
}

fun ItemStack.getIntTag(tag: String): Int? {
    val tagValue = getTag(tag)
    return if (tagValue is Int?) tagValue else null
}

fun ItemStack.getIntArrayTag(tag: String): IntArray? {
    val tagValue = getTag(tag)
    return if (tagValue is IntArray?) tagValue else null
}

fun ItemStack.getFloatTag(tag: String): Float? {
    val tagValue = getTag(tag)
    return if (tagValue is Float?) tagValue else null
}

fun ItemStack.getDoubleTag(tag: String): Double? {
    val tagValue = getTag(tag)
    return if (tagValue is Double?) tagValue else null
}

fun ItemStack.getLongTag(tag: String): Long? {
    val tagValue = getTag(tag)
    return if (tagValue is Long?) tagValue else null
}

fun ItemStack.getBooleanTag(tag: String): Boolean? {
    return editNBT(true) {
        if (!it.hasKey(tag))
            null
        else
            it.getBoolean(tag)
    } as Boolean?
}

fun ItemStack.removeTag(tag: String): ItemStack {
    editNBT { it.remove(tag) }
    return this
}

fun ItemStack.hasTag(tag: String): Boolean {
    return editNBT(true) { it.hasKey(tag) } as Boolean
}



@Suppress("UNCHECKED_CAST")
val ItemStack.tags: Set<String>
    get() = editNBT(true) { it.c() } as Set<String>

@Suppress("UNCHECKED_CAST")
val ItemStack.tagsMap: Map<String, Any>
    get() = editNBT(true) { it.r_GetField("map").value } as Map<String, Any>


inline fun ItemStack.editMeta(func: ItemMeta.() -> Unit) {
    val meta = itemMeta
    func(meta)
    itemMeta = meta
}




inline fun ItemStack.setDisplayName(name: String): ItemStack {
    editMeta {
        displayName = name.applyColors()
    }
    return this
}


inline val ItemStack.displayName: String
    get() = itemMeta.displayName




inline fun ItemStack.setLore(lore: List<String>): ItemStack {
    editMeta {
        this.lore = lore.map(String::applyColors)
    }
    return this
}

inline fun ItemStack.setLore(vararg lore: String): ItemStack {
    setLore(lore.toList())
    return this
}

inline fun ItemStack.addLore(lines: List<String>): ItemStack {
    val currentLore = this.lore
    val newLore = currentLore.toMutableList()
    newLore.addAll(lines)
    setLore(newLore)
    return this
}

inline fun ItemStack.addLore(vararg lines: String) = addLore(lines.toList())

/**
 * @return Mutable Copy of existing lore.
 */
inline var ItemStack.lore: MutableList<String>
    get() = itemMeta.lore ?: mutableListOf()
    set(value) {
        setLore(value)
    }



fun ItemStack.setHideAttributes(state: Boolean): ItemStack {
    return if (state)
        this.setTag("HideFlags", 6)
    else
        removeTag("HideFlags")
}


inline fun ItemStack.setSkullTexture(texture: String?) {
    editMeta {
        if (this is org.bukkit.inventory.meta.SkullMeta) {
            val set = if (texture == null) {
                null
            } else {
                val profile = GameProfile(UUID.randomUUID(), null)
                profile.properties.put("textures", Property("textures", texture))
                profile
            }
            r_SetField("profile", set)
        }
    }
}


fun ItemStack.setTouchable(state: Boolean) = this.setTag("touchable", state)
/**
 * @return true by default, false if item is not droppable
 */
fun ItemStack.isTouchable() = getBooleanTag("touchable") != false


inline val Material.isAir: Boolean
    get() = this == Material.AIR


inline fun ItemStack.nmsCopy(): net.minecraft.server.v1_12_R1.ItemStack {
    return CraftItemStack.asNMSCopy(this)
}


inline fun ItemStack?.equalsByAppearance(item: ItemStack?): Boolean {
    if (this == null || item == null) return false
    val type1 = this.type
    val type2 = item.type
    val meta1 = this.itemMeta
    val meta2 = item.itemMeta
    val display1 = meta1!!.displayName
    val display2 = meta2!!.displayName
    return type1 == type2 && display1 == display2
}
