package dev.nikdekur.minelib.event.packet.early

import io.netty.channel.Channel
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.entity.Player
import dev.nikdekur.minelib.event.packet.PacketEvent
import dev.nikdekur.minelib.nms.protocol.Sender

abstract class EarlyPacketEvent(
    senderSide: Sender,
    player: Player?,
    channel: Channel,
    packet: Packet<*>
) : PacketEvent(senderSide, player, channel, packet)