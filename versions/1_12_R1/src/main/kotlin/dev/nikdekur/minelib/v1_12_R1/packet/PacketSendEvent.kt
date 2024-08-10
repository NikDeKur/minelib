package dev.nikdekur.minelib.v1_12_R1.packet

import dev.nikdekur.minelib.v1_12_R1.nms.protocol.Sender
import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList


class PacketSendEvent(val receiver: Player, channel: Channel, packet: Packet<*>) : PacketEvent(Sender.SERVER, receiver, channel, packet) {


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