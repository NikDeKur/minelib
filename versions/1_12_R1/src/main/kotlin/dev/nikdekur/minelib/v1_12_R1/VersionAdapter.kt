package dev.nikdekur.minelib.v1_12_R1

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.pentity.PersonalEntityService
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.utils.requireMainThread
import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.nms.protocol.InjectProtocolService
import dev.nikdekur.minelib.v1_12_R1.pentity.PersonalEntityServiceImpl
import dev.nikdekur.ndkore.service.bind
import dev.nikdekur.ndkore.service.qualify
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

// Class is found in Runtime so there is no usage of it in the code
@Suppress("unused", "ClassName", "kotlin:S101")
open class VersionAdapter_v1_12_R1(
    override val app: PluginApplication
) : PluginService(), VersionAdapter {

    override suspend fun onEnable() {
        listOf(
            PersonalEntityServiceImpl(app).also(app::registerComponent)
                    bind PersonalEntityService::class
                    qualify MineLib.Qualifier,

            InjectProtocolService(app).also(app::registerComponent)
                    bind InjectProtocolService::class
                    qualify MineLib.Qualifier
        ).forEach {
            app.servicesManager.registerService(it)
        }
    }

    override fun expandBB(entity: Entity, x: Float, y: Float, z: Float) {
        val entity = entity.nms
        val boundingBox = entity.boundingBox

        val x0 = boundingBox.a
        val y0 = boundingBox.b
        val z0 = boundingBox.c
        val x1 = boundingBox.d
        val y1 = boundingBox.e
        val z1 = boundingBox.f
        val aabb = AxisAlignedBB(
            x0 - x,
            y0,
            z0 - z,

            x1 + x,
            y1 + y,
            z1 + z
        )

        entity.a(aabb)
    }


    override fun setHighWalkSpeed(player: Player, speed: Float) {
        requireMainThread("tracker update")
        val handle = player.nms
        handle.abilities.walkSpeed = speed / 2f
        handle.updateAbilities()
    }

    override fun setHighFlySpeed(player: Player, speed: Float) {
        requireMainThread("tracker update")
        val handle = player.nms
        handle.abilities.flySpeed = speed / 2f
        handle.updateAbilities()
    }
}