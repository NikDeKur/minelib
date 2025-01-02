package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.ornament.environment.Environment
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import java.io.File
import java.io.InputStream
import java.util.logging.Logger


class TestServerPlugin : ApplicationServerPlugin() {
    override fun createApplication(environment: Environment): PluginApplication {
        TODO("Not yet implemented")
    }
}

open class TestServer(
    val environment: Environment,
    plugin: TestServerPlugin
) : ServerPlugin {
    override val scheduler = Scheduler(this)

    override val clazzLoader: ClassLoader
        get() = this::class.java.classLoader

    override val listeners = ArrayList<Listener>()

    override fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    override fun addCommand(command: ServerCommand) {
        TODO()
    }

    override fun getDataFolder(): File? {
        return File("plugin")
    }

    override fun getDescription(): PluginDescriptionFile? {
        return PluginDescriptionFile("TestServerPlugin", "1.0", "dev.nikdekur.minelib.plugin.TestServerPlugin")
    }

    override fun getConfig(): FileConfiguration? {
        TODO("Not yet implemented")
    }

    override fun getResource(p0: String?): InputStream? {
        TODO("Not yet implemented")
    }

    override fun saveConfig() {
        TODO("Not yet implemented")
    }

    override fun saveDefaultConfig() {
        TODO("Not yet implemented")
    }

    override fun saveResource(p0: String?, p1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun reloadConfig() {
        TODO("Not yet implemented")
    }

    override fun getPluginLoader(): PluginLoader? {
        TODO("Not yet implemented")
    }

    override fun getServer(): Server? {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDisable() {
        TODO("Not yet implemented")
    }

    override fun onLoad() {
        TODO("Not yet implemented")
    }

    override fun onEnable() {
        TODO("Not yet implemented")
    }

    override fun isNaggable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setNaggable(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDefaultWorldGenerator(
        p0: String?,
        p1: String?
    ): ChunkGenerator? {
        TODO("Not yet implemented")
    }

    override fun getLogger(): Logger? {
        TODO("Not yet implemented")
    }

    override fun getName(): String? {
        TODO("Not yet implemented")
    }

    override fun onTabComplete(
        p0: CommandSender?,
        p1: Command?,
        p2: String?,
        p3: Array<out String?>?
    ): List<String?>? {
        TODO("Not yet implemented")
    }

    override fun onCommand(
        p0: CommandSender?,
        p1: Command?,
        p2: String?,
        p3: Array<out String?>?
    ): Boolean {
        TODO("Not yet implemented")
    }
}