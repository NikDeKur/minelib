@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.movement

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.ext.online
import dev.nikdekur.minelib.ext.runSync
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class ConfigMovementService(override val app: MineLib) : MovementService, PluginService {

    override val bindClass: KClass<*>
        get() = MovementService::class

    val lastLocationMap = ConcurrentHashMap<UUID, Location>()

    override fun onLoad() {
        val config = app.loadConfig<MovementConfig>("movement")
        val delayTicks = config.movementUpdateDelay
        app.scheduler.runTaskTimerAsynchronously(delayTicks) {
            online.forEach(::update)
        }
    }

    override fun onUnload() {
        lastLocationMap.clear()
    }

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