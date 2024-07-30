@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.movement

import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.event.movement.OptiPlayerMoveEvent
import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.ext.online
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object MovementManager : MineLibModule {

    val lastLocationMap = HashMap<UUID, Location>()

    // NOTE: ASYNC
    fun update(player: Player) {
        val location = player.location
        val lastLocation = lastLocationMap[player.uniqueId]
        val event = if (lastLocation == null) {
            null
        } else if (lastLocation.x != location.x || lastLocation.y != location.y || lastLocation.z != location.z || lastLocation.world != location.world) {
            OptiPlayerMoveEvent(player, lastLocation.clone(), location.clone())
        } else {
            null
        }

        event?.call()

        if (event != null && event.isCancelled) {
            processCancel(event)
            return
        }

        if (event == null || !event.isCancelled)
            lastLocationMap[player.uniqueId] = location
    }

    fun processCancel(event: OptiPlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val to = if (event.cancelYaw && event.cancelPitch) {
            from
        } else {
            val pL = player.location
            val newL = from.clone()
            val yaw = event.cancelYaw
            val pitch = event.cancelPitch
            if (!yaw)
                newL.yaw = pL.yaw
            if (!pitch)
                newL.pitch = pL.pitch
            newL
        }
        asyncTeleport(player, to)
        return
    }

    /**
     * Teleport player to location and save new location as last location
     *
     * This prevents calling [OptiPlayerMoveEvent]
     * It Could be useful if you want to teleport a player to a specific location, while some listener cancelling any player moving.
     *
     * @param player Player to teleport
     * @param location Location to teleport player
     */
    fun teleport(player: Player, location: Location) {
        player.teleport(location)
        lastLocationMap[player.uniqueId] = location
    }

    /**
     * Teleport player to specific location thread-safely.
     *
     * Method will be executed in the main thread next tick.
     */
    inline fun asyncTeleport(player: Player, location: Location) {
        app.scheduler.runTask {
            teleport(player, location)
        }
    }

    override fun onLoad() {
        val config = app.loadConfig<MovementConfig>("movement")
        val delayTicks = config.movementUpdateDelay
        app.scheduler.runTaskTimerAsynchronously(delayTicks) {
            online.forEach { update(it) }
        }
    }

    override fun onUnload() {
        lastLocationMap.clear()
    }
}