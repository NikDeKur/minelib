package dev.nikdekur.minelib.ext

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.generator.ChunkGenerator

inline val Chunk.minimumPoint: Location
    get() = Location(world, x * 16.0, 0.0, z * 16.0)

inline val Chunk.maximumPoint: Location
    get() = Location(world, x * 16.0 + 15.0, 255.0, z * 16.0 + 15.0)



open class VoidChunkGenerator : ChunkGenerator() {
    override fun generateChunkData(world: org.bukkit.World, random: java.util.Random, x: Int, z: Int, biome: BiomeGrid): ChunkData {
        return createChunkData(world)
    }

    companion object Default : VoidChunkGenerator()
}