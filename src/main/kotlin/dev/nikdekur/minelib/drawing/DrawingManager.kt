package dev.nikdekur.minelib.drawing

import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.drawing.shape.Shape
import java.util.*

object DrawingManager : MineLibModule {

    val shapes = HashMap<UUID, DrawingShape>()
    val tasks = HashMap<UUID, DrawingTask>()

    /**
     * Add shape to drawing manager
     *
     * @param shape The shape to add
     * @param delay The delay before first drawing (ticks)
     * @param period The period between drawings (ticks)
     * @param liveTime The time the shape will be drawn (ms)
     * @return The id of the shape
     */
    fun addShape(shape: Shape, delay: Long, period: Long, liveTime: Long): DrawingShape {
        val id = UUID.randomUUID()

        val drawingShape = DrawingShape(this, id, shape, delay, period, liveTime)
        tasks[id] = DrawingTask(this, drawingShape).also(DrawingTask::startDrawing)
        shapes[id] = drawingShape

        return drawingShape
    }

    /**
     * Remove shape from drawing manager
     *
     * @param id The id of the shape
     */
    fun removeShape(id: UUID) {
        shapes.remove(id)
        tasks.remove(id)?.cancel()
    }

    /**
     * Find shape by id
     *
     * @param id The id of the shape
     * @return The shape or null if not found
     */
    fun findShape(id: UUID): DrawingShape? {
        return shapes[id]
    }


    override fun onUnload() {
        tasks.values.forEach { it.cancel() }
        shapes.clear()
        tasks.clear()
    }
}