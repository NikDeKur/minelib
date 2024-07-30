package dev.nikdekur.minelib.rpg.update

import dev.nikdekur.minelib.MineLib.Companion.scheduler
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class FixedRateUpdater(val profile: RPGProfile) : StatUpdater {

    override val id: UUID = UUID.randomUUID()

    abstract val frequencyTicks: Long

    var task: BukkitTask? = null

    override fun start() {
        check(task == null) { "Regeneration $id already started" }
        task = scheduler.runTaskTimer(frequencyTicks, ::update)
    }

    override fun cancel() {
        task?.cancel()
        task = null
    }

    abstract fun update()
}