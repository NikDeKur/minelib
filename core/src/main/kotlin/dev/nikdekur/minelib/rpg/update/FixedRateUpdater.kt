package dev.nikdekur.minelib.rpg.update

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.scheduler.PluginScheduler
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class FixedRateUpdater(
    val scheduler: PluginScheduler,
    val profile: RPGProfile
) : StatUpdater {

    override val id: UUID = UUID.randomUUID()

    abstract val frequency: Long

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