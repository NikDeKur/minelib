package dev.nikdekur.minelib.fastboard

import org.bukkit.Bukkit
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.util.*
import java.util.function.Predicate

object FastReflection {

    private const val NM_PACKAGE = "net.minecraft"
    private val OBC_PACKAGE: String = Bukkit.getServer().javaClass.`package`.name
    private val NMS_PACKAGE: String = OBC_PACKAGE.replace("org.bukkit.craftbukkit", "$NM_PACKAGE.server")

    private val VOID_METHOD_TYPE: MethodType = MethodType.methodType(Void.TYPE)
    private val NMS_REPACKAGED: Boolean = optionalClass("$NM_PACKAGE.network.protocol.Packet").isPresent
    private val MOJANG_MAPPINGS: Boolean = optionalClass("$NM_PACKAGE.network.chat.Component").isPresent

    @Volatile
    private var theUnsafe: Any? = null

    fun isRepackaged(): Boolean = NMS_REPACKAGED

    fun nmsClassName(post1_17package: String?, className: String): String {
        return if (NMS_REPACKAGED) {
            val classPackage = if (post1_17package == null) NM_PACKAGE else "$NM_PACKAGE.$post1_17package"
            "$classPackage.$className"
        } else {
            "$NMS_PACKAGE.$className"
        }
    }

    @Throws(ClassNotFoundException::class)
    fun nmsClass(post1_17package: String?, className: String): Class<*> {
        return Class.forName(nmsClassName(post1_17package, className))
    }

    @Throws(ClassNotFoundException::class)
    fun nmsClass(post1_17package: String?, spigotClass: String, mojangClass: String): Class<*> {
        return nmsClass(post1_17package, if (MOJANG_MAPPINGS) mojangClass else spigotClass)
    }

    fun nmsOptionalClass(post1_17package: String?, className: String): Optional<Class<*>> {
        return optionalClass(nmsClassName(post1_17package, className))
    }

    fun obcClassName(className: String): String {
        return "$OBC_PACKAGE.$className"
    }

    @Throws(ClassNotFoundException::class)
    fun obcClass(className: String): Class<*> {
        return Class.forName(obcClassName(className))
    }

    fun obcOptionalClass(className: String): Optional<Class<*>> {
        return optionalClass(obcClassName(className))
    }

    fun optionalClass(className: String): Optional<Class<*>> {
        return try {
            Optional.of(Class.forName(className))
        } catch (e: ClassNotFoundException) {
            Optional.empty()
        }
    }

    fun enumValueOf(enumClass: Class<*>, enumName: String): Any {
        @Suppress("UNCHECKED_CAST")
        return java.lang.Enum.valueOf(enumClass.asSubclass(Enum::class.java), enumName)
    }

    fun enumValueOf(enumClass: Class<*>, enumName: String, fallbackOrdinal: Int): Any {
        return try {
            enumValueOf(enumClass, enumName)
        } catch (e: IllegalArgumentException) {
            val constants = enumClass.enumConstants
            if (constants.size > fallbackOrdinal) {
                constants[fallbackOrdinal]
            } else {
                throw e
            }
        }
    }

    @Throws(ClassNotFoundException::class)
    fun innerClass(parentClass: Class<*>, classPredicate: Predicate<Class<*>>): Class<*> {
        for (innerClass in parentClass.declaredClasses) {
            if (classPredicate.test(innerClass)) {
                return innerClass
            }
        }
        throw ClassNotFoundException("No class in ${parentClass.canonicalName} matches the predicate.")
    }

    @Throws(IllegalAccessException::class)
    fun optionalConstructor(declaringClass: Class<*>, lookup: MethodHandles.Lookup, type: MethodType): Optional<MethodHandle> {
        return try {
            Optional.of(lookup.findConstructor(declaringClass, type))
        } catch (e: NoSuchMethodException) {
            Optional.empty()
        }
    }

    @Throws(Exception::class)
    fun findPacketConstructor(packetClass: Class<*>, lookup: MethodHandles.Lookup): PacketConstructor {
        try {
            val constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE)
            return PacketConstructor { constructor.invoke() }
        } catch (e: NoSuchMethodException) {
            // Переход к варианту через Unsafe
        } catch (e: IllegalAccessException) {
            // Переход к варианту через Unsafe
        }

        if (theUnsafe == null) {
            synchronized(this) {
                if (theUnsafe == null) {
                    val unsafeClass = Class.forName("sun.misc.Unsafe")
                    val theUnsafeField: Field = unsafeClass.getDeclaredField("theUnsafe")
                    theUnsafeField.isAccessible = true
                    theUnsafe = theUnsafeField.get(null)
                }
            }
        }

        val allocateMethodType = MethodType.methodType(Any::class.java, Class::class.java)
        val allocateMethod = lookup.findVirtual(theUnsafe!!.javaClass, "allocateInstance", allocateMethodType)
        return PacketConstructor { allocateMethod.invoke(theUnsafe, packetClass) }
    }

    fun interface PacketConstructor {
        @Throws(Throwable::class)
        fun invoke(): Any
    }
}
