package dev.nikdekur.minelib

import de.tr7zw.changeme.nbtapi.NBT
import dev.nikdekur.minelib.app.AbstractPluginApplication
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.command.RuntimeCommandService
import dev.nikdekur.minelib.command.api.CommandService
import dev.nikdekur.minelib.command.ml.MineLibCommand
import dev.nikdekur.minelib.drawing.DrawingService
import dev.nikdekur.minelib.drawing.SchedulerDrawingService
import dev.nikdekur.minelib.gui.GUIListener
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.YamlBundleUtils
import dev.nikdekur.minelib.i18n.config.ConfigI18nService
import dev.nikdekur.minelib.movement.DataSetMovementService
import dev.nikdekur.minelib.movement.MovementService
import dev.nikdekur.minelib.nms.DefaultVersionAdapter
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.pentity.PersonalEntityService
import dev.nikdekur.minelib.pentity.RuntimePersonalEntityService
import dev.nikdekur.minelib.plugin.ApplicationServerPlugin
import dev.nikdekur.minelib.rpg.RPGProfilesService
import dev.nikdekur.minelib.rpg.RPGService
import dev.nikdekur.minelib.rpg.RuntimeRPGProfilesService
import dev.nikdekur.minelib.rpg.RuntimeRPGService
import dev.nikdekur.ndkore.scheduler.CoroutineScheduler
import dev.nikdekur.ndkore.scheduler.Scheduler
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
import dev.nikdekur.ornament.i18n.dataset.DataSetI18nService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
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
            GUIListener(this)
            // FullRPGListener(this),
            // DefaultConditionsListener(this)
        ) + adapter.components
    }

    val adapter by lazy {
        val packageName = Bukkit.getServer().javaClass.`package`.name
        val versionStr = packageName.substring(packageName.lastIndexOf('.') + 1)

        val found = VersionAdapter.findAdapter(this, versionStr)
        if (found != null) {
            logger.info { "Found version adapter for version: $versionStr" }
            found
        } else {
            logger.warn { "No version adapter found for version: $versionStr. Using default adapter." }
            DefaultVersionAdapter(this)
        }
    }

    override fun createScheduler(): Scheduler {
        return CoroutineScheduler.fromSupervisor(Dispatchers.IO)
    }
    override suspend fun ServicesManager.registerServices() {
        val app = this@MineLib

        listOf(
            // dataset
            YamlKtFileDataSetService(app) bind DataSetService::class qualify Qualifier,

            // i18n
            YamlKtMultiFileDataSetService(app) bind DataSetService::class qualify I18nQualifier,
            DataSetI18nService(app, datasetQualifier = Qualifier, i18nDatasetQualifier = I18nQualifier) bind OrnamentI18nService::class qualify Qualifier,

            DataSetMovementService(app) bind MovementService::class qualify Qualifier,
            SchedulerDrawingService(app) bind DrawingService::class qualify Qualifier,
            RuntimeCommandService(app) bind CommandService::class qualify Qualifier,
            ConfigI18nService(app) bind I18nService::class qualify Qualifier,
            RuntimeRPGService(app) bind RPGService::class qualify Qualifier,
            RuntimeRPGProfilesService(app) bind RPGProfilesService::class qualify Qualifier,
            adapter bind VersionAdapter::class qualify Qualifier,

            RuntimePersonalEntityService(app) bind PersonalEntityService::class qualify Qualifier,
        ).forEach { registerService(it) }

        with (adapter) {
            this@registerServices.registerServices()
        }
    }


    override fun whenFinishReload() {
        super.whenFinishReload() // Respect super class

        try {
            runBlocking {
                YamlBundleUtils.loadDefaultTranslations(this@MineLib)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error while loading MineLib translations!" }
        }
    }




    companion object {
        val Qualifier = "minelib".qualifier
        val I18nQualifier = "${Qualifier.value}_i18n".qualifier
    }
}