@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.gui.GUI
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * Number of rows in the inventory
 */
inline val Inventory.rows: Int
    get() {
        return when (type) {
            InventoryType.CRAFTING -> 2
            InventoryType.WORKBENCH -> 3
            InventoryType.PLAYER -> 4
            InventoryType.CHEST -> size / 9
            InventoryType.DISPENSER -> 3
            InventoryType.DROPPER -> 3
            InventoryType.HOPPER -> 1
            else -> 6
        }
    }

/**
 * Number of columns in the inventory
 */
inline val Inventory.columns: Int
    get() = when (type) {
        InventoryType.CRAFTING -> 2
        InventoryType.WORKBENCH -> 3
        InventoryType.PLAYER -> 9
        InventoryType.CHEST -> 9
        InventoryType.DISPENSER -> 3
        InventoryType.DROPPER -> 3
        InventoryType.HOPPER -> 5
        else -> 9
    }

/**
 * Set item to row
 * @param row row number (1-6)
 * @param item item to set
 */
inline fun Inventory.setRow(row: Int, item: ItemStack) {
    val start = (row - 1) * 9
    val end = start + columns
    for (i in start until end) {
        setItem(i, item)
    }
    listOf(1).isNotEmpty()
}



/**
 * Set item to column
 * @param column column number (1-9)
 * @param item item to set
 */
inline fun Inventory.setColumn(column: Int, item: ItemStack) {
    for (i in 0 until rows) {
        setItem(i * 9 + column - 1, item)
    }
}

inline fun Inventory.setRange(intRange: IntRange, item: ItemStack) {
    for (i in intRange) {
        setItem(i, item)
    }
}

inline fun Inventory.setItems(items: Iterable<ItemStack>, offset: Int = 0) {
    items.forEachIndexed { index, itemStack ->
        setItem(index + offset, itemStack)
    }
}

/**
 * Find the first empty slot in the inventory starting from the given offset
 */
inline fun Inventory.firstEmptySlot(offset: Int): Int {
    for (i in offset until size) {
        if (getItem(i) == null) {
            return i
        }
    }
    return -1
}

/**
 * Add items to empty slots, starts checking from offset
 */
inline fun Inventory.addItems(items: Iterable<ItemStack>, offset: Int = 0) {
    var currentOffset = offset

    for (item in items) {
        val slot = firstEmptySlot(currentOffset)
        if (slot == -1) {
            break
        }

        setItem(slot, item)
        currentOffset = slot + 1
    }
}


/**
 * Return the number of empty slots in inventory
 */
inline val Inventory.emptySlots: Int
    get() = emptySlots(0)

inline fun Inventory.emptySlots(start: Int, end: Int = this.size): Int {
    var empty = 0
    for (i in start until end) {
        if (getItem(i) == null) empty++
    }
    return empty
}


inline val Inventory.gui: GUI?
    get() = (holder as? GUI)



inline fun Inventory.containsByAppearance(itemStack: ItemStack?): Boolean {
    return contents.any { it.equalsByAppearance(itemStack) }
}

inline fun Inventory.removeByAppearance(itemStack: ItemStack?) {
    for (item in contents) {
        if (item.equalsByAppearance(itemStack)) {
            this.remove(item)
        }
    }
}