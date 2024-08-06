package dev.nikdekur.minelib.pentity

import org.bukkit.entity.Player
import org.bukkit.util.Vector

sealed interface ClickContext {
    val player: Player

    data class Left(
        override val player: Player
    ) : ClickContext

    data class Right(
        override val player: Player
    ) : ClickContext

    data class RightAt(
        override val player: Player,
        val at: Vector
    ) : ClickContext
}
