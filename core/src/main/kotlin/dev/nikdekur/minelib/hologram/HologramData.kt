package dev.nikdekur.minelib.hologram

import dev.nikdekur.minelib.pentity.ClickContext
import dev.nikdekur.minelib.utils.AbstractLocation
import org.bukkit.entity.Player

class HologramData(
    val onLeftClick: (ClickContext.Left) -> Unit,
    val onRightClick: (ClickContext.Right) -> Unit,
    val onRightAtClick: (ClickContext.RightAt) -> Unit,
    val shouldSpawn: (Player) -> Boolean,
    val getLocation: (Player) -> AbstractLocation,
    val getText: (Player) -> Collection<String>
)