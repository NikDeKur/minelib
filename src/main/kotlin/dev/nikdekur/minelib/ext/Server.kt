@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.DedicatedPlayerList
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_12_R1.CraftServer
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Logger


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
 * Get the server's online players as a collection
 */
inline val online: Collection<Player>
    get() = Bukkit.getOnlinePlayers()

/**
 * Get the server's online players as a list of [EntityPlayer]
 */
inline val onlineNMS: List<EntityPlayer>
    get() = Bukkit.getServer().nms.players

/**
 * Get the server's offline players as an ArrayList
 *
 * The new list is created every time, so it is safe to modify it.
 */
inline val onlineNames: ArrayList<String>
    get() = online.map { it.name } as ArrayList<String>

/**
 * Get the server's online players uuids as an ArrayList
 *
 * The new list is created every time, so it is safe to modify it.
 */
inline val onlineUUIDs: ArrayList<UUID>
    get() = online.map { it.uniqueId } as ArrayList<UUID>

/**
 * Get the server's offline players as an array
 */
inline val offline: Array<out OfflinePlayer>
    get() = Bukkit.getOfflinePlayers()

/**
 * Get the server's offline players names as an ArrayList
 *
 * The new list is created every time, so it is safe to modify it.
 */
inline val offlineNames: ArrayList<String>
    get() = offline.map { it.name } as ArrayList<String>

/**
 * Get the server's offline players uuids as an ArrayList
 *
 * The new list is created every time, so it is safe to modify it.
 */
inline val offlineUUIDs: ArrayList<UUID>
    get() = offline.map { it.uniqueId } as ArrayList<UUID>

/**
 * Perform a command as the console
 *
 * @param command the command to perform
 *
 * @return `true` if the command was performed successfully, `false` otherwise
 */
inline fun performCommand(command: String): Boolean {
    return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
}






/**
 * Get the bukkit logger
 */
inline val bLogger: Logger
    get() = Bukkit.getLogger()