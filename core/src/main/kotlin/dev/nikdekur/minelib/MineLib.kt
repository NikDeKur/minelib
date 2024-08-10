package dev.nikdekur.minelib

import dev.nikdekur.minelib.command.CommandService
import dev.nikdekur.minelib.command.RuntimeCommandService
import dev.nikdekur.minelib.drawing.DrawingService
import dev.nikdekur.minelib.drawing.DrawingServiceImpl
import dev.nikdekur.minelib.gui.GUIService
import dev.nikdekur.minelib.gui.GUIServiceImpl
import dev.nikdekur.minelib.i18n.ConfigLanguagesService
import dev.nikdekur.minelib.i18n.LanguagesService
import dev.nikdekur.minelib.koin.MineLibKoinContext
import dev.nikdekur.minelib.koin.getKoin
import dev.nikdekur.minelib.koin.loadModule
import dev.nikdekur.minelib.movement.ConfigMovementService
import dev.nikdekur.minelib.movement.MovementService
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.DefaultRPGService
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.utils.ClassUtils.logger
import org.bukkit.Bukkit
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.environmentProperties
import java.util.logging.Level
import kotlin.properties.Delegates

class MineLib : ServerPlugin() {

    override fun registerComponents() {
        super.registerComponents()

        MineLibKoinContext.stopKoin()
        MineLibKoinContext.startKoin {
            environmentProperties()
        }

        loadModule {
            single { this@MineLib }
            singleOf(::DrawingServiceImpl) bind DrawingService::class
            singleOf(::RuntimeCommandService) bind CommandService::class
            singleOf(::ConfigLanguagesService) bind LanguagesService::class
            singleOf(::ConfigMovementService) bind MovementService::class
            singleOf(::GUIServiceImpl) bind GUIService::class
        }

        getKoin().apply {
            registerComponent(get<DrawingService>())
            registerComponent(get<CommandService>())
            registerComponent(get<LanguagesService>())
            registerComponent(get<MovementService>())
            registerComponent(get<GUIService>())
        }
    }

    override val components: Collection<Any>
        get() = listOf(
            // Global modules
            DefaultRPGService
        )



    companion object {
        @JvmStatic
        lateinit var instance: MineLib
            private set

        @JvmStatic
        @get:JvmName("getSchedulerInstance")
        @set:JvmName("setSchedulerInstance")
        var scheduler: Scheduler by Delegates.notNull()
            private set

        val versionAdapter by lazy {
            val packageName = Bukkit.getServer().javaClass.`package`.name
            val versionStr = packageName.substring(packageName.lastIndexOf('.') + 1)
            logger.log(Level.INFO, "Looking for nms adapter on version: $versionStr")
            val adapter = VersionAdapter.getForVersion(versionStr) ?: throw IllegalStateException("Version '$versionStr' is not supported")
            adapter.init(instance)
            adapter
        }
    }
}