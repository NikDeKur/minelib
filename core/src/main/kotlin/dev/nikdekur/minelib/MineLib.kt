package dev.nikdekur.minelib

import de.tr7zw.changeme.nbtapi.NBT
import dev.nikdekur.minelib.app.AbstractPluginApplication
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.command.RuntimeCommandService
import dev.nikdekur.minelib.command.api.CommandService
import dev.nikdekur.minelib.command.ml.MineLibCommand
import dev.nikdekur.minelib.drawing.DrawingService
import dev.nikdekur.minelib.drawing.SchedulerDrawingService
import dev.nikdekur.minelib.gui.GUIService
import dev.nikdekur.minelib.gui.RuntimeGUIService
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.config.ConfigI18nService
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.movement.DataSetMovementService
import dev.nikdekur.minelib.movement.MovementService
import dev.nikdekur.minelib.nms.DefaultVersionAdapter
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.plugin.ApplicationServerPlugin
import dev.nikdekur.minelib.rpg.RPGProfilesService
import dev.nikdekur.minelib.rpg.RPGService
import dev.nikdekur.minelib.rpg.RuntimeRPGProfilesService
import dev.nikdekur.minelib.rpg.RuntimeRPGService
import dev.nikdekur.ndkore.ext.getEntries
import dev.nikdekur.ndkore.ext.resolveJar
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.service.bind
import dev.nikdekur.ndkore.service.inject
import dev.nikdekur.ndkore.service.manager.ServicesManager
import dev.nikdekur.ndkore.service.qualifier
import dev.nikdekur.ndkore.service.qualify
import dev.nikdekur.ornament.dataset.DataSetService
import dev.nikdekur.ornament.dataset.yaml.YamlKtFileDataSetService
import dev.nikdekur.ornament.dataset.yaml.YamlKtMultiFileDataSetService
import dev.nikdekur.ornament.environment.Environment
import dev.nikdekur.ornament.environment.EnvironmentBuilder
import dev.nikdekur.ornament.i18n.Locale
import dev.nikdekur.ornament.i18n.dataset.DataSetI18nService
import dev.nikdekur.ornament.i18n.toLocale
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.util.jar.JarFile
import dev.nikdekur.ornament.i18n.I18nService as OrnamentI18nService

open class MineLibPlugin : ApplicationServerPlugin() {

    override fun whenFinishReload() {
        NBT.preloadApi()
        super.whenFinishReload()
    }

    override fun setupEnvironment(builder: EnvironmentBuilder) {
        super.setupEnvironment(builder) // Respect super class
        builder.apply {
            value("configs", "i18n")
        }
    }

    override fun createApplication(environment: Environment): PluginApplication {
        return MineLib(environment, this)
    }
}


open class MineLib(
    override val environment: Environment,
    plugin: MineLibPlugin
) : AbstractPluginApplication(environment, plugin) {

    val i18n: I18nService by inject(Qualifier)

    override val components by lazy {
        listOf(
            // Commands
            MineLibCommand(this),

            // Listeners
            // FullRPGListener(this),
            // DefaultConditionsListener(this)
        )
    }

    fun getAdapter(app: PluginApplication): VersionAdapter {
        val packageName = Bukkit.getServer().javaClass.`package`.name
        val versionStr = packageName.substring(packageName.lastIndexOf('.') + 1)

        return VersionAdapter.findAdapter(app, versionStr) ?: run {
            logger.warn { "No version adapter found for version: $versionStr. Using default adapter." }
            DefaultVersionAdapter(this)
        }
    }

    override suspend fun ServicesManager.registerServices() {
        val app = this@MineLib


        listOf(
            // dataset
            YamlKtFileDataSetService(app) bind DataSetService::class qualify Qualifier,

            // i18n
            YamlKtMultiFileDataSetService(app) bind DataSetService::class qualify I18nQualifier,
            DataSetI18nService(app, datasetQualifier = I18nQualifier) bind OrnamentI18nService::class qualify Qualifier,

            DataSetMovementService(app) bind MovementService::class qualify Qualifier,
            SchedulerDrawingService(app) bind DrawingService::class qualify Qualifier,
            RuntimeCommandService(app) bind CommandService::class qualify Qualifier,
            ConfigI18nService(app) bind I18nService::class qualify Qualifier,
            RuntimeRPGService(app) bind RPGService::class qualify Qualifier,
            RuntimeRPGProfilesService(app) bind RPGProfilesService::class qualify Qualifier,
            RuntimeGUIService(app) bind GUIService::class qualify Qualifier,
            getAdapter(app) bind VersionAdapter::class qualify Qualifier
        ).forEach { registerService(it) }
    }


    override fun whenFinishReload() {
        super.whenFinishReload() // Respect super class

        try {
            runBlocking {
                loadDefaultTranslations()
            }
        } catch (e: Exception) {
            logger.error(e) { "Error while loading default translations!" }
        }
    }


    suspend fun loadDefaultTranslations() {
        // Initialize default translations
        DefaultMSG

        // Initialize keys
        DefaultMSG.keys

        val bundle = DefaultMSG.bundle.name
        val jar = resolveJar(javaClass.protectionDomain)
        val jarFile = JarFile(jar)
        val defaultBundleTranslations = jarFile.getEntries("translations/$bundle")
        check(defaultBundleTranslations.isNotEmpty()) {
            "Default translations not found!"
        }

        val translationsMap: MutableMultiMap<Locale, String, String> = LinkedHashMap()
        defaultBundleTranslations.forEach {
            if (it.isDirectory || !it.name.endsWith(".yml")) return@forEach
            val localeStr = it.name.removePrefix("translations/$bundle/").removeSuffix(".yml")
            val locale = localeStr.toLocale()
            val cfg = jarFile.getInputStream(it).reader().use {
                YamlConfiguration.loadConfiguration(it)
            }

            DefaultMSG.keys.forEach { msg ->
                val key = msg.key
                val value = cfg[key]

                val string = when (value) {
                    is String -> value
                    is Collection<*> -> value.joinToString("\n")
                    else -> {
                        logger.warn { "Invalid translation for key '$key' in default locale '$localeStr'! Actual type is ${value::class}" }
                        return@forEach
                    }
                }

                translationsMap.put(locale, key, string, ::LinkedHashMap)
            }
        }

        i18n.saveBundle(bundle, DefaultMSG.keys, translationsMap)
    }

    companion object {
        val Qualifier = "minelib".qualifier
        val I18nQualifier = "${Qualifier.value}_i18n".qualifier
    }
}