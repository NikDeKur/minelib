package dev.nikdekur.minelib.rpg.buff

import java.util.LinkedList

open class MapAttachableBuffsList : MapActiveBuffsList(), AttachableBuffsList {

    val attachesMap = HashMap<String, AttachedBuffsList>()

    override val attaches: Iterable<AttachedBuffsList>
        get() = attachesMap.values

    override fun getAttached(id: String): AttachedBuffsList? {
        return attachesMap[id]
    }

    override fun attach(id: String, buffs: ImaginaryBuffsList) {
        val added = LinkedList<RPGBuffData>()
        buffs.forEach { (buff, parameters) ->
            added.add(
                add(buff, parameters)
            )
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
}