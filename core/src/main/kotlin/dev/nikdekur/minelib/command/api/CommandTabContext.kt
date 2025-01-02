@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.ext.offlineNames
import dev.nikdekur.minelib.ext.onlineNames
import org.bukkit.command.CommandSender

class CommandTabContext(
    app: PluginApplication,
    sender: CommandSender,
    args: Array<String>
) : CommandContext(app, sender, args) {

    inline fun online(): MutableList<String> {
        return onlineNames
    }

    inline fun offline(): MutableList<String> {
        return offlineNames
    }
}