package dev.nikdekur.minelib.server

import dev.nikdekur.ndkore.memory.MemoryAmount
import dev.nikdekur.ndkore.memory.MemoryUnit
import dev.nikdekur.ndkore.placeholder.Placeholder

data class ServerRAM(
    val max: MemoryAmount,
    val free: MemoryAmount,
    val used: MemoryAmount
) : Placeholder {

    override val placeholderMap: MutableMap<String, Any>
        get() {
            return super.placeholderMap.also {
                it["max"] = max.convertTo(MemoryUnit.MB).amount
                it["free"] = free.convertTo(MemoryUnit.MB).amount
                it["used"] = used.convertTo(MemoryUnit.MB).amount
            }
        }
}