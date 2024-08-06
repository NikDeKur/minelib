package dev.nikdekur.minelib.hologram

import dev.nikdekur.minelib.pentity.PersonalEntity
import dev.nikdekur.minelib.utils.AbstractLocation
import org.bukkit.entity.Player

interface Hologram : PersonalEntity {

    fun getLocation(player: Player): AbstractLocation
    fun getText(player: Player): Collection<String>

    fun update(player: Player)
    fun update()
}

