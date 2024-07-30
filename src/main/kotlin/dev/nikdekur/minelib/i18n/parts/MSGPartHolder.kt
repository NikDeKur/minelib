@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n.parts

import dev.nikdekur.ndkore.placeholder.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface MSGPartHolder : Placeholder {

    fun getPlaceholder(sender: CommandSender): Iterable<Placeholder> {
        // Size is 3 because we have max 3 placeholders
        val set = HashSet<Placeholder>(3)

        if (this is MSGNameHolder)
            set.add(Placeholder.of("name" to getName(sender)))

        if (this is MSGDescriptionHolder)
            set.add(Placeholder.of("description" to getDescription(sender)))

        set.add(this)
        return set
    }

    fun getIcon(player: Player): ItemStack = throw NotImplementedError()
}