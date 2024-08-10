package dev.nikdekur.minelib.drawing

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.drawing.shape.Shape
import java.util.*
import kotlin.time.Duration

class DrawingServiceImpl(override val app: MineLib) : DrawingService, MineLibModule {

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


    override fun onUnload() {
        tasks.values.forEach { it.cancel() }
        shapes.clear()
        tasks.clear()
    }
}