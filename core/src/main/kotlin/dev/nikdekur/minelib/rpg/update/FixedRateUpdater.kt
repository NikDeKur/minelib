package dev.nikdekur.minelib.rpg.update

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.scheduler.Scheduler
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.time.Duration

abstract class FixedRateUpdater(
    val scheduler: Scheduler,
    val profile: RPGProfile
) : StatUpdater {

    override val id: UUID = UUID.randomUUID()

    abstract val frequency: Duration

    var task: BukkitTask? = null

    override fun start() {
        check(task == null) { "Regeneration $id already started" }
        task = scheduler.runTaskTimer(frequency, ::update)
    }

    override fun cancel() {
        task?.cancel()
        task = null
    }

    abstract fun update()
}