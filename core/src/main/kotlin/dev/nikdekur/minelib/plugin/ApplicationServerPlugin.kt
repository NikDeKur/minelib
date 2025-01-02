package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.ornament.Application
import dev.nikdekur.ornament.environment.Environment
import dev.nikdekur.ornament.environment.EnvironmentBuilder
import dev.nikdekur.ornament.environment.environment
import kotlinx.coroutines.runBlocking

abstract class ApplicationServerPlugin : BaseServerPlugin() {

    open fun setupEnvironment(builder: EnvironmentBuilder) {
        builder.apply {
            val name = description.name
            value("env", "plugins/$name")
        }
    }

    abstract fun createApplication(environment: Environment): PluginApplication

    lateinit var application: PluginApplication


    override fun whenLoad() {
        super.whenLoad() // Respect the super class

        val environment = environment { setupEnvironment(this) }
        application = createApplication(environment)
        runBlocking {
            application.init()
        }

        application.whenLoad()
    }



    override fun whenEnabled() {
        super.whenEnabled() // Respect the super class

        application.whenEnabled()
    }

    override fun whenDisabled() {
        super.whenDisabled() // Respect the super class

        application.whenDisabled()
    }




    override fun whenStartReload() {
        super.whenStartReload() // Respect the super class

        runBlocking {
            try {
                application.stop()
            } catch (_: Exception) {
                // May occur if the plugin if error occurred while enabling the plugin
                // User would have already been notified about the enabling error
                // So don't spam also with onDisable error
            }
        }

        application.whenStartReload()
    }

    override fun whenFinishReload() {
        super.whenFinishReload() // Respect the super class

        runBlocking {
            application.start()
        }

        val state = application.state
        if (state is Application.State.ErrorStarting) {
            val error = state.error
            kLogger.error(error) { "Error occurred while enabling the plugin" }
        }

        application.whenFinishReload()
    }
}