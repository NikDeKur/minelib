@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.nikdekur.minelib.MineLib
import dev.nikdekur.ndkore.ext.r_SetField
import org.bukkit.Material
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





fun ItemStack.setTag(tag: String, value: Any): ItemStack {
    MineLib.versionAdapter.setTag(this, tag, value)
    return this
}


fun ItemStack.getTag(tag: String): Any? {
    return MineLib.versionAdapter.getTag(this, tag)
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
    return tagValue as? Byte
}

fun ItemStack.getByteArrayTag(tag: String): ByteArray? {
    val tagValue = getTag(tag)
    return tagValue as? ByteArray
}

fun ItemStack.getShortTag(tag: String): Short? {
    val tagValue = getTag(tag)
    return tagValue as? Short
}

fun ItemStack.getIntTag(tag: String): Int? {
    val tagValue = getTag(tag)
    return tagValue as? Int
}

fun ItemStack.getIntArrayTag(tag: String): IntArray? {
    val tagValue = getTag(tag)
    return tagValue as? IntArray
}

fun ItemStack.getFloatTag(tag: String): Float? {
    val tagValue = getTag(tag)
    return tagValue as? Float
}

fun ItemStack.getDoubleTag(tag: String): Double? {
    val tagValue = getTag(tag)
    return tagValue as? Double
}

fun ItemStack.getLongTag(tag: String): Long? {
    val tagValue = getTag(tag)
    return tagValue as? Long
}

fun ItemStack.getBooleanTag(tag: String): Boolean? {
    return getTag(tag) as? Boolean
}

fun ItemStack.removeTag(tag: String): ItemStack {
    MineLib.versionAdapter.removeTag(this, tag)
    return this
}

fun ItemStack.hasTag(tag: String): Boolean {
    return MineLib.versionAdapter.hasTag(this, tag)
}

val ItemStack.tags: Collection<String>
    get() = MineLib.versionAdapter.getTags(this)

@Suppress("UNCHECKED_CAST")
val ItemStack.tagsMap: Map<String, Any>
    get() = MineLib.versionAdapter.getTagsMap(this)


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
