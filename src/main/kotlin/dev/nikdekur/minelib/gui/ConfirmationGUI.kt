package dev.nikdekur.minelib.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import dev.nikdekur.minelib.ext.cancel
import dev.nikdekur.minelib.ext.isEmpty
import dev.nikdekur.minelib.ext.setTag

abstract class ConfirmationGUI(player: Player) : GUI(player, 27) {
    abstract fun getMainItem(): ItemStack
    abstract fun getConfirmItem(): ItemStack
    abstract fun getCancelItem(): ItemStack

    override val flags: Set<GUIFlag> = setOf(GUIFlag.CANNOT_TAKE, GUIFlag.CANNOT_PUT)

    open val confirmItemPosition = 11
    open val cancelItemPosition = 15
    open val mainItemPosition = 13

    override fun beforeOpen() {
        val confirmItem = getConfirmItem()
            .setTag("item", "gui_confirm")
        val cancelItem = getCancelItem()
            .setTag("item", "gui_cancel")
        val mainItem = getMainItem()
            .setTag("item", "gui_main")

        inventory.setItem(confirmItemPosition, confirmItem)
        inventory.setItem(cancelItemPosition, cancelItem)
        inventory.setItem(mainItemPosition, mainItem)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.cancel()
        if (event.isShiftClick) return
        if (event.currentItem.isEmpty()) return
        when (event.slot) {
            confirmItemPosition -> onConfirm(event)
            cancelItemPosition -> onCancel(event)
            mainItemPosition -> onMain(event)
        }

        if (event.slot != mainItemPosition)
            closeAndFinish()
    }

    abstract fun onConfirm(event: InventoryClickEvent)
    open fun onCancel(event: InventoryClickEvent) {
        // Override this method to add custom logic
    }
    open fun onMain(event: InventoryClickEvent) {
        // Override this method to add custom logic
    }


}