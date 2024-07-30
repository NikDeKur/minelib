package dev.nikdekur.minelib.utils

import java.io.File
import java.io.FileInputStream
import java.util.function.Predicate
import java.util.logging.Level
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ClassUtils {

    val logger = Logger.getLogger(javaClass.name)

    /**
     * Loads all classes from the given jar file.
     *
     * @param classLoader the class loader used to load the classes
     * @param jarFile the jar file to load the classes from
     * @param filter only classes whose names are accepted by this filter are loaded
     */
    fun loadAllClassesFromJar(
        classLoader: ClassLoader,
        file: File,
        filter: Predicate<String>
    ) {
        ZipInputStream(FileInputStream(file)).use { jar ->
            var entry: ZipEntry? = jar.getNextEntry()
            while (entry != null) {
                if (entry.isDirectory) {
                    entry = jar.getNextEntry()
                    continue
                }
                val entryName: String = entry.name
                if (!entryName.endsWith(".class")) {
                    entry = jar.getNextEntry()
                    continue
                }

                // Check filter:
                val className = entryName.substring(
                    0,
                    entryName.length - ".class".length
                ).replace('/', '.')
                if (!filter.test(className)) {
                    entry = jar.getNextEntry()
                    continue
                }

                // Try to load the class:
                // logger.info(" $className");
                try {
                    classLoader.loadClass(className)
                } catch (e: LinkageError) {
                    // ClassNotFoundException: Not expected here.
                    // LinkageError: If some class dependency could not be found, or if some static
                    // class initialization code fails.
                    logger.log(
                        Level.WARNING, "Could not load class '" + className
                                + "' from jar file '" + file + "'.", e
                    )
                    // Continue loading any other remaining classes.
                } catch (e: ClassNotFoundException) {
                    logger.log(
                        Level.WARNING, "Could not load class '" + className
                                + "' from jar file '" + file + "'.", e
                    )
                }
                entry = jar.getNextEntry()
            }
        }

    }
}
