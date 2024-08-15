package dev.nikdekur.minelib.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.bukkit.util.Vector as BVector

object Serials {
    object Vector : KSerializer<BVector> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Vector") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
        }

        override fun serialize(encoder: Encoder, value: BVector) {
            encoder.encodeStructure(descriptor) {
                encodeDoubleElement(descriptor, 0, value.x)
                encodeDoubleElement(descriptor, 1, value.y)
                encodeDoubleElement(descriptor, 2, value.z)
            }
        }

        override fun deserialize(decoder: Decoder): BVector {
            return try {
                val stringValue = decoder.decodeString()
                parseFromString(stringValue)
            } catch (_: Exception) {
                decoder.decodeStructure(descriptor) {
                    val x = decodeDoubleElement(descriptor, 0)
                    val y = decodeDoubleElement(descriptor, 1)
                    val z = decodeDoubleElement(descriptor, 2)
                    BVector(x, y, z)
                }
            }
        }

        private fun parseFromString(value: String): BVector {
            val parts = value.split(",")
            require(parts.size == 3) { "Invalid format for Vector string" }
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            return BVector(x, y, z)
        }
    }
}