package dev.nikdekur.minelib.inventory

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

enum class InventorySlot(val slot: Int) {
    // ARMOR
    HELMET(103),
    CHESTPLATE(102),
    LEGGINGS(101),
    BOOTS(100),


    // Inventory
    R4_1(9),
    R4_2(10),
    R4_3(11),
    R4_4(12),
    R4_5(13),
    R4_6(14),
    R4_7(15),
    R4_8(16),
    R4_9(17),

    R3_1(18),
    R3_2(19),
    R3_3(20),
    R3_4(21),
    R3_5(22),
    R3_6(23),
    R3_7(24),
    R3_8(25),
    R3_9(26),

    R2_1(27),
    R2_2(28),
    R2_3(29),
    R2_4(30),
    R2_5(31),
    R2_6(32),
    R2_7(33),
    R2_8(34),
    R2_9(35),

    R1_1(0),
    R1_2(1),
    R1_3(2),
    R1_4(3),
    R1_5(4),
    R1_6(5),
    R1_7(6),
    R1_8(7),
    R1_9(8),


    // HotBat
    HB_1(0),
    HB_2(1),
    HB_3(2),
    HB_4(3),
    HB_5(4),
    HB_6(5),
    HB_7(6),
    HB_8(7),
    HB_9(8),


    // Hands
    HAND(-1),
    OFF_HAND(-1);


    fun get(inventory: Inventory): ItemStack? {
        val item: ItemStack?
        val slot = slot
        if (slot != -1) {
            return inventory.getItem(slot)
        } else {
            item = if (inventory is PlayerInventory) {
                when (this) {
                    HAND -> inventory.itemInMainHand
                    OFF_HAND -> inventory.itemInOffHand
                    else -> null
                }
            } else null
        }

        if (item == null || item.type == Material.AIR) {
            return null
        }

        return item
    }


    companion object {


        val inventorySlots: List<InventorySlot> = listOf(
                HELMET, CHESTPLATE, LEGGINGS, BOOTS,

                R4_1, R4_2, R4_3, R4_4, R4_5, R4_6, R4_7, R4_8, R4_9,
                R3_1, R3_2, R3_3, R3_4, R3_5, R3_6, R3_7, R3_8, R3_9,
                R2_1, R2_2, R2_3, R2_4, R2_5, R2_6, R2_7, R2_8, R2_9,
                R1_1, R1_2, R1_3, R1_4, R1_5, R1_6, R1_7, R1_8, R1_9
            )

        val allPlayerSlots: List<InventorySlot> = listOf(
                HELMET, CHESTPLATE, LEGGINGS, BOOTS,

                R4_1, R4_2, R4_3, R4_4, R4_5, R4_6, R4_7, R4_8, R4_9,
                R3_1, R3_2, R3_3, R3_4, R3_5, R3_6, R3_7, R3_8, R3_9,
                R2_1, R2_2, R2_3, R2_4, R2_5, R2_6, R2_7, R2_8, R2_9,
                R1_1, R1_2, R1_3, R1_4, R1_5, R1_6, R1_7, R1_8, R1_9,

                HAND, OFF_HAND
            )


        val allEntitySlots: List<InventorySlot> = listOf(
                HELMET, CHESTPLATE, LEGGINGS, BOOTS,

                HAND, OFF_HAND
            )
    }
}
