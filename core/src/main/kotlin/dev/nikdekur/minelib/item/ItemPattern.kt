@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.item

import dev.nikdekur.minelib.ext.*
import dev.nikdekur.minelib.i18n.locale.LocaleProvider
import dev.nikdekur.minelib.i18n.sender.PlayerContext
import dev.nikdekur.ndkore.ext.toTArray
import dev.nikdekur.ornament.i18n.Key
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.function.BiConsumer

class ItemPattern {
    var material: Material = Material.STONE
    fun setMaterial(material: Material): ItemPattern {
        this.material = material
        return this
    }

    var displayName: String? = null
    fun setDisplayName(name: String?): ItemPattern {
        displayName = name
        return this
    }





    val lore = LinkedList<String>()
    fun addLore(lore: List<String>): ItemPattern {
        this.lore.clear()
        this.lore.addAll(lore)
        return this
    }

    inline fun setLore(vararg lore: String) = addLore(lore.toList())
    fun addLore(lore: String): ItemPattern {
        this.lore.add(lore)
        return this
    }





    var displayNameKey: Key? = null
    fun setDisplayName(key: Key?): ItemPattern {
        this.displayNameKey = key
        return this
    }



    var loreKeys: MutableCollection<Key>? = null
    fun addLore(vararg keys: Key): ItemPattern {
        val lore = loreKeys ?: LinkedList<Key>().also { loreKeys = it }
        lore.addAll(keys)
        return this
    }

    fun setLore(keys: Collection<Key>?): ItemPattern {
        loreKeys = keys?.toMutableList()
        return this
    }




    fun getFinalDisplayName(
        localeProvider: LocaleProvider?,
        modify: (Key) -> Key
    ): String? {
        return if (localeProvider != null && displayNameKey != null) {
            localeProvider.translate(modify(displayNameKey!!))
        } else {
            displayName
        }
    }


    fun getFinalLore(
        localeProvider: LocaleProvider?,
        modify: (Key) -> Key
    ): List<String> {
        val keys: Collection<Key>? = loreKeys
        return if (localeProvider != null && keys != null) {
            keys.map(modify).map(localeProvider::translate)
        } else {
            lore
        }
    }


    val enchantments: MutableMap<Enchantment, Int> = HashMap()
    fun addEnchantment(enchantment: Enchantment, level: Int): ItemPattern {
        enchantments[enchantment] = level
        return this
    }


    fun setHideAttributes(state: Boolean): ItemPattern {
        if (state) this.setTag("HideFlags", 6)
        else tags.remove("HideFlags")
        return this
    }

    val hideFlags = ArrayList<ItemFlag>()
    fun addFlags(vararg flags: ItemFlag): ItemPattern {
        hideFlags.addAll(flags)
        return this
    }


    fun addFlags(flags: Collection<ItemFlag>): ItemPattern {
        hideFlags.addAll(flags)
        return this
    }


    val attributeModifiers = LinkedList<AttributeModifierData>()
    fun addAttributeModifier(attribute: AttributeModifierData): ItemPattern {
        attributeModifiers.add(attribute)
        return this
    }

    fun addAttributeModifier(
        attribute: Attribute,
        operation: AttributeModifier.Operation,
        slot: EquipmentSlot,
        amount: Double,
        uuid: UUID = UUID.randomUUID()
    ) = addAttributeModifier(AttributeModifierData(attribute, operation, slot, amount, uuid))



    var unbreakable: Boolean = false
    fun setUnbreakable(state: Boolean): ItemPattern {
        editMeta { itemMeta, _ ->
            itemMeta.isUnbreakable = state
            unbreakable = state
        }
        return this
    }




    fun setTouchable(state: Boolean): ItemPattern {
        if (!state) this.setTag("touchable", false)
        else tags.remove("touchable")
        return this
    }
    /**
     * @return true by default, false if item is not droppable
     */
    fun isTouchable() = getTag("touchable") as? Boolean != false



    val tags = HashMap<String, Any>()
    fun setTag(key: String, value: Any?): ItemPattern {
        if (value == null) tags.remove(key)
        else tags[key] = value
        return this
    }

    inline fun hasTag(key: String) = tags.containsKey(key)
    inline fun getTag(key: String) = tags[key]



