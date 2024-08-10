package dev.nikdekur.minelib.drawing

import dev.nikdekur.minelib.drawing.shape.Shape
import dev.nikdekur.ndkore.`interface`.Snowflake
import java.util.*

data class DrawingShape(
    val manager: DrawingServiceImpl,
    override val id: UUID,
    val shape: Shape,
    val delay: Long,
    val period: Long,
    val liveTime: Long
) : Snowflake<UUID> {

    var paused = false


    /**
     * Manually calls the draw method of the shape
     *
     * Will draw the shape once and don't affect the drawing manager.
     *
     * @see Shape.draw
     */
    fun draw() {
        shape.draw()
    }

    /**
     * Stops the drawing of the shape
     *
     * Will remove the shape from the drawing manager and stop the drawing task.
     *
     * @see DrawingServiceImpl.removeShape
     */
    fun stop() {
        manager.removeShape(id)
    }
}