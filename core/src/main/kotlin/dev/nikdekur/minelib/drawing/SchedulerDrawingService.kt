package dev.nikdekur.minelib.drawing

import dev.nikdekur.minelib.drawing.shape.Shape
import dev.nikdekur.minelib.plugin.ServerPlugin
import java.util.*
import kotlin.time.Duration

class SchedulerDrawingService(override val app: ServerPlugin) : DrawingService {

    override val bindClass
        get() = DrawingService::class

    val shapes = HashMap<UUID, DrawingShape>()
    val tasks = HashMap<UUID, DrawingTask>()

    override fun addShape(shape: Shape, delay: Long, period: Long, liveTime: Duration): DrawingShape {
        val id = UUID.randomUUID()

        val drawingShape = DrawingShape(this, id, shape, delay, period, liveTime.inWholeMilliseconds)
        tasks[id] = DrawingTask(this, drawingShape).also(DrawingTask::startDrawing)
        shapes[id] = drawingShape

        return drawingShape
    }

    override fun removeShape(id: UUID) {
        shapes.remove(id)
        tasks.remove(id)?.cancel()
    }

    override fun findShape(id: UUID): DrawingShape? {
        return shapes[id]
    }


    override fun onDisable() {
        tasks.values.forEach { it.cancel() }
        shapes.clear()
        tasks.clear()
    }
}