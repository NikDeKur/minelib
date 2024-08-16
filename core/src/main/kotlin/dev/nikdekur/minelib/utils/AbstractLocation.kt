@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

@Serializable(AbstractLocation.Serializer::class)
data class AbstractLocation(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float = 0f,
    var pitch: Float = 0f
) : Cloneable {

    fun add(x: Double, y: Double, z: Double): AbstractLocation {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun add(vector: Vector): AbstractLocation {
        return add(vector.x, vector.y, vector.z)
    }

    fun subtract(x: Double, y: Double, z: Double): AbstractLocation {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun subtract(vector: Vector): AbstractLocation {
        return subtract(vector.x, vector.y, vector.z)
    }

    inline fun toVector(): Vector {
        return Vector(x, y, z)
    }

    inline fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }



    object Serializer : KSerializer<AbstractLocation> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractLocation") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
            element<Float>("yaw")
            element<Float>("pitch")
        }

        override fun serialize(encoder: Encoder, value: AbstractLocation) {
            encoder.encodeStructure(descriptor) {
                encodeDoubleElement(descriptor, 0, value.x)
                encodeDoubleElement(descriptor, 1, value.y)
                encodeDoubleElement(descriptor, 2, value.z)
                encodeFloatElement(descriptor, 3, value.yaw)
                encodeFloatElement(descriptor, 4, value.pitch)
            }
        }

        override fun deserialize(decoder: Decoder): AbstractLocation {
            return try {
                val stringValue = decoder.decodeString()
                parseFromString(stringValue)
            } catch (_: Exception) {
                decoder.decodeStructure(descriptor) {
                    var x = 0.0
                    var y = 0.0
                    var z = 0.0
                    var yaw = 0f
                    var pitch = 0f

                    while (true) {
                        when (val index = decodeElementIndex(descriptor)) {
                            0 -> x = decodeDoubleElement(descriptor, 0)
                            1 -> y = decodeDoubleElement(descriptor, 1)
                            2 -> z = decodeDoubleElement(descriptor, 2)
                            3 -> yaw = decodeFloatElement(descriptor, 3)
                            4 -> pitch = decodeFloatElement(descriptor, 4)
                            CompositeDecoder.DECODE_DONE -> break
                            else -> error("Unexpected index: $index")
                        }
                    }

                    AbstractLocation(x, y, z, yaw, pitch)
                }
            }
        }

        private fun parseFromString(value: String): AbstractLocation {
            val parts = value.split(",")
            require(parts.size < 3) { "Invalid format for AbstractLocation string" }
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            val yaw = parts.getOrNull(3)?.toFloat() ?: 0f
            val pitch = parts.getOrNull(4)?.toFloat() ?: 0f
            return AbstractLocation(x, y, z, yaw, pitch)
        }
    }
}
