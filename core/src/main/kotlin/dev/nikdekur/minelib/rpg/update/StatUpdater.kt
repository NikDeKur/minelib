package dev.nikdekur.minelib.rpg.update

import dev.nikdekur.ndkore.`interface`.Cancellable
import dev.nikdekur.ndkore.`interface`.Unique
import java.util.UUID

interface StatUpdater : Unique<UUID>, Cancellable {
    fun start()
}