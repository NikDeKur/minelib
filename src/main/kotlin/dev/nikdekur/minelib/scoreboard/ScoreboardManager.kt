package dev.nikdekur.minelib.scoreboard


import dev.nikdekur.minelib.PluginModule
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.scoreboard.events.AssembleBoardCreateEvent
import dev.nikdekur.ndkore.cooldown.Cooldown
import dev.nikdekur.ndkore.cooldown.GrowPolicy
import dev.nikdekur.ndkore.cooldown.GrowingCooldownManager
import dev.nikdekur.ndkore.module.dependencies
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class ScoreboardManager(override val app: ServerPlugin, val adapter: AssembleAdapter) : PluginModule {

    // ScoreboardManager will be loaded after all possibly displayable data is loaded
    // And unloaded first before all possibly displayable data is unloaded
    
    override val dependencies = dependencies {
        last = true
    }

    var thread: AssembleThread? = null
    var listeners: AssembleListener = AssembleListener(this)
    var style = AssembleStyle.MODERN

    val boards = ConcurrentHashMap<UUID, AssembleBoard>()

    val cooldownPolicy = GrowPolicy.exponential(Cooldown(1, TimeUnit.SECONDS))
    val cooldownManager = GrowingCooldownManager<UUID>(cooldownPolicy) { uuid, cooldown ->
        bLogger.warning("[Scoreboard] Increasing cooldown for $uuid to ${cooldown.duration} ${cooldown.unit}")
    }

    var ticks: Long = 10
    val isHook = false
    val isCallEvents = true

    override fun onLoad() {

        val config = app.loadConfig<ScoreboardConfig>("scoreboard")
        ticks = config.updateDelay
        style = config.style


        // Register Events
        app.addListener(listeners)

        // Ensure that the thread has stopped running.
        if (this.thread != null) {
            thread!!.stop()
            this.thread = null
        }

        // Register new boards for existing online players.
        for (player in app.server.onlinePlayers) {
            // Call Events if enabled.

            if (this.isCallEvents) {
                val createEvent = AssembleBoardCreateEvent(player)

                Bukkit.getPluginManager().callEvent(createEvent)
                if (createEvent.isCancelled) {
                    continue
                }
            }

            boards.putIfAbsent(player.uniqueId, AssembleBoard(player, this))
        }

        // Start Thread.
        this.thread = AssembleThread(this)
    }


    override fun onUnload() {
        if (this.thread != null) {
            thread!!.stop()
            this.thread = null
        }



        // Destroy player scoreboards.
        for (uuid in boards.keys) {
            val player = Bukkit.getPlayer(uuid)

            if (player == null || !player.isOnline) {
                continue
            }

            boards.remove(uuid)
            player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        }
    }
}
