package dev.nikdekur.minelib.event.packet.early

import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.nms.protocol.Sender


class EarlyPacketSendEvent(channel: Channel, packet: Packet<*>) : EarlyPacketEvent(Sender.SERVER, null, channel, packet) {


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