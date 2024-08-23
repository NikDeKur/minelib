package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.minelib.movement.OptiPlayerMoveEvent
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.RPGProfilesService
import dev.nikdekur.minelib.service.PluginListener
import dev.nikdekur.ndkore.service.inject
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerChangedWorldEvent

class DefaultConditionsListener(
    override val app: ServerPlugin
) : PluginListener {

    val rpgProfilesService: RPGProfilesService by inject()

    // -------------------
    //      INVENTORY
    // -------------------

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val profile = rpgProfilesService.getProfile(event.whoClicked) ?: return
        profile.buffs.updateConditional(DefaultConditions.INVENTORY)
    }

    @EventHandler
    fun onInventoryInteract(event: InventoryInteractEvent) {
        val profile = rpgProfilesService.getProfile(event.whoClicked) ?: return
        profile.buffs.updateConditional(DefaultConditions.INVENTORY)
    }




    // -------------------
    //       WORLD
    // -------------------

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        val profile = rpgProfilesService.getProfile(event.player) ?: return
        profile.buffs.updateConditional(DefaultConditions.WORLD)
    }








    // -------------------
    //      LOCATION
    // -------------------
    @EventHandler
    fun onMove(event: OptiPlayerMoveEvent) {
        val profile = rpgProfilesService.getProfile(event.player) ?: return
        profile.buffs.updateConditional(DefaultConditions.LOCATION)
    }




}