package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.ndkore.service.manager.RuntimeServicesManager
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import java.io.File
import java.io.InputStream
import java.util.logging.Logger
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

open class TestServerPlugin : ServerPlugin {

    override val clazzLoader: ClassLoader
        get() = this.javaClass.classLoader

    override val components: Collection<Any>
        get() = emptyList()

    override val onlinePlayers: Collection<Player>
        get() = emptyList()

    override var scheduler: Scheduler by Delegates.notNull()

    override val servicesManager = RuntimeServicesManager()

    override val listeners: Collection<Listener>
        get() = emptyList()

    var startTime: Long = 0
        private set

    override val uptime: Duration
        get() = (System.currentTimeMillis() - startTime).milliseconds

    override fun registerComponent(component: Any) = false

    override fun addListener(listener: Listener) {

    }

    override fun reload() {

    }

    override fun loadDirectory(name: String): File {
        throw NotImplementedError()
    }

    override fun loadFile(fileName: String, folder: File?): File {
        throw NotImplementedError()
    }

    override fun <T : Any> loadConfig(
        configName: String,
        clazz: Class<T>,
        requireFilled: Boolean,
        folder: File?
    ): T {
        throw NotImplementedError()
    }

    override fun saveConfig(configName: String, config: Any, folder: File?) {

    }

    override fun getDataFolder(): File? {
        TODO("Not yet implemented")
    }

    override fun getDescription(): PluginDescriptionFile? {
        TODO("Not yet implemented")
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

    private val _logger = Logger.getLogger("TestServerPlugin")

    override fun getLogger() = _logger

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