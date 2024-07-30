@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.nms.packet

import dev.nikdekur.ndkore.ext.r_SetField
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Location

object PacketBuilder {
    object Entity {

        object Data {

            /**
             * Creates a [PacketPlayOutEntityMetadata] packet to update the entity's data short.
             *
             * @param entityId the entity to update the data of
             * @param data the data to update the entity with
             * @return the created packet
             */
            fun updateShort(entityId: Int, data: DataWatcher): Packet {
                val nms = PacketPlayOutEntityMetadata(entityId, data, false)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityMetadata] packet to update the entity's data long.
             *
             * @param entityId the entity to update the data of
             * @param data the data to update the entity with
             * @return the created packet
             */
            fun updateLong(entityId: Int, data: DataWatcher): Packet {
                val nms = PacketPlayOutEntityMetadata(entityId, data, true)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityMetadata] packet to change the entity's data short.
             *
             * @param entity the entity to change the data of
             * @param func the function to change the data
             * @return the created packet
             */
            fun updateShort(entity: net.minecraft.server.v1_12_R1.Entity, func: DataWatcher.() -> Unit = {}): Packet {
                val data = entity.dataWatcher
                data.func()
                return updateShort(entity.id, data)
            }

            /**
             * Creates a [PacketPlayOutEntityMetadata] packet to change the entity's data long.
             *
             * @param entity the entity to change the data of
             * @param func the function to change the data
             * @return the created packet
             */
            fun updateLong(entity: net.minecraft.server.v1_12_R1.Entity, func: DataWatcher.() -> Unit = {}): Packet {
                val data = entity.dataWatcher
                data.func()
                return updateLong(entity.id, data)
            }
        }

        object Status {

            /**
             * Creates a [PacketPlayOutEntityStatus] packet to send the status of an entity.
             *
             * See [**statuses code**](https://wiki.vg/Entity_statuses) for [status] information.
             *
             * @param entityId the entity to send the status of
             * @param status the status code
             * @return the created packet
             */
            fun status(entityId: Int, status: Byte): Packet {
                val nms = PacketPlayOutEntityStatus()
                nms.r_SetField("a", entityId)
                nms.r_SetField("b", status)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityStatus] packet to send the death status of an entity.
             *
             * The packet plays the death sound and death animation.
             *
             * @param entityId the entity to send the death status of
             * @return the created packet
             * @see status
             */
            inline fun death(entityId: Int): Packet {
                return status(entityId, 3)
            }
        }

        object Animation {

