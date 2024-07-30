@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.utils.requireMainThread
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.spigotmc.AsyncCatcher
import kotlin.math.ceil

// PACKETS

/**
 * Return the NMS representation of the Bukkit player.
 *
 * @throws IllegalStateException if the player is not a CraftPlayer (only CraftPlayer is supported by bukkit)
 */
inline val Player.nms
    get() = (this as? CraftPlayer)?.handle ?: error("Player is not CraftPlayer. Only CraftPlayer stores EntityPlayer.")

/**
 * Send packets to the player.
 *
 * Uses the player's connection to send the packets to the player.
 *
 * @see Packet
 */
inline fun Player.sendPackets(packets: Iterable<Packet<*>>) {
    val player = nms
    val connection = player.playerConnection ?: return
    for (packet in packets) {
        connection.sendPacket(packet)
    }
}

/**
 * Send packets to the player.
 *
 * Uses the player's connection to send the packets to the player.
 *
 * @see Packet
 */
inline fun Player.sendPackets(vararg packets: Packet<*>)  {
    val player = nms
    val connection = player.playerConnection ?: return
    for (packet in packets) {
        connection.sendPacket(packet)
    }
}

/**
 * Send packets to the player.
 *
 * Uses the player's connection to send the packets to the player.
 *
 * @see Packet
 */
inline fun EntityPlayer.sendPackets(packets: Iterable<Packet<*>>) {
    val connection = playerConnection ?: return
    for (packet in packets) {
        connection.sendPacket(packet)
    }
}

/**
 * Send packets to the player.
 *
 * Uses the player's connection to send the packets to the player.
 *
 * @see Packet
 */
inline fun EntityPlayer.sendPackets(vararg packets: Packet<*>)  {
    val connection = playerConnection ?: return
    for (packet in packets) {
        connection.sendPacket(packet)
    }
}


/**
 * Raytrace version of the [Player.getTargetBlock] method.
 *
 * This method will return the exact location and the block that the player is looking at.
 *
 * This method potentially can be slow, so it is recommended to use the [Player.getTargetLocation] method instead.
 *
 * @param maxDistance the maximum distance to raytrace
 * @param transparent the set of transparent blocks
 * @param step the step of the raytrace
 * @return the location that the player is looking at
 */
fun Player.getTargetLocation(maxDistance: Int, transparent: Set<Material>, step: Double = 1.3): Location? {
    val location = player.eyeLocation
    val direction = location.direction
    val times = (maxDistance / step).toInt()
    repeat(times) {
        val newD = direction.multiply(step).normalize()
        location.add(newD)
        val block = location.block
        if (!transparent.contains(block.type))
            return location
    }
    return null
}

/**
 * Retrieve the player's target block, but rounded up location player is looking to the top of the block.
 *
 * This method potentially can be slow, so it is recommended to use the [Player.getTargetLocation] if you don't need the exact location.
 *
 * @param maxDistance the maximum distance to raytrace
 * @param transparent the set of transparent blocks
 * @param step the step of the raytrace
 * @return the location and the block that the player is looking at
 */
inline fun Player.getTargetUpLocation(maxDistance: Int, transparent: Set<Material>, step: Double = 1.3): Location? {
    val target = getTargetLocation(maxDistance, transparent, step) ?: return null
    target.y = ceil(location.y)
    return target
}


/**
 * Method to set the player's walk speed.
 *
 * Method uses direct NMS to prevent you from stacking into Bukkit's limits, but it's still safe to use.
 *
 * @param speed the speed to set (from 0 to 1)
 */
inline fun Player.setHighWalkSpeed(speed: Float) {
    requireMainThread("tracker update")
    val handle = nms
    handle.abilities.walkSpeed = speed / 2f
    handle.updateAbilities()
}

/**
 * Method to set the player's fly speed.
 *
 * Method uses direct NMS to prevent you from stacking into Bukkit's limits, but it's still safe to use.
 *
 * @param speed the speed to set (from 0 to 1)
 */
inline fun Player.setHighFlySpeed(speed: Float) {
    requireMainThread("tracker update")
    val handle = nms
    handle.abilities.flySpeed = speed / 2f
    handle.updateAbilities()
}

/**
 * Set the player's experience depends on 2 variables:
 *
 * Player has: [has] and out from: [outFrom]
 *
 * The xp set will be [has] / [outFrom]
 *
 * @param has the amount of xp the player has
 * @param outFrom the amount of xp the player needs to level up
 * @return the experience set (float, from 0 to 1)
 */
inline fun Player.setExp(has: Number, outFrom: Number): Float {
    val experience = (has.toDouble() / outFrom.toDouble()).toFloat()
    require(experience in 0f..1f) { "Experience must be in range 0-1 (inclusive)" }
    this.exp = experience
    return experience
}