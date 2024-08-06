package dev.nikdekur.minelib.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import dev.nikdekur.minelib.ext.addItems
import dev.nikdekur.minelib.ext.cancel
import dev.nikdekur.minelib.ext.getStringTag
import dev.nikdekur.minelib.item.Patterns

abstract class PagedGUI(
    player: Player,
    size: Int,
    var page: Int = 0
) : GUI(player, size) {

    open val previousArrowPos: Int = 45
    open val nextArrowPos: Int = 53

    open val previousArrow: ItemStack = Patterns.ARROW_PREVIOUS.build(player)
    open val nextArrow: ItemStack = Patterns.ARROW_NEXT.build(player)

    open val startInventoryFrom: Int = 0
    val allZoneSize by lazy {
        size - startInventoryFrom
    }

    private var _content: List<ItemStack>? = null
    abstract val content: List<ItemStack>

    open fun getFinalContent(): List<ItemStack> {
        if (_content != null) return _content!!
        val list = content
        _content = list
        return list
    }

    open fun getPlaced(upToPage: Int): Int {
        if (upToPage <= 0) return 0
        if (upToPage == 1) return allZoneSize - 1

        var count = 0

        for (i in 0 until upToPage) {
            count += allZoneSize
            count -= if (i == 0) 1 else 2
        }

        return count
    }

    /**
     * Check if a page exists
     *
     * Do [getPlaced] + 1 because [getPlaced] thinks that the next page already exists
     *
     * We increase the slots taken by one slot that [getPlaced] takes for the next page arrow
     *
     * So if all items can be placed in page before, we say that no next page exists
     */
    open fun isPageExists(page: Int): Boolean {
        val all = getFinalContent()
        val placed = getPlaced(page) + 1
        return all.size > placed
    }


    override val flags: Set<GUIFlag> = setOf(GUIFlag.CANNOT_PUT, GUIFlag.CANNOT_TAKE)

    override fun beforeOpen() {
        changeInventory(inventory)

        val all = getFinalContent()

        var zone = allZoneSize
        if (page > 0) {
            inventory.setItem(previousArrowPos, previousArrow)
            zone--
        }

        val placed = getPlaced(page)

        val leftToLocate = all.size - placed
        if (leftToLocate > zone) {
            inventory.setItem(nextArrowPos, nextArrow)
            zone--
        }

        // Content
        val toIndex = minOf(placed + zone, all.size)
        val pageContent = all.subList(placed, toIndex)

        inventory.addItems(pageContent, startInventoryFrom)
    }

    /**
     * Update the current inventory
     *
     * Call when equipable in inventory seems to be changed
     *
     * @see [isPageExists]
     */
    override fun update() {
        while (!isPageExists(page) && page > 0) {
            page--
        }
        _content = null
        super.update()
    }

    open fun openPage(page: Int) {
        this.page = page
        open()
    }

    override fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem
        if (item != null) {
            val itemTag = item.getStringTag("ITEM")
            if (itemTag != null) {
                if (itemTag == "ARROW_PREVIOUS" && event.slot == previousArrowPos) {
                    page--
                    update()
                    event.cancel()
                    return
                } else if (itemTag == "ARROW_NEXT" && event.slot == nextArrowPos) {
                    page++
                    update()
                    event.cancel()
                    return
                }
            }
        }

        whenClick(event)
    }


    open fun whenClick(event: InventoryClickEvent) {}
    open fun changeInventory(inventory: Inventory) {}
}
