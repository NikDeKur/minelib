package dev.nikdekur.minelib.rpg.buff

import java.util.LinkedList

open class MapAttachableBuffsList : MapActiveBuffsList(), AttachableBuffsList {

    val attachesMap = HashMap<String, AttachedBuffsList>()

    override val attaches: Iterable<AttachedBuffsList>
        get() = attachesMap.values

    override fun getAttached(id: String): AttachedBuffsList? {
        return attachesMap[id]
    }

    override fun attach(id: String, buffs: BuffsList) {
        val added = LinkedList<RPGBuff<*>>()
        buffs.forEach {
            added.add(it)
            addBuff(it)
        }
        val attached = AttachedBuffsList(id, added)
        attachesMap[id] = attached
    }


    override fun detach(id: String) {
        val attached = attachesMap.remove(id) ?: return
        attached.buffs.forEach {
            removeBuff(it)
        }
    }

    override fun detachAll() {
        LinkedList(attachesMap.keys).forEach(this::detach)
    }
}