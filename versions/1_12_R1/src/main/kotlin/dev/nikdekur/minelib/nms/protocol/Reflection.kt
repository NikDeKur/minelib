package dev.nikdekur.minelib.nms.protocol

import org.bukkit.Bukkit
import dev.nikdekur.minelib.nms.protocol.Reflection.ConstructorInvoker
import dev.nikdekur.minelib.nms.protocol.Reflection.MethodInvoker
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A utility class that simplifies reflection in Bukkit plugins.
 *
 * @author Kristian
 */
object Reflection {
    // Deduce the net.minecraft.server.v* package
    private val OBC_PREFIX: String = Bukkit.getServer().javaClass.getPackage().name
    private val NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server")
    private val VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "")

    // Variable replacement
    private val MATCH_VARIABLE: Pattern = Pattern.compile("\\{([^}]+)}")

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target - the target type.
     * @param name - the name of the field, or NULL to ignore.
     * @param fieldType - a compatible field type.
     * @return The field accessor.
     */
    fun <T> getField(target: Class<*>?, name: String?, fieldType: Class<T>?): FieldAccessor<T> {
        return getField(target, name, fieldType, 0)
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className - lookup name of the class, see [.getClass].
     * @param name - the name of the field, or NULL to ignore.
     * @param fieldType - a compatible field type.
     * @return The field accessor.
     */
    fun <T> getField(className: String, name: String?, fieldType: Class<T>?): FieldAccessor<T> {
        return getField(getClass(className), name, fieldType, 0)
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target - the target type.
     * @param fieldType - a compatible field type.
     * @param index - the number of compatible fields to skip.
     * @return The field accessor.
     */
    fun <T> getField(target: Class<*>?, fieldType: Class<T>?, index: Int): FieldAccessor<T> {
        return getField(target, null, fieldType, index)
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className - lookup name of the class, see [.getClass].
     * @param fieldType - a compatible field type.
     * @param index - the number of compatible fields to skip.
     * @return The field accessor.
     */
    fun <T> getField(className: String, fieldType: Class<T>?, index: Int): FieldAccessor<T> {
        return getField(getClass(className), fieldType, index)
    }

    // Common method
    private fun <T> getField(target: Class<*>?, name: String?, fieldType: Class<T>?, index: Int): FieldAccessor<T> {
        var indx = index
        for (field in target!!.declaredFields) {
            if ((name == null || field.name == name) && fieldType!!.isAssignableFrom(field.type) && (indx-- <= 0)) {
                field.isAccessible = true

                // A function for retrieving a specific field value
                return object : FieldAccessor<T> {
                    override fun get(target: Any?): T {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            return field[target] as T
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override fun set(target: Any?, value: Any?) {
                        try {
                            field[target] = value
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override fun hasField(target: Any): Boolean {
                        // target instanceof DeclaringClass
                        return field.declaringClass.isAssignableFrom(target.javaClass)
                    }
                }
            }
        }

        // Search in parent classes
        if (target.superclass != null) return getField(target.superclass, name, fieldType, indx)

        throw IllegalArgumentException("Cannot find field with type $fieldType")
    }

    /**
     * Retrieves a field with a given type and parameters. This is most useful
     * when dealing with Collections.
     *
     * @param target the target class.
     * @param fieldType Type of the field
     * @param params Variable length array of type parameters
     * @return The field
     *
     * @throws IllegalArgumentException If the field cannot be found
     */
    fun getParameterizedField(target: Class<*>?, fieldType: Class<*>, vararg params: Class<*>?): Field {
        for (field in target!!.declaredFields) {
            val type = field.genericType
            if (field.type == fieldType
                && type is ParameterizedType
                && type.actualTypeArguments.contentEquals(params)
            ) {
                return field
            }
        }

        throw IllegalArgumentException("Unable to find a field with type " + fieldType + " and params " + params.contentToString())
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param className - lookup name of the class, see [.getClass].
     * @param methodName - the method name, or NULL to skip.
     * @param params - the expected parameters.
     * @return An object that invokes this specific method.
     * @throws IllegalStateException If we cannot find this method.
     */
    fun getMethod(className: String, methodName: String?, vararg params: Class<*>?): MethodInvoker {
        return getTypedMethod(getClass(className), methodName, null, *params)
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param clazz - a class to start with.
     * @param methodName - the method name, or NULL to skip.
     * @param params - the expected parameters.
     * @return An object that invokes this specific method.
     * @throws IllegalStateException If we cannot find this method.
     */
    fun getMethod(clazz: Class<*>?, methodName: String?, vararg params: Class<*>?): MethodInvoker {
        return getTypedMethod(clazz, methodName, null, *params)
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param clazz - a class to start with.
     * @param methodName - the method name, or NULL to skip.
     * @param returnType - the expected return type, or NULL to ignore.
     * @param params - the expected parameters.
     * @return An object that invokes this specific method.
     * @throws IllegalStateException If we cannot find this method.
     */
    fun getTypedMethod(
        clazz: Class<*>?,
        methodName: String?,
        returnType: Class<*>?,
        vararg params: Class<*>?
    ): MethodInvoker {
        for (method in clazz!!.declaredMethods) {
            if ((methodName == null || method.name == methodName)
                && (returnType == null || method.returnType == returnType)
                && method.parameterTypes.contentEquals(params)
            ) {
                method.isAccessible = true

                return MethodInvoker { target, arguments ->
                    try {
                        method.invoke(target, *arguments)
                    } catch (e: Exception) {
                        throw RuntimeException("Cannot invoke method $method", e)
                    }
                }
            }
        }

        // Search in every superclass
        if (clazz.superclass != null) return getMethod(clazz.superclass, methodName, *params)

        throw IllegalStateException("Unable to find method $methodName (${params.toList()}).")
    }

    /**
     * Search for the first publicly and privately defined constructor of the given name and parameter count.
     *
     * @param className - lookup name of the class, see [.getClass].
     * @param params - the expected parameters.
     * @return An object that invokes this constructor.
     * @throws IllegalStateException If we cannot find this method.
     */
    fun getConstructor(className: String, vararg params: Class<*>?): ConstructorInvoker {
        return getConstructor(getClass(className), *params)
    }

    /**
     * Search for the first publicly and privately defined constructor of the given name and parameter count.
     *
     * @param clazz - a class to start with.
     * @param params - the expected parameters.
     * @return An object that invokes this constructor.
     * @throws IllegalStateException If we cannot find this method.
     */
    fun getConstructor(clazz: Class<*>, vararg params: Class<*>?): ConstructorInvoker {
        for (constructor in clazz.declaredConstructors) {
            if (constructor.parameterTypes.contentEquals(params)) {
                constructor.isAccessible = true

                return ConstructorInvoker {
                    try {
                        constructor.newInstance(*it)
                    } catch (e: Exception) {
                        throw RuntimeException("Cannot invoke constructor $constructor", e)
                    }
                }
            }
        }

        throw IllegalStateException(
            String.format(
                "Unable to find constructor for %s (%s).",
                clazz,
                params.toList()
            )
        )
    }

    /**
     * Retrieve a class from its full name, without knowing its type on compile time.
     *
     *
     * This is useful when looking up fields by a NMS or OBC type.
     *
     *
     *
     * @see {@link .getClass
     * @param lookupName - the class name with variables.
     * @return The class.
     */
    fun getUntypedClass(lookupName: String): Class<out Any> {
        return getClass(lookupName)
    }

    /**
     * Retrieve a class from its full name with alternatives, without knowing its type on compile time.
     *
     *
     * This is useful when looking up fields by a NMS or OBC type.
     *
     *
     *
     * @see {@link .getClass
     * @param lookupName - the class name with variables.
     * @param aliases - alternative names for this class.
     * @return The class.
     */
    fun getUntypedClass(lookupName: String, vararg aliases: String): Class<out Any> {
        return getClass(lookupName, *aliases)
    }

    /**
     * Retrieve a class from its full name.
     *
     *
     * Strings enclosed with curly brackets - such as {TEXT} - will be replaced according to the following table:
     *
     *
     * <table border="1">
     * <tr>
     * <th>Variable</th>
     * <th>Content</th>
    </tr> *
     * <tr>
     * <td>{nms}</td>
     * <td>Actual package name of net.minecraft.server.VERSION</td>
    </tr> *
     * <tr>
     * <td>{obc}</td>
     * <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
    </tr> *
     * <tr>
     * <td>{version}</td>
     * <td>The current Minecraft package VERSION, if any.</td>
    </tr> *
    </table> *
     *
     * @param lookupName - the class name with variables.
     * @return The looked-up class.
     * @throws IllegalArgumentException If a variable or class could not be found.
     */
    fun getClass(lookupName: String): Class<out Any> {
        return getCanonicalClass(expandVariables(lookupName))
    }

    /**
     * Retrieve the first class that matches the full class name.
     *
     *
     * Strings enclosed with curly brackets - such as {TEXT} - will be replaced according to the following table:
     *
     *
     * <table border="1">
     * <tr>
     * <th>Variable</th>
     * <th>Content</th>
    </tr> *
     * <tr>
     * <td>{nms}</td>
     * <td>Actual package name of net.minecraft.server.VERSION</td>
    </tr> *
     * <tr>
     * <td>{obc}</td>
     * <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
    </tr> *
     * <tr>
     * <td>{version}</td>
     * <td>The current Minecraft package VERSION, if any.</td>
    </tr> *
    </table> *
     *
     * @param lookupName - the class name with variables.
     * @param aliases - alternative names for this class.
     * @return Class object.
     * @throws RuntimeException If we are unable to find any of the given classes.
     */
    fun getClass(lookupName: String, vararg aliases: String): Class<out Any> {
        return try {
            // Try the main class first
            getClass(lookupName)
        } catch (e: RuntimeException) {
            var success: Class<*>? = null

            // Try every alias too
            for (alias in aliases) {
                try {
                    success = getClass(alias)
                    break
                } catch (e1: RuntimeException) {
                    // Ignore
                }
            }

            success
                ?: // Hack failed
                throw RuntimeException(
                    String.format(
                        "Unable to find %s (%s)",
                        lookupName,
                        java.lang.String.join(",", *aliases)
                    )
                )
        }
    }

    /**
     * Retrieve a class in the net.minecraft.server.VERSION.* package.
     *
     * @param name - the name of the class, excluding the package.
     * @throws IllegalArgumentException If the class doesn't exist.
     */
    fun getMinecraftClass(name: String): Class<*> {
        return getCanonicalClass("$NMS_PREFIX.$name")
    }

    /**
     * Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
     *
     * @param name - the name of the class, excluding the package.
     * @throws IllegalArgumentException If the class doesn't exist.
     */
    fun getCraftBukkitClass(name: String): Class<*> {
        return getCanonicalClass("$OBC_PREFIX.$name")
    }

    /**
     * Retrieve a class by its canonical name.
     *
     * @param canonicalName - the canonical name.
     * @return The class.
     */
    private fun getCanonicalClass(canonicalName: String): Class<*> {
        try {
            return Class.forName(canonicalName)
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Cannot find $canonicalName", e)
        }
    }

    /**
     * Expand variables such as "{nms}" and "{obc}" to their corresponding packages.
     *
     * @param name - the full name of the class.
     * @return The expanded string.
     */
    private fun expandVariables(name: String): String {
        val output = StringBuffer()
        val matcher = MATCH_VARIABLE.matcher(name)

        while (matcher.find()) {
            val variable = matcher.group(1)

            // Expand all detected variables
            var replacement = when {
                "nms".equals(variable, ignoreCase = true) -> NMS_PREFIX
                "obc".equals(variable, ignoreCase = true) -> OBC_PREFIX
                "version".equals(variable, ignoreCase = true) -> VERSION
                else -> throw IllegalArgumentException("Unknown variable: $variable")
            }

            // Assume the expanded variables are all packages, and append a dot
            if (replacement.isNotEmpty() && matcher.end() < name.length && name[matcher.end()] != '.') replacement += "."
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement))
        }

        matcher.appendTail(output)
        return output.toString()
    }

    /**
     * An interface for invoking a specific constructor.
     */
    fun interface ConstructorInvoker {
        /**
         * Invoke a constructor for a specific class.
         *
         * @param arguments - the arguments to pass to the constructor.
         * @return The constructed object.
         */
        fun invoke(vararg arguments: Any?): Any?
    }

    /**
     * An interface for invoking a specific method.
     */
    fun interface MethodInvoker {
        /**
         * Invoke a method on a specific target object.
         *
         * @param target - the target object, or NULL for a static method.
         * @param arguments - the arguments to pass to the method.
         * @return The return value, or NULL if is void.
         */
        fun invoke(target: Any?, vararg arguments: Any?): Any?
    }

    /**
     * An interface for retrieving the field content.
     *
     * @param <T> - field type.
    </T> */
    interface FieldAccessor<T> {
        /**
         * Retrieve the content of a field.
         *
         * @param target - the target object, or NULL for a static field.
         * @return The value of the field.
         */
        operator fun get(target: Any?): T

        /**
         * Set the content of a field.
         *
         * @param target - the target object, or NULL for a static field.
         * @param value - the new value of the field.
         */
        fun set(target: Any?, value: Any?)

        /**
         * Determine if the given object has this field.
         *
         * @param target - the object to test.
         * @return TRUE if it does, FALSE otherwise.
         */
        fun hasField(target: Any): Boolean
    }
}