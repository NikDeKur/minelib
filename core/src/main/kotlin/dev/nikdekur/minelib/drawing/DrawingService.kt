package dev.nikdekur.minelib.drawing

import dev.nikdekur.minelib.drawing.shape.Shape
import dev.nikdekur.minelib.service.PluginService
import java.util.UUID
import kotlin.time.Duration

interface DrawingService : PluginService {

    /**
     * Add shape to drawing manager
     *
     * @param shape The shape to add
     * @param delay The delay before first drawing (ticks)
     * @param period The period between drawings (ticks)
     * @param liveTime The time the shape will be drawn (ms)
     * @return The id of the shape
     */
    fun addShape(shape: Shape, delay: Long, period: Long, liveTime: Duration): DrawingShape


    /**
     * Remove shape from drawing manager
     *
     * @param id The id of the shape
     */
    fun removeShape(id: UUID)


    /**
     * Find shape by id
     *
     * @param id The id of the shape
     * @return The shape or null if not found
     */
    fun findShape(id: UUID): DrawingShape?
}