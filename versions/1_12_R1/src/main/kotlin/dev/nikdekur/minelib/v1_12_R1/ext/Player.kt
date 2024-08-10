@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.v1_12_R1.ext

import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

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