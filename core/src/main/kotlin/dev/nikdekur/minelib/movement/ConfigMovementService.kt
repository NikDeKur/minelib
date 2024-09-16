@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.movement

import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.ext.online
import dev.nikdekur.minelib.ext.runSync
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.plugin.loadConfig
import dev.nikdekur.minelib.service.PluginService
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ConfigMovementService(
    override val app: ServerPlugin
) : PluginService(), MovementService {

    override val bindClass
        get() = MovementService::class

    val lastLocationMap = ConcurrentHashMap<UUID, Location>()

    override fun onEnable() {
        val config = app.loadConfig<MovementConfig>("movement")
        val delayTicks = config.movementUpdateDelay
        app.scheduler.runTaskTimerAsynchronously(delayTicks) {
            online.forEach(::update)
        }
    }

    override fun onDisable() {
        lastLocationMap.clear()
    }

    inline fun isChanged(old: Location, new: Location): Boolean {
        return old.world != new.world || old.x != new.x || old.y != new.y || old.z != new.z
    }

    // REMEMBER: ASYNC
    fun update(player: Player) {
        val location = player.location
        val lastLocation = lastLocationMap[player.uniqueId]
        val event =
            if (lastLocation == null) null
            else if (isChanged(lastLocation, location)) OptiPlayerMoveEvent(player, lastLocation.clone(), location.clone())
            else null

        event?.call()


        if (event?.isCancelled == true)
            processCancel(event)
        else
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
        teleportSafe(player, to)
        return
    }

    override fun teleport(player: Player, location: Location) {
        player.teleport(location)
        lastLocationMap[player.uniqueId] = location
    }

    override fun teleportSafe(player: Player, location: Location) {
        app.scheduler.runSync {
            teleport(player, location)
        }
    }




}