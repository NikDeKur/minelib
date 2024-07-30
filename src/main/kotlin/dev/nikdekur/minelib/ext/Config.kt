@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.i18n.LanguagesManager
import dev.nikdekur.minelib.i18n.MSGHolder
import dev.nikdekur.minelib.nms.entity.MineEntity
import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.ndkore.ext.enumValueOfOrNull
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.ext.fromBeautifulString
import dev.nikdekur.ndkore.ext.toUUIDOrNull
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import java.math.BigInteger
import java.text.DecimalFormat

val ConfigurationSection.keys: MutableSet<String>
    get() = getKeys(false)

val ConfigurationSection.allKeys: MutableSet<String>
    get() = getKeys(true)


fun ConfigurationSection.copyTo(section: ConfigurationSection) {
    pairs.forEach(section::set)
}

fun ConfigurationSection.getOrThrow(path: String): Any {
    return get(path) ?: throwNotFound(path)
}

inline fun ConfigurationSection.getSection(path: String): ConfigurationSection? {
    return getConfigurationSection(path)
}

fun ConfigurationSection.getSectionOrThrow(path: String): ConfigurationSection {
    return getSection(path) ?: throwNotFound(path)
}

fun ConfigurationSection.getListSection(): List<ConfigurationSection> {
    return keys.mapNotNull { getSection(it) }
}

fun ConfigurationSection.getListSection(path: String): List<ConfigurationSection> {
    return getSection(path)?.getListSection() ?: return emptyList()
}

inline fun ConfigurationSection.forEachSectionSafe(action: (ConfigurationSection) -> Unit) {
    getListSection().forEachSafe({ e, section ->
        bLogger.warning("Error occurred in section ${section.currentPath}")
        e.printStackTrace()
    }, action)
}

inline fun ConfigurationSection.forEachSectionSafe(path: String, action: (ConfigurationSection) -> Unit) {
    getListSection(path).forEachSafe(Exception::printStackTrace, action)
}




val VECTOR_ZERO = Vector(0, 0, 0)

fun ConfigurationSection.readVectorOrThrow(path: String): Vector {
    val serialized = getStringOrThrow(path)
    val coords = serialized.substring(1, serialized.length - 1)
        .split(",")
        .map { it.toDoubleOrNull() ?: throwReport(path, "Invalid number format in vector") }
    if (coords.size != 3) throwReport(path, "Invalid vector format. Required 3 coordinates.")
    return Vector(coords[0], coords[1], coords[2])
}

fun ConfigurationSection.readVector(path: String, default: Vector? = null): Vector? {
    return try {
        readVectorOrThrow(path)
    } catch (e: Exception) {
        return default
    }
}



fun ConfigurationSection.readLocation(path: String, defWorld: org.bukkit.World, def: Location? = null): Location? {
    return try {
        readLocationOrThrow(path, defWorld)
    } catch (e: Exception) {
        def
    }
}

fun ConfigurationSection.readLocationOrThrow(path: String, defWorld: org.bukkit.World): Location {
    val string = getStringOrThrow(path)
    val args = string.substring(1, string.length - 1).split(",")
    if (args.size < 3) throwReport(path, "Invalid location format. Required 3 coordinates.")
    try {
        return when (args.size) {
            3 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                Location(defWorld, x, y, z)
            }

            4 -> {
                val worldId = args[0]
                val world = worldId.toUUIDOrNull()?.let { Bukkit.getWorld(it) } ?: Bukkit.getWorld(worldId) ?: defWorld
                val x = args[1].toDouble()
                val y = args[2].toDouble()
                val z = args[3].toDouble()
                Location(world, x, y, z)
            }

            5 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val yaw = args[3].toFloat()
                val pitch = args[4].toFloat()
                Location(defWorld, x, y, z, yaw, pitch)
            }

            6 -> {
                val worldId = args[0]
                val world = worldId.toUUIDOrNull()?.let { Bukkit.getWorld(it) } ?: Bukkit.getWorld(worldId) ?: defWorld
                val x = args[1].toDouble()
                val y = args[2].toDouble()
                val z = args[3].toDouble()
                val yaw = args[4].toFloat()
                val pitch = args[5].toFloat()
                Location(world, x, y, z, yaw, pitch)
            }

            else -> throwReport(path, "Invalid location format. Required 3, 4, 5 or 6 coordinates.")
        }
    } catch (e: NumberFormatException) {
        throwReport(path, "Invalid number format in location")
    }
}

fun ConfigurationSection.readAbstractLocation(path: String, def: AbstractLocation? = null): AbstractLocation? {
    try {
        val string = getString(path) ?: return def
        val args = string.substring(1, string.length - 1).split(",")
        if (args.size < 3) return def
        return when (args.size) {
            3 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                AbstractLocation(x, y, z)
            }
            4 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val yaw = args[3].toFloat()
                AbstractLocation(x, y, z, yaw)
            }
            5 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val yaw = args[3].toFloat()
                val pitch = args[4].toFloat()
                AbstractLocation(x, y, z, yaw, pitch)
            }
            else -> def
        }
    } catch (e: Exception) {
        bLogger.warning("Error while reading location at path '$path'. Default value will be returned.")
        e.printStackTrace()
        return def
    }
}

