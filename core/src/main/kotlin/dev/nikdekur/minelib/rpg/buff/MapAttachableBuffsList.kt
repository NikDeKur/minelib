package dev.nikdekur.minelib.rpg.buff

import java.util.LinkedList

open class MapAttachableBuffsList : MapMutableBuffsList(), AttachableBuffsList {

    val attachesMap = HashMap<String, AttachedBuffsList>()

    override val attaches: Iterable<AttachedBuffsList>
        get() = attachesMap.values

    override fun getAttached(id: String): AttachedBuffsList? {
        return attachesMap[id]
    }

    override fun attach(id: String, buffs: Iterable<RPGBuffData<*>>) {
        if (attachesMap.containsKey(id)) return

        val added = LinkedList<RPGBuffData<*>>()
        buffs.forEach { data ->
            addBuff(data)
            added.add(data)
        }
        val attached = AttachedBuffsList(id, added)
        attachesMap[id] = attached
    }

    fun <T : Comparable<T>> add(buff: RPGBuff<T>, parameters: BuffParameters = BuffParameters()) =
        addBuff(buff.stat, buff.value, parameters)


    override fun detach(id: String) {
        val attached = attachesMap.remove(id) ?: return
        attached.buffs.forEach {
            removeBuff(it)
        }
    }

    override fun detachAll() {
        LinkedList(attachesMap.keys).forEach(this::detach)
    }

    override fun toString(): String {
        return "MapAttachableBuffsList(attaches=$attachesMap)"
    }
}