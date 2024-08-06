package dev.nikdekur.minelib.nms.track

import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.Packet

class FakePlayerPersonalTrackerEntry(
    viewer: EntityPlayer,
    entity: EntityPlayer,
    renderDistance: Int,
    spigotViewDistance: Int,
    k: Int
) : PersonalTrackerEntry(viewer, entity, renderDistance, spigotViewDistance, k, false) {

    override fun broadcastIncludingSelf(packet: Packet<*>?) {
        broadcast(packet)
    }

}
