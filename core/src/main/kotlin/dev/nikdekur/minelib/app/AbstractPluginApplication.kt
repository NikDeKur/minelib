package dev.nikdekur.minelib.app

import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.koin.SimpleKoinContext
import dev.nikdekur.ndkore.service.manager.KoinServicesManager
import dev.nikdekur.ndkore.service.manager.ServicesManager
import dev.nikdekur.ornament.AbstractApplication
import dev.nikdekur.ornament.environment.Environment
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.koin.environmentProperties
import java.io.File

abstract class AbstractPluginApplication(
    override val environment: Environment,
    val plugin: ServerPlugin
) : AbstractApplication(), PluginApplication, ServerPlugin by plugin {


    override val onlinePlayers: Collection<Player>
        get() = server.onlinePlayers


    abstract suspend fun ServicesManager.registerServices()


    override fun whenFinishReload() {
        super.whenFinishReload()
        registerComponents()
    }


    /**
     * Internal function called once the plugin is enabled to register all components.
     *
     * Function go through [components] and register them via [registerComponent] function.
     */
    protected open fun registerComponents() {
        val components = try {
            components
        } catch (e: LinkageError) {
            logger.error {
                "Failed to register plugin components. " +
                        "Make sure to create all instances in lazy or getter format. " +
                        "Storing instances in fields can cause bukkit class-loading issues."
            }

            logger.error(e) {
                "Also, error may occur if dependency collision is present. " +
                        "Make sure for bukkit not to see similar classes (at same package) in different plugins."
            }

            return
        }

        components.forEachSafe(
            { e, el -> logger.warn(e) { "Failed to register component $el" } },
            ::registerComponent
        )
    }


    override fun registerComponent(component: Any): Boolean {
        var success = false

        if (component is Listener) {
            addListener(component)
            success = true
        }

        if (component is ServerCommand) {
            addCommand(component)
            success = true
        }

        return success
    }


    override suspend fun createServicesManager(): ServicesManager {
        return KoinServicesManager {
            context(SHARED_CONTEXT)
        }.apply {
            registerServices()
        }
    }





    override fun loadDirectory(name: String): File {
        val dir = File(dataFolder, name)
        if (!dir.exists())
            dir.mkdirs()
        return dir
    }


    override fun loadFile(fileName: String, folder: File?): File {
        val folder = folder ?: dataFolder
        folder.mkdirs()
        return File(folder, fileName).also {
            if (!it.exists()) it.createNewFile()
        }
    }

//    val yaml by lazy {
//        Yaml(
//            configuration = YamlConfiguration(
//                strictMode = false
//            )
//        )
//    }
//
//    override fun <T : Any> loadConfig(
//        configName: String,
//        clazz: Class<T>,
//        requireFilled: Boolean,
//        folder: File?
//    ): T {
//        val configName = if (!configName.endsWith(".yml")) "$configName.yml" else configName
//        val file = loadFile(configName, folder)
//        // Check if the file is empty
//        // loadFile ensures that the file exists
//        if (file.length() == 0L) {
//            check(!requireFilled) {
//                "Config file `$configName` is empty while it should be filled."
//            }
//
//            val config = try {
//                clazz.newInstance()
//            } catch (_: NoSuchMethodException) {
//                throw IllegalArgumentException(
//                    "Cannot create a default instance of ${clazz.simpleName}. " +
//                            "Make sure the class has a no-args constructor or enable `requireFilled`."
//                )
//            }
//            saveConfig(configName, config, folder)
//            return config
//        }
//
//        val type = KType(clazz.kotlin)
//        return yaml.loadConfig(file.readText(), type)
//    }
//
//    override fun saveConfig(configName: String, config: Any, folder: File?) {
//        val name = if (!configName.endsWith(".yml")) "$configName.yml" else configName
//        val file = loadFile(name, folder)
//        file.writeText(yaml.encodeToString(config))
//    }


    companion object {
        @JvmStatic
        val SHARED_CONTEXT by lazy {
            SimpleKoinContext().apply {
                startKoin { environmentProperties() }
            }
        }
    }
}