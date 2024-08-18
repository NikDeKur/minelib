@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.gui

import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.service.PluginComponent
import dev.nikdekur.ndkore.service.inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.InventoryView
import java.util.*

@Suppress("LeakingThis")
abstract class GUI(val player: Player, val size: Int) : InventoryHolder, PluginComponent {

    val service by inject<GUIService>()

    val id: UUID = UUID.randomUUID()
    lateinit var inv: Inventory
    lateinit var inventoryView: InventoryView
    open val flags: Set<GUIFlag> = emptySet()

    override fun getInventory(): Inventory {
        return inv
    }

    abstract fun getTitle(): String
    
    val titleFinal: String
        get() = getTitle().applyColors()




    init {
        service.registerGUI(this)
    }

    var initialized = false
    fun init() {
        inv = Bukkit.createInventory(this, size, titleFinal)
        onCreate()
    }
    inline fun checkInit() {
        if (!initialized) {
            init()
            initialized = true
        }
    }
    inline fun checkInitOrThrow() {
        check(initialized) { "GUI is not initialized! Initialization happens when gui is open" }
    }

    open fun open() {
        checkInit()
        beforeOpen()
        inventoryView = player.openInventory(inv)
    }

    open fun close() {
        checkInitOrThrow()
        player.closeInventory()
    }

    open fun update() {
        checkInit()
        inventory.clear()
        beforeOpen()
    }

    open fun finish() {
        service.unregisterGUI(this)
    }

    open fun closeAndFinish() {
        close()
        finish()
    }




    open fun onCreate() {
        // Override to add custom logic
    }
    open fun beforeOpen() {
        // Override to add custom logic
    }

    open fun onOpen(event: InventoryOpenEvent) {
        // Override to add custom logic
    }
    open fun onClick(event: InventoryClickEvent) {
        // Override to add custom logic
    }
    open fun onClose(event: InventoryCloseEvent) { finish() }
    open fun onDrag(event: InventoryDragEvent) {
        // Override to add custom logic
    }
    open fun onInteract(event: InventoryInteractEvent) {
        // Override to add custom logic
    }

    override fun toString(): String {
        return "GUI(id=$id, class=${javaClass.name}, player=${player.name}, init=$initialized)"
    }
}



