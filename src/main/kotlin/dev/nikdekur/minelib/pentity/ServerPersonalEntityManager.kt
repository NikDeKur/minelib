package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.event.packet.PacketReceiveEvent
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.nms.protocol.ProtocolModule
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.ndkore.ext.distanceSquared
import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.ext.sqrt
import dev.nikdekur.ndkore.module.dependencies
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity.EnumEntityUseAction
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ServerPersonalEntityManager : MineLibModule, Listener {
    override val dependencies = dependencies {
        after(ProtocolModule::class.java)
    }

    val managers = ConcurrentHashMap<UUID, PersonalEntityManager>()

    fun getManager(world: World): PersonalEntityManager {
        return managers.computeIfAbsent(world.uid) { PersonalEntityManager(world) }
    }

    /**
     * The maximum distance (squared) for interacting with the entity.
     *
     * Now it's three blocks.
     *
     * If the distance between the player and the entity is greater than this value, the interaction will be ignored.
     */
    const val MAX_INTERACT_DISTANCE_SQUARED = 9

    /**
     * The distance (squared) for reporting the player interaction with the entity.
     *
     * Now it's 10 blocks.
     *
     * If the distance between the player and the entity is greater or equal to this value,
     * the interaction will be ignored and reported to the console.
     */
    const val REPORT_INTERACT_DISTANCE_SQUARED = 100

    /**
     * Check if the player can interact with the entity.
     *
     * The player can interact with the entity if:
     * - The player and the entity are in the same world.
     * - The distance between the player and the entity is less or equal to [MAX_INTERACT_DISTANCE_SQUARED].
     *
     * If the interacting is suspicious, it will be reported to the console.
     */
    fun checkInteractLegality(player: Player, entity: PersonalEntity, entityId: Int): Boolean {
        val playerLocation = player.location

        // Don't do checking for worlds, because if an entity is found, it's already in the same world

        // No reporting because of asynchronous nature
        val nmsEntity = entity.getEntity(player, entityId) ?: return false
        val distanceSquared = distanceSquared(
            playerLocation.x, playerLocation.y, playerLocation.z,
            nmsEntity.locX, nmsEntity.locY, nmsEntity.locZ
        )
        if (distanceSquared >= REPORT_INTERACT_DISTANCE_SQUARED) {
            bLogger.warning("Player ${player.name} (${player.uniqueId}) tried to interact with personal entity '$entity' from distance ${distanceSquared.sqrt()} blocks!")
            return false
        }
        if (distanceSquared > MAX_INTERACT_DISTANCE_SQUARED) return false

        return true
    }

    @EventHandler // Note: ASYNC
    fun onPacketReceive(event: PacketReceiveEvent) {
        val packet = event.packet as? PacketPlayInUseEntity ?: return
        val entityId = packet.r_GetField("a").value as? Int ?: return
        val player = event.player ?: return

        val manager = getManager(player.world)

        val entity = manager.getEntityByPersonalEntity(entityId) ?: return
        if (!checkInteractLegality(player, entity, entityId)) return

        val type = packet.a()

        app.scheduler.runTask {
            when (type) {
                EnumEntityUseAction.ATTACK -> entity.onLeftClick(player)
                EnumEntityUseAction.INTERACT -> entity.onRightClick(player)
                EnumEntityUseAction.INTERACT_AT -> {
                    val pos = packet.c() ?: return@runTask
                    entity.onRightClickAt(player, pos)
                }
                else -> { /* Do Nothing */ }
            }
        }
    }

    /**
     * Fix teleport tracking with updating tracking after teleport
     *
     * This is needed because of the bug in NMS, where the player tracker is not updated after teleport
     */
    @EventHandler
    fun teleportTrackerFix(event: PlayerTeleportEvent) {
        val player = event.player
        debug("Teleport tracker fix for ${player.name}")
        app.scheduler.runTaskLater(5) {
            getManager(player.world).updateTracking(player)
        }
    }

//    @EventHandler
//    fun onJoin(event: PlayerJoinEvent) {
//        val player = event.player
//        plugin.scheduler.runTask {
//            getManager(player.world).updateTracking(player)
//        }
//    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        getManager(player.world).removeTracking(player)
    }

    // TODO: Check compatibility with a changing world




    override fun onUnload() {
        managers.values.forEach(PersonalEntityManager::onUnload)
        managers.clear()
    }
}