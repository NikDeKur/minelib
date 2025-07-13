@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

inline operator fun EntityEquipment.get(slot: EquipmentSlot): ItemStack? =
    when (slot) {
        EquipmentSlot.HAND -> itemInMainHand
        EquipmentSlot.OFF_HAND -> itemInOffHand
        EquipmentSlot.HEAD -> helmet
        EquipmentSlot.CHEST -> chestplate
        EquipmentSlot.LEGS -> leggings
        EquipmentSlot.FEET -> boots
    }