package dev.nikdekur.minelib.rpg.update

import dev.nikdekur.ndkore.`interface`.Cancellable
import dev.nikdekur.ndkore.`interface`.Snowflake
import java.util.UUID

interface StatUpdater : Snowflake<UUID>, Cancellable {
    fun start()
}