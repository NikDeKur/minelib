@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld

inline val World.craft: CraftWorld
    get() = (this as CraftWorld)

inline val World.nms: WorldServer
    get() = craft.handle

inline fun World.broadcast(vararg packets: Packet<*>) {
    players.forEach { player -> player.sendPackets(*packets) }
}