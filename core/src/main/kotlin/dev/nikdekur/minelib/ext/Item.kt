@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.tr7zw.changeme.nbtapi.NBT
import de.tr7zw.changeme.nbtapi.NBTType
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT
import dev.nikdekur.ndkore.ext.r_SetField
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
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

fun <T> ItemStack.editNBT(func: ReadWriteItemNBT.() -> T) = NBT.modify(this, func)

fun ReadWriteItemNBT.getTag(key: String): Any? {
    val type = getType(key)
    return when (type) {
        NBTType.NBTTagByte -> getByte(key)
        NBTType.NBTTagShort -> getShort(key)
        NBTType.NBTTagInt -> getInteger(key)
        NBTType.NBTTagLong -> getLong(key)
        NBTType.NBTTagFloat -> getFloat(key)
        NBTType.NBTTagDouble -> getDouble(key)
        NBTType.NBTTagByteArray -> getByteArray(key)
        NBTType.NBTTagString -> getString(key)
        NBTType.NBTTagIntArray -> getIntArray(key)
        NBTType.NBTTagLongArray -> getLongArray(key)
        else -> null
    }
}

fun ItemStack.setTag(key: String, value: Any): ItemStack {
    editNBT {
        when (value) {
            is String -> setString(key, value)
            is Int -> setInteger(key, value)
            is Double -> setDouble(key, value)
            is Float -> setFloat(key, value)
            is Long -> setLong(key, value)
            is Short -> setShort(key, value)
            is Byte -> setByte(key, value)
            is ByteArray -> setByteArray(key, value)
            is IntArray -> setIntArray(key, value)
            is LongArray -> setLongArray(key, value)
            is Boolean -> setBoolean(key, value)
            is ItemStack -> setItemStack(key, value)
            is UUID -> setUUID(key, value)
            is Enum<*> -> setEnum(key, value)

            else -> throw IllegalArgumentException("Unsupported type: ${value::class.java}")
        }
    }
    return this
}

fun ItemStack.removeTag(key: String): ItemStack {
    editNBT {
        removeKey(key)
    }
    return this
}


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
    editNBT {
        if (state)
            setByte("HideFlags", 6)
        else
            removeKey("HideFlags")
    }
    return this
}


inline fun ItemStack.setSkullTexture(texture: String?) {
    editMeta {
        if (this is SkullMeta) {
            val profile =
                if (texture == null) null
                else GameProfile(UUID.randomUUID(), null).apply {
                    properties.put("textures", Property("textures", texture))
                }

            r_SetField("profile", profile)
        }
    }
}


fun ItemStack.setTouchable(state: Boolean) = editNBT {
    setBoolean("untouchable", !state)
}
/**
 * @return true by default, false if item is not touchable.
 */
fun ItemStack.isTouchable() = editNBT {
    !getBoolean("untouchable")
}

inline val Material.isAir: Boolean
    get() = this == Material.AIR



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
