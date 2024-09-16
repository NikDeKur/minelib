@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.i18n.msg.MSGNameHolder
import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.minelib.rpg.RPGService
import dev.nikdekur.ndkore.ext.randDouble
import dev.nikdekur.ndkore.ext.randFloat
import dev.nikdekur.ndkore.ext.randInt
import dev.nikdekur.ndkore.ext.toBooleanSmartOrNull
import dev.nikdekur.ndkore.`interface`.Snowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

val random = java.util.Random()


abstract class RPGStat<T> : Snowflake<String>, MSGNameHolder where T : Comparable<T>, T : Any {

    /**
     * The default value of the stat
     *
     * Will be used when the stat is not set
     */
    abstract val defaultValue: T

    /**
     * The static value of the stat
     *
     * Will always be used and added to the value
     *
     * For example, the static value of the health stat is 20,
     * and the value is 10, the final value will be 30.
     *
     * @see defaultValue
     */
    abstract val staticValue: T

    /**
     * The class of the value of the stat
     */
    abstract val valueClass: KClass<T>

    /**
     * The name of the stat message
     */
    abstract val nameBuffMSG: MessageReference

    /**
     * Check that given value is an instance of the stat value
     */
    open fun isInstance(value: Any): Boolean {
        return valueClass.isInstance(value)
    }

    /**
     * Perform the addition of two values
     *
     * @param value the first value
     * @param other the second value
     * @return the result of the addition (merge)
     */
    abstract fun plus(value: T, other: T): T

    /**
     * Perform the subtraction of two values
     *
     * @param value the first value
     * @param other the second value
     * @return the result of the subtraction
     */
    abstract fun minus(value: T, other: T): T

    /**
     * Returns the random value between min and max
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return the random value between min and max
     */
    abstract fun range(min: T, max: T): T

    /**
     * Read (decode) the value from the string
     *
     * @param value the string value
     * @return the value or null if the value is not valid
     */
    abstract fun read(value: String): T?

    /**
     * The kotlinx-serializer that will be used to serialize the value
     *
     * @return the kotlinx-serializer
     */
    abstract fun valueSerializer(): KSerializer<T>

    override fun toString(): String {
        return "RPGStat(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? RPGStat<*> ?: return false
        return o.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }





    class Serializer(val service: RPGService) : KSerializer<RPGStat<*>> {
        override val descriptor = PrimitiveSerialDescriptor("RPGStat", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: RPGStat<*>) {
            println("Serializing stat ${value.id}")
            encoder.encodeString(value.id)
        }

        override fun deserialize(decoder: Decoder): RPGStat<*> {
            val id = decoder.decodeString()
            return service.getStat(id) ?: error("Stat $id not found")
        }

        override fun toString(): String {
            return "RPGStat.Serializer(service=$service)"
        }
    }
}

@Serializable
abstract class RPGDoubleStat : RPGStat<Double>() {
    override val defaultValue = 0.0
    override val staticValue = 0.0

    override val valueClass: KClass<Double> = Double::class
    override fun plus(value: Double, other: Double): Double {
        return value + other
    }
    override fun minus(value: Double, other: Double): Double {
        return value - other
    }

    override fun range(min: Double, max: Double): Double {
        return random.randDouble(min, max)
    }

    override fun read(value: String): Double? {
        return value.toDoubleOrNull()
    }

    override fun valueSerializer() = Double.serializer()
}

@Serializable
abstract class RPGFloatStat : RPGStat<Float>() {
    override val defaultValue = 0f
    override val staticValue = 0f

    override val valueClass: KClass<Float> = Float::class
    override fun plus(value: Float, other: Float): Float {
        return value + other
    }
    override fun minus(value: Float, other: Float): Float {
        return value - other
    }

    override fun range(min: Float, max: Float): Float {
        return random.randFloat(min, max)
    }

    override fun read(value: String): Float? {
        return value.toFloatOrNull()
    }

    override fun valueSerializer() = Float.serializer()
}

@Serializable
abstract class RPGIntStat : RPGStat<Int>() {
    override val defaultValue = 0
    override val staticValue = 0

    override val valueClass: KClass<Int> = Int::class
    override fun plus(value: Int, other: Int): Int {
        return value + other
    }
    override fun minus(value: Int, other: Int): Int {
        return value - other
    }

    override fun range(min: Int, max: Int): Int {
        return random.randInt(min, max)
    }

    override fun read(value: String): Int? {
        return value.toIntOrNull()
    }

    override fun valueSerializer() = Int.serializer()
}

abstract class RPGBigIntegerStat : RPGStat<java.math.BigInteger>() {
    override val defaultValue = java.math.BigInteger.ZERO
    override val staticValue = java.math.BigInteger.ZERO

    override val valueClass: KClass<java.math.BigInteger> = java.math.BigInteger::class
    override fun plus(value: java.math.BigInteger, other: java.math.BigInteger): java.math.BigInteger {
        return value + other
    }
    override fun minus(value: java.math.BigInteger, other: java.math.BigInteger): java.math.BigInteger {
        return value - other
    }

    override fun range(min: java.math.BigInteger, max: java.math.BigInteger): java.math.BigInteger {
        return random.randDouble(min.toDouble(), max.toDouble()).toBigDecimal().toBigInteger()
    }

    override fun read(value: String): java.math.BigInteger? {
        return value.toBigIntegerOrNull()
    }

    override fun valueSerializer() = object : KSerializer<java.math.BigInteger> {
        override val descriptor = PrimitiveSerialDescriptor("java.math.BigInteger", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: java.math.BigInteger) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): java.math.BigInteger {
            return decoder.decodeString().toBigInteger()
        }
    }
}

@Serializable
abstract class RPGBooleanStat : RPGStat<Boolean>() {
    override val defaultValue = false
    override val staticValue = false
    override val valueClass: KClass<Boolean> = Boolean::class

    override fun plus(value: Boolean, other: Boolean): Boolean {
        return value || other
    }

    override fun range(min: Boolean, max: Boolean): Boolean {
        throw UnsupportedOperationException("Boolean stat can't be ranged")
    }

    override fun minus(value: Boolean, other: Boolean): Boolean {
        return value && other
    }

    override fun read(value: String): Boolean? {
        return value.toBooleanSmartOrNull()
    }

    override fun valueSerializer() = Boolean.serializer()
}
