package dev.nikdekur.minelib.event.packet.early

import dev.nikdekur.minelib.event.packet.PacketEvent
import dev.nikdekur.minelib.nms.protocol.Sender
import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player

abstract class EarlyPacketEvent(
    senderSide: Sender,
    player: Player?,
    channel: Channel,
    packet: Packet<*>
) : PacketEvent(senderSide, player, channel, packet)