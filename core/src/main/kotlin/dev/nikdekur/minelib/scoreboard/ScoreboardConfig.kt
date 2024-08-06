package dev.nikdekur.minelib.scoreboard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardConfig(
    @SerialName("update_delay")
    val updateDelay: Long = 10L,
    val style: AssembleStyle = AssembleStyle.MODERN
)