    val onBuild = LinkedList<BiConsumer<ItemStack, Player?>>()
    fun onBuild(action: (ItemStack, Player?) -> Unit): ItemPattern {
        onBuild.add(action)
        return this
    }
    fun onBuildFirst(action: (ItemStack, Player?) -> Unit): ItemPattern {
        onBuild.addFirst(action)
        return this
    }

    fun editMeta(action: (ItemMeta, Player?) -> Unit): ItemPattern {
        onBuild { itemStack, player ->
            itemStack.editMeta {
                action(this, player)
            }
        }
        return this
    }


    var amount = 1
    fun setAmount(amount: Int): ItemPattern {
        this.amount = amount
        return this
    }

    var durability: Short = 0
    fun setDurability(durability: Short): ItemPattern {
        this.durability = durability
        return this
    }

    var data: Byte? = null
    fun setData(data: Byte?): ItemPattern {
        this.data = data
        return this
    }

    /**
     * Set colour for colorable items
     */
    @Suppress("DEPRECATION")
    fun setColor(color: DyeColor): ItemPattern {
        return setDurability(color.woolData.toShort())
    }

    var armorColor: Color? = null
    fun setArmorColor(color: Color): ItemPattern {
        this.armorColor = color
        return this
    }

    var skullOwner: String? = null
    fun setSkullOwner(owner: String): ItemPattern {
        skullOwner = owner
        return this
    }

    var skullTexture: String? = null
    fun setSkullTexture(value: String): ItemPattern {
        skullTexture = value
        return this
    }

    var unstackable: Boolean = false
    fun setUnstackable(state: Boolean = true): ItemPattern {
        unstackable = state
        return this
    }

    fun setEggType(type: EntityType): ItemPattern {
        if (this.material != Material.MONSTER_EGG) return this
        @Suppress("DEPRECATION")
        setDurability(type.typeId)
        return this
    }


    fun clone() = ItemPattern()
        .setMaterial(material)
        .setDisplayName(displayName)
        .addLore(lore)
        .setDisplayName(displayNameKey)
        .setLore(loreKeys)
        .setAmount(amount)
        .setDurability(durability)
        .setData(data)
        .also { it.tags.putAll(tags) }
        .also { it.onBuild.addAll(onBuild) }


    fun build(
        localeProvider: LocaleProvider? = null,
        player: Player? = null,
        modify: (Key) -> Key = { it }
    ): ItemStack {
        @Suppress("DEPRECATION")
        val item = ItemStack(material, amount, durability, data)

        val finalDisplay = getFinalDisplayName(localeProvider, modify)
        if (finalDisplay != null) item.setDisplayName(finalDisplay)

        val finalLore = getFinalLore(localeProvider, modify)
        if (finalLore.isNotEmpty()) item.setLore(finalLore)

        enchantments.forEach { (enchantment, level) ->
            item.addEnchantment(enchantment, level)
        }

        attributeModifiers.forEach { attribute ->
            item.addAttributeModifier(attribute)
        }

        item.editMeta {
            if (hideFlags.isNotEmpty())
                addItemFlags(*hideFlags.toTArray())

            if (isUnbreakable)
                this.isUnbreakable = unbreakable

            @Suppress("DEPRECATION")
            if (this is SkullMeta)
                this.owner = skullOwner

            if (this is LeatherArmorMeta)
                this.color = armorColor
        }

        if (skullTexture != null)
            item.setSkullTexture(skullTexture)

        if (unstackable)
            setTag("unstack", UUID.randomUUID().toString())

        onBuild.forEach {
            it.accept(item, player)
        }

        tags.forEach(item::setTag)

        return item
    }




    companion object {

        @JvmStatic
        inline fun from(material: Material) = ItemPattern()
            .setMaterial(material)

        @JvmStatic
        inline fun from(item: Item) {
            Material.getMaterial(item.name)?.let { from(it) }
        }

        @JvmStatic
        fun fromSkull(value: String): ItemPattern {
            return from(Material.SKULL_ITEM)
                .setDurability(3)
                .setSkullTexture(value)
        }
    }
}

inline fun ItemPattern.build(
    playerContext: PlayerContext,
    noinline modify: (Key) -> Key = { it }
) = build(
    localeProvider = playerContext.localeProvider,
    player = playerContext.sender,
    modify = modify
)