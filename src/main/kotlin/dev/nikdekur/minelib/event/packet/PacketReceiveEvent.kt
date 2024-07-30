package dev.nikdekur.minelib.event.packet

import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.nms.protocol.Sender


class PacketReceiveEvent(val sender: Player, channel: Channel, packet: Packet<*>) : PacketEvent(Sender.CLIENT, sender, channel, packet) {


    override fun getHandlers() = HANDLERS
    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLERS
    }


    var cancel = false
    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}