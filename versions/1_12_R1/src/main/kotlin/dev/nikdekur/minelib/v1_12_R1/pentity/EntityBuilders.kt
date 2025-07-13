package dev.nikdekur.minelib.v1_12_R1.pentity

import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.pentity.ClickContext
import dev.nikdekur.minelib.pentity.PersonalEntityBuilder
import dev.nikdekur.minelib.pentity.PersonalEntityContext
import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.nms.track.PersonalEntityTracker
import net.minecraft.server.v1_12_R1.EntityArmorStand
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

object EntityBuilders {

    open class TrackerPersonalEntityBuilder<C : PersonalEntityContext> : PersonalEntityBuilder<C> {
        open var onLeftClick: ClickContext.Left.() -> Unit = {}
        open var onRightClick: ClickContext.Right.() -> Unit = {}
        open var onRightClickAt: ClickContext.RightAt.() -> Unit = {}
        open var entitiesBuilder: (C) -> Collection<Entity> = { emptyList() }


        open fun onLeftClick(action: ClickContext.Left.() -> Unit) {
            onLeftClick = action
        }

        open fun onRightClick(action: ClickContext.Right.() -> Unit) {
            onRightClick = action
        }

        open fun onRightClickAt(action: ClickContext.RightAt.() -> Unit) {
            onRightClickAt = action
        }

        open fun entities(action: C.() -> Collection<Entity>) {
            entitiesBuilder = action
        }

        override fun build(world: World): TrackerPersonalEntity<C> {
            val id = UUID.randomUUID()
            val tracker = PersonalEntityTracker(world.nms)
            return object : TrackerPersonalEntity<C>(id, world, tracker) {

                override fun createEntities(context: C): Collection<Entity> {
                    return entitiesBuilder(context)
                }

                override fun ClickContext.Left.onLeftClick() {
                    return onLeftClick(this)
                }

                override fun ClickContext.Right.onRightClick() {
                    return onRightClick(this)
                }

                override fun ClickContext.RightAt.onRightAtClick() {
                    return onRightClickAt(this)
                }
            }
        }
    }


    open class TrackerHologramEntityBuilder<C : PersonalEntityContext> : TrackerPersonalEntityBuilder<C>() {
        var hologramDataBuilder: ((Player) -> HologramData)? = null

        fun data(builder: (Player) -> HologramData) {
            hologramDataBuilder = builder
        }

        override fun build(world: World): TrackerPersonalEntity<C> {
            entities {
                val builder = hologramDataBuilder
                requireNotNull(builder) { "Data builder must be set for TrackerHologramEntityBuilder" }

                val data = builder(player)

                val location = data.location

                val list = LinkedList<EntityArmorStand>()
                data.text.mapTo(list) { line ->
                    EntityArmorStand(world.nms).apply {
                        customName = line.applyColors()
                        customNameVisible = true
                        isSmall = true
                        isInvisible = true
                        isMarker = true
                        isSilent = true
                        setInvulnerable(true)
                        isNoGravity = true
                    }
                }

                locateArmorStands(location, list)
                list.map { it.bukkitEntity as ArmorStand }
            }

            return super.build(world)
        }

        fun locateArmorStands(location: AbstractLocation, armorStands: Collection<EntityArmorStand>) {
            val locX = location.x
            var locY = location.y
            val locZ = location.z
            for (entity in armorStands.reversed()) {
                entity.setPositionRotation(locX, locY, locZ, location.yaw, location.pitch)
                locY += 0.22
            }
        }


        data class HologramData(
            val location: AbstractLocation,
            val text: List<String>
        )
    }


    fun <C : PersonalEntityContext> custom(func: TrackerPersonalEntityBuilder<C>.() -> Unit): TrackerPersonalEntityBuilder<C> {
        val builder = TrackerPersonalEntityBuilder<C>()
        builder.apply(func)
        return builder
    }

    fun <C : PersonalEntityContext> hologram(func: TrackerHologramEntityBuilder<C>.() -> Unit): TrackerHologramEntityBuilder<C> {
        val builder = TrackerHologramEntityBuilder<C>()
        builder.apply(func)
        return builder
    }
}