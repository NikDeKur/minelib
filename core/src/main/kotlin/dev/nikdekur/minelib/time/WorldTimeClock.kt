package dev.nikdekur.minelib.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.bukkit.World

/**
 * A clock that uses the time of a world as its time source.
 *
 * If server TPS is 20, then 1 second in the world is 50 ticks.
 * (normal time rate)
 *
 * If server TPS distinctly differs from 20, then the time rate might increase or decrease.
 *
 * @param world The world to use as the time source.
 */
class WorldTimeClock(val world: World) : Clock {
    override fun now(): Instant {
        return Instant.fromEpochMilliseconds(world.fullTime * 50)
    }
}