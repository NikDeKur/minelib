package dev.nikdekur.minelib.movement

import dev.nikdekur.ndkore.serial.LenientDurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class MovementConfig(
    @Serializable(with = LenientDurationSerializer::class)
    @SerialName("update_delay")
    val updateDelay: Duration = 0.5.seconds
)