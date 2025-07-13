package dev.nikdekur.minelib.v1_12_R1.protocol

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.service.PluginListener
import dev.nikdekur.ndkore.service.inject
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent

class InjectProtocolListener(
    override val app: PluginApplication
) : PluginListener {

    val service: InjectProtocolService by inject(MineLib.Qualifier)

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        val channel = service.getChannel(e.player)

        // Don't inject players that have been explicitly uninjected
        if (!service.uninjectedChannels.contains(channel)) {
            service.injectPlayer(e.player)
        }
    }
}