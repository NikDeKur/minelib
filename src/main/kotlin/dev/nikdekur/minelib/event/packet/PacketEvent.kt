package dev.nikdekur.minelib.event.packet

import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import dev.nikdekur.minelib.nms.protocol.Sender

abstract class PacketEvent(
    val senderSide: Sender,
    val player: Player?,
    val channel: Channel,
    var packet: Packet<*>
) : Event(true), Cancellable