package dev.nikdekur.minelib

import dev.nikdekur.minelib.command.RuntimeCommandService
import dev.nikdekur.minelib.command.ml.MineLibCommand
import dev.nikdekur.minelib.drawing.SchedulerDrawingService
import dev.nikdekur.minelib.gui.RuntimeGUIService
import dev.nikdekur.minelib.i18n.ConfigLanguagesService
import dev.nikdekur.minelib.koin.MineLibKoinContext
import dev.nikdekur.minelib.movement.ConfigMovementService
import dev.nikdekur.minelib.nms.DefaultVersionAdapter
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.DefaultRPGService
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.utils.ClassUtils
import org.bukkit.Bukkit
import org.koin.environmentProperties
import java.util.logging.Level
import kotlin.properties.Delegates

class MineLib : ServerPlugin() {

    override val components by lazy {
        listOf(
            // Commands
            MineLibCommand(this),

            // Services
            SchedulerDrawingService(this),
            RuntimeCommandService(this),
            ConfigLanguagesService(this),
            ConfigMovementService(this),
            DefaultRPGService(this),
            RuntimeGUIService()
        )
    }


    override fun whenLoad() {
        MineLibKoinContext.stopKoin()
        MineLibKoinContext.startKoin {
            environmentProperties()
        }

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



    companion object {
        @JvmStatic
        lateinit var instance: MineLib
            private set

        @JvmStatic
        @get:JvmName("getSchedulerInstance")
        @set:JvmName("setSchedulerInstance")
        var scheduler: Scheduler by Delegates.notNull()
            private set

        lateinit var versionAdapter: VersionAdapter
    }
}