package dev.nikdekur.minelib.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import dev.nikdekur.minelib.ext.cancel
import dev.nikdekur.minelib.ext.gui
import dev.nikdekur.minelib.ext.isAir
import dev.nikdekur.minelib.ext.isTouchable
import dev.nikdekur.minelib.gui.GUIFlag.CANNOT_PUT
import dev.nikdekur.minelib.gui.GUIFlag.CANNOT_TAKE
import java.util.*

object GUIManager : Listener {

    val guis = HashMap<UUID, GUI>()

    fun registerGUI(gui: GUI) {
        guis[gui.id] = gui
    }

    fun unregisterGUI(gui: GUI) {
        guis.remove(gui.id)
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        event.inventory.gui?.onOpen(event)
    }

    @EventHandler
    fun onGUIClick(event: InventoryClickEvent) {
        event.inventory.gui?.onClick(event)
    }

    @EventHandler
    fun onGUIClose(event: InventoryCloseEvent) {
        event.inventory.gui?.onClose(event)
    }

    @EventHandler
    fun onGUIDrag(event: InventoryDragEvent) {
        event.inventory.gui?.onDrag(event)
    }

    @EventHandler
    fun onGUIInteract(event: InventoryInteractEvent) {
        event.inventory.gui?.onInteract(event)
    }



    val takeActions: HashSet<InventoryAction> = HashSet()
    val putActions: HashSet<InventoryAction> = HashSet()

    init {
        for (action in InventoryAction.entries) {
            when (action) {
                InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME,
                InventoryAction.COLLECT_TO_CURSOR, InventoryAction.CLONE_STACK,
                InventoryAction.DROP_ALL_CURSOR, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ONE_SLOT -> {
                    takeActions.add(action)
                }

                InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME -> {
                    putActions.add(action)
                }

                InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.HOTBAR_MOVE_AND_READD -> {
                    takeActions.add(action)
                    putActions.add(action)
                }

                else -> {
                    // Do nothing
                }
            }
        }
    }


    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem
        val clickedInventory = event.clickedInventory

        val gui = event.inventory.holder
        if (clickedInventory != null && gui is GUI) {
            val action = event.action

            for (flag in gui.flags) {
                when (flag) {
                    CANNOT_TAKE -> {
                        if (takeActions.contains(action) && event.inventory == clickedInventory) {
                            event.cancel()
                        }
                    }

                    CANNOT_PUT -> {
                        if (!putActions.contains(action)) continue
                        if (event.clickedInventory != event.whoClicked.inventory || event.isShiftClick) {
                            event.cancel()
                        }
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }


        if (clickedItem == null || clickedItem.type.isAir || clickedItem.itemMeta == null || event.cursor == null) return

        if (!clickedItem.isTouchable()) {
            val player = event.whoClicked as? Player
            if (player != null) {
                if (!player.scoreboardTags.contains("passTouchable"))
                    event.cancel()
            } else {
                event.cancel()
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val clickedInventory = event.inventory

        val gui = clickedInventory.holder as? GUI ?: return
        for (flag in gui.flags) {
            when (flag) {
                CANNOT_PUT -> {
                    event.isCancelled = true
                }

                else -> {
                    // Do nothing
                }
            }
        }
    }

}