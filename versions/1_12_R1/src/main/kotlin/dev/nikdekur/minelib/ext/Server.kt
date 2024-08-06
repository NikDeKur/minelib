@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.DedicatedPlayerList
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_12_R1.CraftServer
import org.bukkit.entity.Player

/**
 * Retrieve the NMS server object
 */
inline val Server.nms: DedicatedPlayerList
    get() = (this as CraftServer).handle

/**
 * Broadcast packets to all online players on the server
 *
 * @param packets the packets to broadcast
 * @see Player.sendPackets
 */
inline fun Server.broadcast(vararg packets: Packet<*>) {
    onlinePlayers.forEach { player -> player.sendPackets(*packets) }
}

/**
 * Get the server's online players as a list of [EntityPlayer]
 */
inline val onlineNMS: List<EntityPlayer>
    get() = Bukkit.getServer().nms.players