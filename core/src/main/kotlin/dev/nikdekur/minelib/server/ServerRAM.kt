package dev.nikdekur.minelib.server

import dev.nikdekur.ndkore.memory.MemoryAmount
import dev.nikdekur.ndkore.memory.MemoryUnit
import dev.nikdekur.ndkore.memory.toBigInteger
import dev.nikdekur.ndkore.placeholder.Placeholder

data class ServerRAM(
    val max: MemoryAmount,
    val free: MemoryAmount,
    val used: MemoryAmount
) : Placeholder {

    override fun getPlaceholder(key: String): Any? {
        return when (key) {
            "max" -> max.toBigInteger(MemoryUnit.MiB).toString()
            "free" -> free.toBigInteger(MemoryUnit.MiB).toString()
            "used" -> used.toBigInteger(MemoryUnit.MiB).toString()
            else -> null
        }
    }
}