package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.ext.getEntries
import dev.nikdekur.ndkore.ext.resolveJar
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.service.inject
import dev.nikdekur.ornament.i18n.Locale
import dev.nikdekur.ornament.i18n.toLocale
import org.bukkit.configuration.file.YamlConfiguration
import java.util.jar.JarFile

object YamlBundleUtils {
    suspend fun loadDefaultTranslations(plugin: PluginApplication) {
        val jar = resolveJar(plugin.javaClass.protectionDomain)
        val jarFile = JarFile(jar)

        val translationsEntry = jarFile.getEntries("translations")
            .filter { it.isDirectory }

        translationsEntry.forEachSafe(
            { e, el -> plugin.logger.error(e) { "Error occurred while loading ${el.name}" }}
        ) { bundleEntry ->
            val bundleId = bundleEntry.name
                .removePrefix("translations")
                .replace("/", "")

            loadBundle(plugin, bundleId, jarFile)
        }
    }

    suspend fun loadBundle(plugin: PluginApplication, bundleId: String, jarFile: JarFile) {
        val bundlePath = "translations/$bundleId"
        val bundleEntries = jarFile.getEntries(bundlePath)

        val translationsMap: MutableMultiMap<Locale, String, String> = LinkedHashMap()
        bundleEntries.forEach { entry ->
            if (entry.isDirectory || !entry.name.endsWith(".yml")) return@forEach
            val localeStr = entry.name
                .removePrefix(bundlePath)
                .replace("/", "")
                .removeSuffix(".yml")

            val locale = localeStr.toLocale()
            val config = jarFile.getInputStream(entry).reader().use {
                YamlConfiguration().apply {
                    options().pathSeparator(EMPTY_CHAR)
                    load(it)
                }
            }


            // Put all keys and values (as string) into the map
            config.getKeys(false).forEach { key ->
                val value = config[key]
                val string = when (value) {
                    is String -> value
                    is Collection<*> -> value.joinToString("\n")
                    else -> {
                        plugin.logger.warn { "Invalid translation for key '$key' in default locale '$localeStr'! Actual type is ${value::class}" }
                        return@forEach
                    }
                }

                translationsMap.put(locale, key, string, ::LinkedHashMap)
            }
        }

        val i18n: I18nService by plugin.inject(MineLib.Qualifier)

        i18n.saveBundle(
            id = bundleId,
            translations = translationsMap,
            merge = true
        )
    }


    const val EMPTY_CHAR = (-1).toChar()
}