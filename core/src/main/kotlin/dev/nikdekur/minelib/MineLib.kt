package dev.nikdekur.minelib

import de.tr7zw.changeme.nbtapi.NBT
import dev.nikdekur.minelib.command.RuntimeCommandService
import dev.nikdekur.minelib.command.ml.MineLibCommand
import dev.nikdekur.minelib.drawing.SchedulerDrawingService
import dev.nikdekur.minelib.gui.RuntimeGUIService
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.config.ConfigI18nService
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.minelib.movement.ConfigMovementService
import dev.nikdekur.minelib.nms.DefaultVersionAdapter
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.RuntimeRPGProfilesService
import dev.nikdekur.minelib.rpg.RuntimeRPGService
import dev.nikdekur.minelib.rpg.condition.DefaultConditionsListener
import dev.nikdekur.minelib.rpg.strategy.FullRPGListener
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.utils.ClassUtils
import dev.nikdekur.ndkore.ext.getEntries
import dev.nikdekur.ndkore.ext.resolveJar
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.service.inject
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.util.jar.JarFile
import java.util.logging.Level
import kotlin.properties.Delegates

class MineLib : ServerPlugin() {

    val i18n: I18nService by inject()

    override val components by lazy {
        listOf(
            // Commands
            MineLibCommand(this),

            // Services
            SchedulerDrawingService(this),
            RuntimeCommandService(this),
            ConfigI18nService(this),
            ConfigMovementService(this),
            RuntimeRPGService(this),
            RuntimeRPGProfilesService(this),
            RuntimeGUIService(this),

            // Listeners
            FullRPGListener(this),
            DefaultConditionsListener(this)
        )
    }


    override fun whenLoad() {
        val packageName = Bukkit.getServer().javaClass.`package`.name
        val versionStr = packageName.substring(packageName.lastIndexOf('.') + 1)
        logger.log(Level.INFO, "Looking for version adapter on version: $versionStr")
        val adapter = VersionAdapter.findAdapter(versionStr) ?: run {
            ClassUtils.logger.log(Level.WARNING, "No version adapter found for version: $versionStr. Using default adapter.")
            DefaultVersionAdapter
        }
        adapter.init(instance)
        versionAdapter = adapter
    }


    override fun afterReload() {
        NBT.preloadApi()

        try {
            loadDefaultTranslations()
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Error while loading default translations!", e)
        }

    }




    fun loadDefaultTranslations() {
        val jar = resolveJar(javaClass.protectionDomain)
        val jarFile = JarFile(jar)
        val defaultBundleTranslations = jarFile.getEntries("translations/default")
        check(defaultBundleTranslations.isNotEmpty()) {
            "Default translations not found!"
        }

        val translationsMap: MutableMultiMap<Locale, I18nMessage, String> = LinkedHashMap()
        defaultBundleTranslations
            .forEach {
                if (it.isDirectory || !it.name.endsWith(".yml")) return@forEach
                val localeStr = it.name.removePrefix("translations/default/").removeSuffix(".yml")
                val locale = Locale.fromCodeOrThrow(localeStr)
                val cfg = jarFile.getInputStream(it).reader().use {
                    YamlConfiguration.loadConfiguration(it)
                }

                DefaultMSG.entries.forEach { msg ->
                    val key = msg.name
                    val value = cfg[key]

                    val string = when (value) {
                        is String -> value
                        is Collection<*> -> value.joinToString("\n")
                        else -> {
                            logger.log(Level.WARNING, "Invalid translation for key '$key' in default locale '$localeStr'!")
                            return@forEach
                        }
                    }

                    translationsMap.put(locale, msg, string, ::LinkedHashMap)
                }
            }

        i18n.loadBundle("default", DefaultMSG.entries, translationsMap)
    }


    companion object {
        @JvmStatic
        lateinit var instance: MineLib

        @JvmStatic
        @get:JvmName("getSchedulerInstance")
        @set:JvmName("setSchedulerInstance")
        var scheduler: Scheduler by Delegates.notNull()
            private set

        lateinit var versionAdapter: VersionAdapter
    }
}