fun ConfigurationSection.readAbstractLocationOrThrow(path: String): AbstractLocation {
    val string = getStringOrThrow(path)
    val args = string.substring(1, string.length - 1).split(",")
    if (args.size < 3) throwReport(path, "Invalid location format. Required 3 coordinates.")
    try {
        return when (args.size) {
            3 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                AbstractLocation(x, y, z)
            }

            4 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val yaw = args[3].toFloat()
                AbstractLocation(x, y, z, yaw)
            }

            5 -> {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val yaw = args[3].toFloat()
                val pitch = args[4].toFloat()
                AbstractLocation(x, y, z, yaw, pitch)
            }

            else -> throwReport(path, "Invalid location format. Required 3, 4 or 5 coordinates.")
        }
    } catch (e: NumberFormatException) {
        throwReport(path, "Invalid number format in location")
    }
}


fun ConfigurationSection.readMSGHolderOrThrow(path: String): MSGHolder {
    val id = getStringOrThrow(path)
    return LanguagesManager.getMessage(id) ?: run {
        throwReport(path, "Unknown message id '$id'")
    }
}

fun ConfigurationSection.readMSGHolder(path: String, default: MSGHolder? = null): MSGHolder? {
    return try {
        readMSGHolderOrThrow(path)
    } catch (e: Exception) {
        default
    }
}


fun ConfigurationSection.readEntityTypeOrThrow(path: String): MineEntity {
    val str = getStringOrThrow(path)
    return enumValueOfOrNull<MineEntity>(str.uppercase()) ?: throwReport(path, "Unknown entity type '$str'")
}

fun ConfigurationSection.readEntityType(path: String, def: MineEntity? = null): MineEntity? {
    return try {
        readEntityTypeOrThrow(path)
    } catch (e: Exception) {
        def
    }
}



fun ConfigurationSection.readMaterialOrThrow(path: String): Material {
    val str = getStringOrThrow(path)
    return Material.matchMaterial(str) ?: throwReport(path, "Unknown material '$str'")
}

fun ConfigurationSection.readMaterial(path: String, def: Material? = null): Material? {
    return try {
        readMaterialOrThrow(path)
    } catch (e: Exception) {
        def
    }
}




fun ConfigurationSection.readBigInteger(path: String, default: BigInteger? = null): BigInteger? {
    return try {
        readBigIntegerOrThrow(path)
    } catch (e: Exception) {
        default
    }
}

fun ConfigurationSection.readBigIntegerOrThrow(path: String): BigInteger {
    val str = getStringOrThrow(path)
    return fromBeautifulString(str).toBigDecimal().toBigInteger()
}



inline fun ConfigurationSection.throwInvalidNumberFormat(path: String): Nothing {
    throwReport(path, "Invalid number format")
}
fun ConfigurationSection.getStringOrThrow(path: String): String {
    return getString(path) ?: throwNotFound(path)
}
fun ConfigurationSection.getIntOrThrow(path: String): Int {
    val int = getOrThrow(path)
    return if (int is Number) int.toInt() else throwInvalidNumberFormat(path)
}
fun ConfigurationSection.getFloatOrThrow(path: String): Float {
    val int = getOrThrow(path)
    return if (int is Number) int.toFloat() else throwInvalidNumberFormat(path)
}
fun ConfigurationSection.getDoubleOrThrow(path: String): Double {
    val double = getOrThrow(path)
    return if (double is Number) NumberConversions.toDouble(double) else throwInvalidNumberFormat(path)
}
fun ConfigurationSection.getLongOrThrow(path: String): Long {
    val long = getOrThrow(path)
    return if (long is Number) NumberConversions.toLong(long) else throwInvalidNumberFormat(path)
}
fun ConfigurationSection.getBooleanOrThrow(path: String): Boolean {
    val boolean = getOrThrow(path)
    return if (boolean is Boolean) boolean else throwReport(path, "Invalid boolean format")
}

fun ConfigurationSection.getIntOr(path: String, def: Int? = null): Int? {
    return try {
        getIntOrThrow(path)
    } catch (e: Exception) {
        def
    }
}

fun ConfigurationSection.getFloatingOr(path: String, def: Float? = null): Float? {
    return try {
        getFloatOrThrow(path)
    } catch (e: Exception) {
        def
    }
}
fun ConfigurationSection.getDoubleOr(path: String, def: Double? = null): Double? {
    return try {
        getDoubleOrThrow(path)
    } catch (e: Exception) {
        def
    }
}

fun ConfigurationSection.getLongOr(path: String, def: Long? = null): Long? {
    return try {
        getLongOrThrow(path)
    } catch (e: Exception) {
        def
    }
}

fun ConfigurationSection.getBooleanOr(path: String, def: Boolean? = null): Boolean? {
    return try {
        getBooleanOrThrow(path)
    } catch (e: Exception) {
        def
    }
}









inline fun ConfigurationSection.report(path: String, message: String) {
    bLogger.warning("Error while reading at '$currentPath' path '$path': $message")
}

inline fun ConfigurationSection.throwReport(path: String, message: String): Nothing {
    throw IllegalArgumentException("Error while reading at '$currentPath' path '$path': $message")
}

inline fun ConfigurationSection.throwNotFound(path: String): Nothing {
    throw NoSuchElementException("At '$currentPath' path '$path' not found")
}



val DEFAULT_DECIMAL_FORMAT = DecimalFormat("#,##0.00")
fun ConfigurationSection.getDecimalFormat(path: String): DecimalFormat {
    val str: String = getString(path) ?: return DEFAULT_DECIMAL_FORMAT
    return DecimalFormat(str)
}


val ConfigurationSection.pairs: Map<String, Any>
    get() = getValues(false)

val ConfigurationSection.allPairs: Map<String, Any>
    get() =  getValues(true)