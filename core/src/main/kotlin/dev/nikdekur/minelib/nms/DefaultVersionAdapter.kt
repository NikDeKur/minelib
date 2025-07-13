package dev.nikdekur.minelib.nms

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.service.PluginService
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

open class DefaultVersionAdapter(
    override val app: PluginApplication
) : PluginService(), VersionAdapter {

    override fun expandBB(entity: Entity, x: Float, y: Float, z: Float) {
        // NOOP
    }

    override fun setHighWalkSpeed(player: Player, speed: Float) {
        player.walkSpeed = speed
    }

    override fun setHighFlySpeed(player: Player, speed: Float) {
        player.flySpeed = speed
    }

}