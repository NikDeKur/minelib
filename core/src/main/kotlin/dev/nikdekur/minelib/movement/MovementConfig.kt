package dev.nikdekur.minelib.movement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovementConfig(
    @SerialName("movement_update_delay")
    val movementUpdateDelay: Long = 10
)