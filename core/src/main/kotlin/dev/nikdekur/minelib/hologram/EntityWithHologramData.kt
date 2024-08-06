package dev.nikdekur.minelib.hologram

import dev.nikdekur.minelib.pentity.PersonalEntityData
import org.bukkit.entity.Player
import org.bukkit.util.Vector

data class EntityWithHologramData(
    val entity: PersonalEntityData,
    val hologram: HologramData,
    val hologramOffset: (Player) -> Vector
)
