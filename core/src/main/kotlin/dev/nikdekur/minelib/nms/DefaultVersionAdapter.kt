package dev.nikdekur.minelib.nms

import dev.nikdekur.minelib.MineLib
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object DefaultVersionAdapter : VersionAdapter {
    override fun init(plugin: MineLib) {
        // NOOP
    }

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