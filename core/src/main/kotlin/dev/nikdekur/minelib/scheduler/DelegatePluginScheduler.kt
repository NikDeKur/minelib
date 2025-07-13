package dev.nikdekur.minelib.scheduler

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitWorker
import java.util.concurrent.Callable
import java.util.concurrent.Future

@JvmInline
@Suppress("unused")
value class DelegatePluginScheduler(val plugin: Plugin) : PluginScheduler {

    /**
     * Schedules a once off task to occur after a delay.
     *
     * This task will be executed by the main server thread.
     *
     * @param delay Delay in server ticks before executing th task
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncDelayedTask(delay: Long, task: Runnable): Int {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay)
    }

    /**
     * Schedules a once off task to occur after a delay.
     *
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing the task
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncDelayedTask(task: BukkitRunnable, delay: Long): Int {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay)
    }

    /**
     * Schedules a once off task to occur as soon as possible.
     *
     *
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncDelayedTask(task: Runnable): Int {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task)
    }

    /**
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncDelayedTask(task: BukkitRunnable): Int {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task)
    }

    /**
     * Schedules a repeating task.
     *
     *
     * This task will be executed by the main server thread.
     *
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncRepeatingTask(delay: Long, period: Long, task: Runnable): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delay, period)
    }

    /**
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleSyncRepeatingTask(task: BukkitRunnable, delay: Long, period: Long): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delay, period)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Schedules a once off task to occur after a delay. This task will be
     * executed by a thread managed by the scheduler.
     *
     * @param delay Delay in server ticks before executing the task
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleAsyncDelayedTask(delay: Long, task: Runnable): Int {
        return Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, task, delay)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Schedules a once off task to occur as soon as possible. This task will
     * be executed by a thread managed by the scheduler.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleAsyncDelayedTask(task: Runnable): Int {
        return Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, task)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Schedule a repeating task. This task will be executed by a thread
     * managed by the scheduler.
     *
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    override fun scheduleAsyncRepeatingTask(delay: Long, period: Long, task: Runnable): Int {
        return Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, task, delay, period)
    }

    /**
     * Calls a method on the main thread and returns a Future object. This
     * task will be executed by the main server thread.
     *
     *  * Note: The Future.get() methods must NOT be called from the main
     * thread.
     *  * Note2: There is at least an average of 10ms latency until the
     * isDone() method returns true.
     *
     * @param <T> The callable's return type
     * @param task Task to be executed
     * @return Future object related to the task
    </T> */
    override fun <T> callSyncMethod(task: Callable<T>): Future<T> {
        return Bukkit.getScheduler().callSyncMethod(plugin, task)
    }

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of tasks to be removed
     */
    override fun cancelTask(taskId: Int) {
        return Bukkit.getScheduler().cancelTask(taskId)
    }

    /**
     * Removes all tasks associated with a particular plugin from the
     * scheduler.
     *
     */
    override fun cancelTasks() {
        return Bukkit.getScheduler().cancelTasks(plugin)
    }

    /**
     * Removes all tasks from the scheduler.
     */
    override fun cancelAllTasks() {
        return Bukkit.getScheduler().cancelAllTasks()
    }

    /**
     * Check if the task currently running.
     *
     *
     * A repeating task might not be running currently, but will be running in
     * the future. A task that has finished, and does not repeat, will not be
     * running ever again.
     *
     *
     * Explicitly, a task is running if there exists a thread for it, and that
     * thread is alive.
     *
     * @param taskId The task to check.
     *
     *
     * @return If the task is currently running.
     */
    override fun isCurrentlyRunning(taskId: Int): Boolean {
        return Bukkit.getScheduler().isCurrentlyRunning(taskId)
    }

    /**
     * Check if the task queued to be run later.
     *
     *
     * If a repeating task is currently running, it might not be queued now
     * but could be in the future. A task that is not queued, and not running,
     * will not be queued again.
     *
     * @param taskId The task to check.
     *
     *
     * @return If the task is queued to be run.
     */
    override fun isQueued(taskId: Int): Boolean {
        return Bukkit.getScheduler().isQueued(taskId)
    }

    /**
     * Returns a list of all active workers.
     *
     *
     * This list contains async tasks that are being executed by separate
     * threads.
     *
     * @return Active workers
     */
    override val activeWorkers: MutableList<BukkitWorker>
        get() = Bukkit.getScheduler().activeWorkers

    /**
     * Returns a list of all pending tasks. The ordering of the tasks is not
     * related to their order of execution.
     *
     * @return Active workers
     */
    override val pendingTasks: MutableList<BukkitTask>
        get() = Bukkit.getScheduler().pendingTasks

    /**
     * Returns a task that will run on the next server tick.
     *
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTask(task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, task)
    }

    /**
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTask(task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, task)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Returns a task that will run asynchronously.
     *
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskAsynchronously(task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }

    /**
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskAsynchronously(task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }

    /**
     * Returns a task that will run after the specified number of server
     * ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskLater(delay: Long, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay)
    }

    /**
     * @param delay the ticks to wait before running the task
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskLater(delay: Long, task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskLaterAsynchronously(delay: Long, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay)
    }

    /**
     * @param delay the ticks to wait before running the task
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskLaterAsynchronously(delay: Long, task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay)
    }

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimer(delay: Long, period: Long, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
    }

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param period the ticks to wait between runs and before start
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimer(period: Long, task: Runnable): BukkitTask {
        return runTaskTimer(period, period, task)
    }

    /**
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimer(delay: Long, period: Long, task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task for the first
     * time
     * @param period the ticks to wait between runs
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimerAsynchronously(delay: Long, period: Long, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period)
    }

    /**
     * **Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.**
     *
     *
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param period the ticks to wait between runs and before the first run
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimerAsynchronously(period: Long, task: Runnable): BukkitTask {
        return runTaskTimerAsynchronously(period, period, task)
    }

    /**
     * @param delay the ticks to wait before running the task for the first time
     * @param period the ticks to wait between runs
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    override fun runTaskTimerAsynchronously(delay: Long, period: Long, task: BukkitRunnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period)
    }
}