package dev.nikdekur.minelib.server

import dev.nikdekur.ndkore.memory.MemoryAmount
import dev.nikdekur.ndkore.memory.MemoryUnit
import dev.nikdekur.ndkore.placeholder.Placeholder

data class ServerRAM(
    val max: MemoryAmount,
    val free: MemoryAmount,
    val used: MemoryAmount
) : Placeholder {

    override fun getPlaceholder(key: String): Any? {
        return when (key) {
            "max" -> max.convertTo(MemoryUnit.MB).amount
            "free" -> free.convertTo(MemoryUnit.MB).amount
            "used" -> used.convertTo(MemoryUnit.MB).amount
            else -> super.getPlaceholder(key)
        }
    }
}