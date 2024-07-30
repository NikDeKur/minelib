@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.scheduler

import dev.nikdekur.ndkore.scheduler.Scheduler
import dev.nikdekur.ndkore.scheduler.SchedulerTask
import kotlinx.coroutines.runBlocking
import org.bukkit.scheduler.BukkitTask
import dev.nikdekur.minelib.scheduler.Scheduler as BScheduler

class BukkitScheduler(val original: BScheduler) : Scheduler {

    private inline fun convertToBlocking(crossinline task: suspend () -> Unit): () -> Unit {
        return {
            runBlocking { task() }
        }
    }

    private inline fun createTask(task: BukkitTask): SchedulerTask {
        return object : SchedulerTask {
            override val id: Int = task.taskId
            override fun isCancelled() = task.isCancelled
            override fun cancel() = task.cancel()
        }
    }


    override fun runTask(task: suspend () -> Unit): SchedulerTask {
        val bTask = original.runTask(convertToBlocking(task))
        return createTask(bTask)
    }

    override fun runTaskTimer(
        delay: Long,
        interval: Long,
        task: suspend () -> Unit,
    ): SchedulerTask {
        val bTask = original.runTaskTimer(delay, interval, convertToBlocking(task))
        return createTask(bTask)
    }

    override fun runTaskLater(
        delay: Long,
        task: suspend () -> Unit,
    ): SchedulerTask {
        val bTask = original.runTaskLater(delay, convertToBlocking(task))
        return createTask(bTask)
    }

    override fun cancelAllTasks() {
        original.cancelTasks()
    }
}