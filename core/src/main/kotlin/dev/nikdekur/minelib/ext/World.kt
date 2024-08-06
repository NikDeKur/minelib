@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.utils.requireMainThread
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World

//package org.ndk.godsimulator.extension
//
//import com.google.common.base.Predicates
//import net.minecraft.server.v1_12_R1.AxisAlignedBB
//import net.minecraft.server.v1_12_R1.Vec3D
//import org.apache.commons.lang.Validate
//import org.bukkit.Location
//import org.bukkit.World
//import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
//import org.bukkit.entity.Entity
//import org.bukkit.util.Vector
//import org.ndk.godsimulator.newbukkit.FluidCollisionMode
//import org.ndk.godsimulator.newbukkit.util.BoundingBox
//import org.ndk.godsimulator.newbukkit.util.RayTraceResult
//import org.spigotmc.AsyncCatcher
//import java.util.function.Predicate
//
//fun World.getNearbyEntities(boundingBox: BoundingBox): Collection<Entity> {
//    return this.getNearbyEntities(boundingBox, null as Predicate<Entity>?)
//}
//
//fun World.getNearbyEntities(boundingBox: BoundingBox, filter: Predicate<Entity>?): Collection<Entity> {
//    AsyncCatcher.catchOp("getNearbyEntities")
//    Validate.notNull(boundingBox, "Bounding box is null!")
//    val bb = AxisAlignedBB(
//        boundingBox.minX,
//        boundingBox.minY,
//        boundingBox.minZ,
//        boundingBox.maxX,
//        boundingBox.maxY,
//        boundingBox.maxZ
//    )
//    val entityList: List<net.minecraft.server.v1_12_R1.Entity> =
//        this.handle.getEntities(null as net.minecraft.server.v1_12_R1.Entity?, bb, Predicates.alwaysTrue())
//    val bukkitEntityList = ArrayList<Entity>(entityList.size)
//    val var7: Iterator<*> = entityList.iterator()
//
//    while (true) {
//        var bukkitEntity: CraftEntity
//        do {
//            if (!var7.hasNext()) {
//                return bukkitEntityList
//            }
//
//            val entity = var7.next() as net.minecraft.server.v1_12_R1.Entity
//            bukkitEntity = entity.bukkitEntity
//        } while (filter != null && !filter.test(bukkitEntity))
//
//        bukkitEntityList.add(bukkitEntity)
//    }
//}
//
//
//fun World.rayTraceEntities(start: Location, direction: Vector, maxDistance: Double): RayTraceResult? {
//    return this.rayTraceEntities(start, direction, maxDistance, null as Predicate<Entity>?)
//}
//
//fun World.rayTraceEntities(start: Location, direction: Vector, maxDistance: Double, raySize: Double): RayTraceResult? {
//    return this.rayTraceEntities(start, direction, maxDistance, raySize, null as Predicate<Entity>?)
//}
//
//fun World.rayTraceEntities(
//    start: Location,
//    direction: Vector,
//    maxDistance: Double,
//    filter: Predicate<Entity>?
//): RayTraceResult? {
//    return this.rayTraceEntities(start, direction, maxDistance, 0.0, filter)
//}
//
//fun World.rayTraceEntities(
//    start: Location,
//    direction: Vector,
//    maxDistance: Double,
//    raySize: Double,
//    filter: Predicate<Entity>?
//): RayTraceResult? {
//    Validate.notNull(start, "Start location is null!")
//    Validate.isTrue(this == start.world, "Start location is from different world!")
//    start.checkFinite()
//    Validate.notNull(direction, "Direction is null!")
//    direction.checkFinite()
//    Validate.isTrue(direction.lengthSquared() > 0.0, "Direction's magnitude is 0!")
//    if (maxDistance < 0.0) {
//        return null
//    } else {
//        val startPos = start.toVector()
//        val dir = direction.clone().normalize().multiply(maxDistance)
//        val aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize)
//        val entities: Collection<Entity> = this.getNearbyEntities(aabb, filter)
//        var nearestHitEntity: Entity? = null
//        var nearestHitResult: RayTraceResult? = null
//        var nearestDistanceSq = Double.MAX_VALUE
//        val var17: Iterator<*> = entities.iterator()
//
//        while (var17.hasNext()) {
//            val entity = var17.next() as Entity
//            val boundingBox: BoundingBox = entity.boundingBox.expand(raySize, raySize, raySize)
//            val hitResult = boundingBox.rayTrace(startPos, direction, maxDistance)
//            if (hitResult != null) {
//                val distanceSq = startPos.distanceSquared(hitResult.getHitPosition())
//                if (distanceSq < nearestDistanceSq) {
//                    nearestHitEntity = entity
//                    nearestHitResult = hitResult
//                    nearestDistanceSq = distanceSq
//                }
//            }
//        }
//
//        return if (nearestHitEntity == null) null else RayTraceResult(
//            nearestHitResult!!.getHitPosition(),
//            nearestHitEntity,
//            nearestHitResult.hitBlockFace
//        )
//    }
//}
//
//fun World.rayTraceBlocks(start: Location, direction: Vector, maxDistance: Double): RayTraceResult? {
//    return this.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, false)
//}
//
//fun World.rayTraceBlocks(
//    start: Location,
//    direction: Vector,
//    maxDistance: Double,
//    fluidCollisionMode: FluidCollisionMode
//): RayTraceResult? {
//    return this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, false)
//}
//
//fun World.rayTraceBlocks(
//    start: Location,
//    direction: Vector,
//    maxDistance: Double,
//    fluidCollisionMode: FluidCollisionMode,
//    ignorePassableBlocks: Boolean
//): RayTraceResult? {
//    Validate.notNull(start, "Start location is null!")
//    Validate.isTrue(this == start.world, "Start location is from different world!")
//    start.checkFinite()
//    Validate.notNull(direction, "Direction is null!")
//    direction.checkFinite()
//    Validate.isTrue(direction.lengthSquared() > 0.0, "Direction's magnitude is 0!")
//    Validate.notNull(fluidCollisionMode, "Fluid collision mode is null!")
//    if (maxDistance < 0.0) {
//        return null
//    } else {
//        val dir = direction.clone().normalize().multiply(maxDistance)
//        val startPos = Vec3D(start.x, start.y, start.z)
//        val endPos = Vec3D(start.x + dir.x, start.y + dir.y, start.z + dir.z)
//        val nmsHitResult: net.minecraft.server.v1_12_R1. = this.handle.clip(
//            ClipContext(
//                startPos,
//                endPos,
//                if (ignorePassableBlocks) net.minecraft.world.level.ClipContext.Block.COLLIDER else net.minecraft.world.level.ClipContext.Block.OUTLINE,
//                CraftFluidCollisionMode.toNMS(fluidCollisionMode),
//                null as net.minecraft.world.entity.Entity?
//            )
//        )
//        return CraftRayTraceResult.fromNMS(this, nmsHitResult)
//    }
//}
//
//fun World.rayTrace(
//    start: Location,
//    direction: Vector,
//    maxDistance: Double,
//    fluidCollisionMode: FluidCollisionMode,
//    ignorePassableBlocks: Boolean,
//    raySize: Double,
//    filter: Predicate<Entity>?
//): RayTraceResult? {
//    val blockHit = this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks)
//    var startVec: Vector? = null
//    var blockHitDistance = maxDistance
//    if (blockHit != null) {
//        startVec = start.toVector()
//        blockHitDistance = startVec.distance(blockHit.getHitPosition())
//    }
//
//    val entityHit = this.rayTraceEntities(start, direction, blockHitDistance, raySize, filter)
//    if (blockHit == null) {
//        return entityHit
//    } else if (entityHit == null) {
//        return blockHit
//    } else {
//        val entityHitDistanceSquared = startVec!!.distanceSquared(entityHit.getHitPosition())
//        return if (entityHitDistanceSquared < blockHitDistance * blockHitDistance) entityHit else blockHit
//    }
//}
//
//inline val Entity.boundingBox: BoundingBox
//    get() {
//        val bb = (this as CraftEntity).handle.boundingBox
//        return BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f)
//    }

inline fun World.location(x: Double, y: Double, z: Double): Location {
    return Location(this, x, y, z)
}
inline fun World.location(x: Double, y: Double, z: Double, yaw: Float, pitch: Float): Location {
    return Location(this, x, y, z, yaw, pitch)
}
inline fun World.location(x: Int, y: Int, z: Int): Location {
    return Location(this, x.toDouble(), y.toDouble(), z.toDouble())
}
inline fun World.location(x: Int, y: Int, z: Int, yaw: Float, pitch: Float): Location {
    return Location(this, x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)
}


inline fun World.fillArea(type: Material, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
    require(type.isBlock) { "Material must be block" }
    requireMainThread("fillArea")
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                val blockAt = this.getBlockAt(x, y, z)
                if (blockAt.type == type) continue
                blockAt.type = type
            }
        }
    }
}


