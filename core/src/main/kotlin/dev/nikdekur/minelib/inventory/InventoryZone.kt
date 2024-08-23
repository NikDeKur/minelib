package dev.nikdekur.minelib.inventory

import kotlin.math.min


@JvmInline
value class InventoryZone(val slots: MutableSet<Int>) {

    fun remove(slot: Int) {
        slots.remove(slot)
    }

    companion object {

        @JvmStatic
        fun ofShape(corner1: Int, corner2: Int): InventoryZone {
            val slots = HashSet<Int>()

            val startRow = (corner1 - 1) / 9
            val endRow = (corner2 - 1) / 9

            for (row in startRow..endRow) {
                val startSlot = row * 9 + (corner1 - 1) % 9
                val endSlot = (row * 9 + min(((corner2 - 1) % 9).toDouble(), 8.0)).toInt()

                for (slot in startSlot..endSlot) {
                    slots.add(slot + 1)
                }
            }

            return InventoryZone(slots)
        }
    }
}
