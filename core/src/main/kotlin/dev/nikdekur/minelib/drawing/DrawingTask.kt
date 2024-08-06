package dev.nikdekur.minelib.drawing

import org.bukkit.scheduler.BukkitRunnable

data class DrawingTask(val manager: DrawingManager, val shape: DrawingShape) : BukkitRunnable() {

    val endTime = System.currentTimeMillis() + shape.liveTime

    fun startDrawing() {
        this.runTaskTimer(manager.app, shape.delay, shape.period)
    }

    override fun run() {
        if (System.currentTimeMillis() >= endTime || this.isCancelled) {
            shape.stop()
            return
        }

        if (shape.paused) return

        this.shape.draw()
    }
}