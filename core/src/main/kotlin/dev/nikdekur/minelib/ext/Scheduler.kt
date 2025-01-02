@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.utils.isPrimaryThread
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/** Returns a [Duration] equal to this [Int] number of ticks. */
inline val Int.ticks: Duration get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)

/** Returns a [Duration] equal to this [Long] number of ticks. */
inline val Long.ticks: Duration get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)

inline val Duration.inWholeTicks: Long
    get() = inWholeMilliseconds / 50

/**
 * Run a task on the main thread.
 *
 * If the current thread is the main thread, the task will be executed immediately.
 *
 * If the current thread is not the main thread, the task will be executed on next tick via [Scheduler.runTask].
 *
 * @param runnable The task to run
 * @see Scheduler.runTask
 */
inline fun Scheduler.runSync(runnable: Runnable) {
    if (isPrimaryThread) {
        runnable.run()
    } else {
        runTask(runnable)
    }
}

/**
 * Run a task on the main thread.
 *
 * If the current thread is the main thread, the task will be executed immediately.
 *
 * If the current thread is not the main thread, the task will be executed on next tick via [Scheduler.runTask].
 *
 * @param runnable The task to run
 * @see Scheduler.runTask
 */
inline fun BukkitScheduler.runSync(plugin: Plugin, runnable: Runnable) {
    if (isPrimaryThread) {
        runnable.run()
    } else {
        runTask(plugin, runnable)
    }
}