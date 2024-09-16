@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import java.util.LinkedList

data class AttachedBuffsList(val id: String, val buffs: List<RPGBuffData<*>>)

interface AttachableBuffsList : MutableBuffsList {
    val attaches: Iterable<AttachedBuffsList>

    fun getAttached(id: String): AttachedBuffsList?

    fun attach(id: String, buffs: Iterable<RPGBuffData<*>>)
    fun detach(id: String)
    fun detachAll()
}


inline fun AttachableBuffsList.attach(id: String, buffs: ImaginaryBuffsList) {
    val list = LinkedList<RPGBuffData<*>>()
    buffs.forEach {
        val data = RPGBuffData(
            buff = it.first,
            parameters = it.second
        )
        list.add(data)
    }
    attach(id, list)
}