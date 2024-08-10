package dev.nikdekur.minelib.v1_12_R1.packet.early

import dev.nikdekur.minelib.v1_12_R1.nms.protocol.Sender
import dev.nikdekur.minelib.v1_12_R1.packet.PacketEvent
import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player

abstract class EarlyPacketEvent(
    senderSide: Sender,
    player: Player?,
    channel: Channel,
    packet: Packet<*>
) : PacketEvent(senderSide, player, channel, packet)