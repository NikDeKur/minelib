@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.i18n.msg.MSGNameHolder
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.ndkore.ext.randDouble
import dev.nikdekur.ndkore.ext.randFloat
import dev.nikdekur.ndkore.ext.randInt
import dev.nikdekur.ndkore.ext.toBooleanSmartOrNull
import dev.nikdekur.ndkore.`interface`.Snowflake
import kotlin.reflect.KClass

val random = java.util.Random()

abstract class RPGStat<T> : Snowflake<String>, MSGNameHolder where T : Comparable<T>, T : Any {
    abstract val defaultValue: T
    abstract val clazz: KClass<T>

    abstract val nameBuffMSG: MSGHolder

    fun isInstance(value: Any): kotlin.Boolean {
        return clazz.isInstance(value)
    }

    abstract fun plus(value: T, other: T): T
    abstract fun minus(value: T, other: T): T

    /**
     * Returns the random value between min and max
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return the random value between min and max
     */
    abstract fun range(min: T, max: T): T

    abstract fun read(value: String): T?


    inline fun new(value: T = defaultValue): RPGBuff<T> {
        return RPGBuff(this, value)
    }

    override fun toString(): String {
        return "RPGStat(id=$id)"
    }

    override fun equals(other: Any?): kotlin.Boolean {
        val o = other as? RPGStat<*> ?: return false
        return o.id == this.id
    }

    override fun hashCode(): kotlin.Int {
        return id.hashCode()
    }


    abstract class Double : RPGStat<kotlin.Double>() {
        override val defaultValue: kotlin.Double = 0.0
        override val clazz: KClass<kotlin.Double> = kotlin.Double::class
        override fun plus(value: kotlin.Double, other: kotlin.Double): kotlin.Double {
            return value + other
        }
        override fun minus(value: kotlin.Double, other: kotlin.Double): kotlin.Double {
            return value - other
        }

        override fun range(min: kotlin.Double, max: kotlin.Double): kotlin.Double {
            return random.randDouble(min, max)
        }

        override fun read(value: String): kotlin.Double? {
            return value.toDoubleOrNull()
        }
    }

    abstract class Float : RPGStat<kotlin.Float>() {
        override val defaultValue: kotlin.Float = 0f
        override val clazz: KClass<kotlin.Float> = kotlin.Float::class
        override fun plus(value: kotlin.Float, other: kotlin.Float): kotlin.Float {
            return value + other
        }
        override fun minus(value: kotlin.Float, other: kotlin.Float): kotlin.Float {
            return value - other
        }

        override fun range(min: kotlin.Float, max: kotlin.Float): kotlin.Float {
            return random.randFloat(min, max)
        }

        override fun read(value: String): kotlin.Float? {
            return value.toFloatOrNull()
        }

    }

    abstract class Int : RPGStat<kotlin.Int>() {
        override val defaultValue: kotlin.Int = 0
        override val clazz: KClass<kotlin.Int> = kotlin.Int::class
        override fun plus(value: kotlin.Int, other: kotlin.Int): kotlin.Int {
            return value + other
        }
        override fun minus(value: kotlin.Int, other: kotlin.Int): kotlin.Int {
            return value - other
        }

        override fun range(min: kotlin.Int, max: kotlin.Int): kotlin.Int {
            return random.randInt(min, max)
        }

        override fun read(value: String): kotlin.Int? {
            return value.toIntOrNull()
        }
    }

    abstract class BigInteger : RPGStat<java.math.BigInteger>() {
        override val defaultValue: java.math.BigInteger = java.math.BigInteger.ZERO
        override val clazz: KClass<java.math.BigInteger> = java.math.BigInteger::class
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
    }

    abstract class Boolean : RPGStat<kotlin.Boolean>() {
        override val defaultValue: kotlin.Boolean = false
        override val clazz: KClass<kotlin.Boolean> = kotlin.Boolean::class
        override fun plus(value: kotlin.Boolean, other: kotlin.Boolean): kotlin.Boolean {
            return value || other
        }

        override fun range(min: kotlin.Boolean, max: kotlin.Boolean): kotlin.Boolean {
            throw UnsupportedOperationException("Boolean stat can't be ranged")
        }

        override fun minus(value: kotlin.Boolean, other: kotlin.Boolean): kotlin.Boolean {
            return value && other
        }

        override fun read(value: String): kotlin.Boolean? {
            return value.toBooleanSmartOrNull()
        }
    }
}