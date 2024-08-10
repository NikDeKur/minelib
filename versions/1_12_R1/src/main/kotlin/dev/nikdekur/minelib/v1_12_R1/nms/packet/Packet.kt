@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.v1_12_R1.nms.packet

import dev.nikdekur.minelib.v1_12_R1.ext.sendPackets
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.World
import org.bukkit.entity.Player

class Packet(val nms: Iterable<Packet<*>>) {

    constructor(vararg nms: Packet<*>) : this(nms.toList())

    fun send(player: Player) {
        player.sendPackets(nms)
    }

    fun send(player: EntityPlayer) {
        player.sendPackets(nms)
    }

    @JvmName("broadcastPlayers")
    inline fun broadcast(players: Iterable<Player>) {
        players.forEach(::send)
    }

    @JvmName("broadcastNMSP")
    inline fun broadcast(players: Iterable<EntityPlayer>) {
        players.forEach(::send)
    }

    fun broadcast(world: World) {
        world.players.forEach {
            it.sendPackets(nms)
        }
    }

    fun broadcast(world: net.minecraft.server.v1_12_R1.World) {
        world.players.forEach {
            if (it is EntityPlayer)
                it.sendPackets(nms)
        }
    }

    @JvmName("broadcastWorlds")
    inline fun broadcast(worlds: Iterable<World>) {
        worlds.forEach(this::broadcast)
    }

    @JvmName("broadcastNMSWorlds")
    inline fun broadcast(worlds: Iterable<net.minecraft.server.v1_12_R1.World>) {
        worlds.forEach(this::broadcast)
    }
}