            /**
             * Creates a [PacketPlayOutAnimation] packet to send an animation of an entity.
             *
             * Animation Codes:
             * - 0: Swing main hand
             * - 1: Take damage
             * - 2: Leave bed
             * - 3: Swing offhand
             * - 4: Critical effect
             * - 5: Magic critical effect
             *
             * @param entityId the entity to send the animation of
             * @param animation the animation code
             * @return the created packet
             * @throws IllegalArgumentException if the animation code is not in range 0-5 (inclusive)
             */
            fun animate(entityId: Int, animation: Int): Packet {
                require(animation in 0..5) { "Animation must be in range 0-5 (inclusive)" }
                val nms = PacketPlayOutAnimation()
                nms.r_SetField("a", entityId)
                nms.r_SetField("b", animation)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of swinging the main hand.
             *
             * Create animation with code 0 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun swingMainHand(entityId: Int): Packet {
                return animate(entityId, 0)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of taking damage.
             *
             * Create animation with code 1 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun takeDamage(entityId: Int): Packet {
                return animate(entityId, 1)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of leaving bed.
             *
             * Create animation with code 2 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun leaveBed(entityId: Int): Packet {
                return animate(entityId, 2)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of swinging the offhand.
             *
             * Create animation with code 3 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun swingOffHand(entityId: Int): Packet {
                return animate(entityId, 3)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of a critical effect.
             *
             * Create animation with code 4 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun criticalEffect(entityId: Int): Packet {
                return animate(entityId, 4)
            }

            /**
             * Creates a [PacketPlayOutAnimation] packet to send the animation of a magic-critical effect.
             *
             * Create animation with code 5 via [animate].
             *
             * @param entityId the entity to send the animation of
             * @return the created packet
             */
            inline fun magicCriticalEffect(entityId: Int): Packet {
                return animate(entityId, 5)
            }
        }


        object Block {

            /**
             * Creates a [PacketPlayOutBlockBreakAnimation] packet to set the break stage of a block.
             *
             * @param breakerId the id of the entity breaking the block
             * @param location the location of the block to set progress
             * @param stage the stage of the break (0-9)
             * @return the created packet
             * @throws IllegalArgumentException if the stage is not in range 0-9 (inclusive)
             */
            fun breakStage(breakerId: Int, location: Location, stage: Int): Packet {
                require(stage in 0..9) { "Stage must be in range 0-9 (inclusive)" }
                val nms = PacketPlayOutBlockBreakAnimation(breakerId, BlockPosition(location.blockX, location.blockY, location.blockZ), stage)
                return Packet(nms)
            }
        }


        object Move {

            /**
             * Creates a [PacketPlayOutEntityTeleport] packet to teleport an entity.
             *
             * @param entityId the entity to teleport
             * @param location the new location to teleport the entity to
             * @param isOnGround if the entity is on the ground (default true)
             * @return the created packet
             */
            fun teleport(entityId: Int, location: Location, isOnGround: Boolean = true): Packet {
                val nms = PacketPlayOutEntityTeleport()
                nms.r_SetField("a", entityId)
                nms.r_SetField("b", location.x)
                nms.r_SetField("c", location.y)
                nms.r_SetField("d", location.z)
                nms.r_SetField("e", (location.yaw * 256.0f / 360.0f).toInt().toByte())
                nms.r_SetField("f", (location.pitch * 256.0f / 360.0f).toInt().toByte())
                nms.r_SetField("g", isOnGround)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityTeleport] packet to teleport an entity.
             *
             * Entity would be teleported to the specified in [entity] location.
             *
             * @param entity the entity to teleport
             * @return the created packet
             */
            fun teleport(entity: net.minecraft.server.v1_12_R1.Entity): Packet {
                return Packet(PacketPlayOutEntityTeleport(entity))
            }

            /**
             * Creates a [PacketPlayOutEntityTeleport] packet to teleport an entity.
             *
             * Entity would be teleported to the specified in [location] if the entity is not already there.
             *
             * If you want to bypass this check, use [teleport] instead.
             *
             * @param entity the entity to teleport
             * @param location the new location to teleport the entity to
             * @return the created packet or null if the entity is already at the location
             */
            fun teleport(entity: net.minecraft.server.v1_12_R1.Entity, location: Location): Packet? {
                val newX = location.x
                val newY = location.y
                val newZ = location.z
                val newYaw = location.yaw
                val newPitch = location.pitch
                if (entity.locX != newX || entity.locY != newY || entity.locZ != newZ || entity.yaw != newYaw || entity.pitch != newPitch) {
                    entity.setLocation(newX, newY, newZ, newYaw, newPitch)
                    return teleport(entity)
                }
                return null
            }
        }


        object Appearance {

            /**
             * Creates a [PacketPlayOutEntityEquipment] packet to set an item in the entity's equipment.
             *
             * @param entityId the entity to set the equipment of
             * @param slot the slot to set the item in
             * @param item the item to set in the slot
             * @return the created packet
             */
            fun equipment(entityId: Int, slot: EnumItemSlot, item: ItemStack): Packet {
                val nms = PacketPlayOutEntityEquipment(entityId, slot, item)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityEquipment] packet to set multiple items in the entity's equipment.
             *
             * @param entityId the entity to set the equipment of
             * @param map the map of slots to items to set
             * @return the created packet
             */
            fun equipment(entityId: Int, map: Map<EnumItemSlot, ItemStack>): Packet {
                val nms = map.map { (slot, item) ->
                    PacketPlayOutEntityEquipment(entityId, slot, item)
                }
                return Packet(nms)
            }
        }

        object View {

            /**
             * Creates a [PacketPlayOutSpawnEntity] packet to spawn an entity.
             *
             * @param entity the entity to spawn
             * @param objectType the object type of the entity
             * @param param the param of the entity
             * @param position the position of the entity
             * @return the created packet
             */
            fun spawn(entity: net.minecraft.server.v1_12_R1.Entity, objectType: Int, param: Int = 0, position: BlockPosition? = null): Packet {
                val nms = PacketPlayOutSpawnEntity(entity, objectType, param)
                if (position != null) {
                    nms.r_SetField("c", position.x)
                    nms.r_SetField("d", position.y)
                    nms.r_SetField("e", position.z)
                }
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutSpawnEntityLiving] packet to spawn a living entity.
             *
             * Creates the living entity on the client side, it doesn't exist on the server side.
             *
             * To
             *
             * @param entity the entity to spawn
             * @return the created packet
             */
            fun spawnLiving(entity: EntityLiving): Packet {
                val nms = PacketPlayOutSpawnEntityLiving(entity)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutSpawnEntityLiving] packet to spawn multiple living entities.
             *
             * @param entities the entities to spawn
             * @return the created packet
             */
            fun spawnLiving(entities: Iterable<EntityLiving>): Packet {
                val nms = entities.map(::PacketPlayOutSpawnEntityLiving)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutNamedEntitySpawn] packet to spawn a named entity.
             *
             * @param human the human to spawn
             * @return the created packet
             * @see spawnLiving
             */
            fun spawnNamed(human: EntityHuman): Packet {
                val nms = PacketPlayOutNamedEntitySpawn(human)
                return Packet(nms)
            }


            /**
             * Creates a [PacketPlayOutEntity] packet to show an entity.
             *
             * If the entity is not spawned, it will not be shown. Use [spawnLiving] to spawn the entity.
             *
             * @param entityId the entity to show
             * @return the created packet
             */
            fun show(entityId: Int): Packet {
                val nms = PacketPlayOutEntity(entityId)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntity] packet to show multiple entities.
             *
             * If the entity is not spawned, it will not be shown. Use [spawnLiving] to spawn the entity.
             *
             * @param entityIds the entities to show
             * @return the created packet
             */
            fun show(entityIds: Iterable<Int>): Packet {
                val nms = entityIds.map {
                    PacketPlayOutEntity(it)
                }
                return Packet(nms)
            }




            /**
             * Creates a [PacketPlayOutEntityDestroy] packet to despawn an entity.
             *
             * @param entityId the entity to despawn
             * @return the created packet
             */
            fun remove(entityId: Int): Packet {
                val nms = PacketPlayOutEntityDestroy(entityId)
                return Packet(nms)
            }

            /**
             * Creates a [PacketPlayOutEntityDestroy] packet to despawn multiple entities.
             *
             * @param entityIds the entities to despawn
             * @return the created packet
             */
            fun remove(entityIds: Iterable<Int>): Packet {
                val nms = entityIds.map {
                    PacketPlayOutEntityDestroy(it)
                }
                return Packet(nms)
            }
        }

    }
}