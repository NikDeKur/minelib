@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE", "UNCHECKED_CAST")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.map.multi.MultiHashMap
import java.util.*


open class BuffsListImpl : MultiHashMap<RPGStat<*>, UUID, RPGBuff<*>>(), BuffsList {

    override fun getBuffs(): Collection<RPGBuff<*>> {
        return values.flatMap { it.values }
    }


    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> {
        val res = get(type) ?: return emptyList()
        return res.values as? Collection<RPGBuff<T>> ?: emptyList()
    }

    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<T>? {
        return get(type, id) as? RPGBuff<T>
    }



    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? {
        var value: T? = null
        val buffs = getBuffs(type)
        for (buff in buffs) {
            value = if (value == null) {
                buff.value
            } else {
                type.plus(value, buff.value)
            }
        }
        return value
    }

    final override inline fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T {
        return getBuffValueOrNull(type) ?: type.defaultValue
    }


    override fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>) {
        if (contains(buff.stat, buff.id)) return

        beforeAddBuff(buff)
        buff.beforeAdd(this)

        put(buff.stat, buff.id, buff)

        buff.afterAdd(this)
        afterAddBuff(buff)

    }

    final override inline fun <T : Comparable<T>> addBuff(type: RPGStat<T>, value: T) {
        addBuff(type.new(value))
    }


    
    override fun <T : Comparable<T>> removeBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> {
        val buffs = getBuffs(type)
        buffs.forEach(this::removeBuff)
        return buffs
    }

    override fun <T  : Comparable<T>> removeBuff(buff: RPGBuff<T>) {
        if (!contains(buff.stat, buff.id)) return

        beforeRemoveBuff(buff)
        buff.beforeRemove(this)

        remove(buff.stat, buff.id)

        afterRemoveBuff(buff)
        buff.afterRemove(this)
    }

    override fun forEachStat(action: (RPGStat<*>) -> Unit) {
        keys.forEach(action)
    }

    override fun forEachBuff(action: (RPGBuff<*>) -> Unit) {
        forEach { _, map ->
            map.forEach { (_, buff) ->
                action(buff)
            }
        }
    }

    override fun updateConditional(profile: RPGProfile) {
        forEachBuff {
            if (it !is RPGConditionalBuff) return@forEachBuff
            val apply = it.check(profile)
            if (apply)
                addBuff(it)
            else
                removeBuff(it)
        }
    }

    override fun compareByBuffsValue(other: BuffsList): Int {
        var res = 0
        forEachStat { stat ->
            val thisBuff = getBuffValueOrNull(stat)
            val otherBuff = other.getBuffValueOrNull(stat)
            if (thisBuff == null || otherBuff == null)
                return@forEachStat

            thisBuff as Comparable<Any>
            otherBuff as Comparable<Any>

            if (thisBuff > otherBuff)
                res++
            else if (thisBuff < otherBuff)
                res--
        }

        return res
    }

    override fun clear() {
        forEach { _, map ->
            map.forEach { (_, buff) ->
                buff.afterRemove(this)
            }
        }
        super.clear()
    }
}


class ImaginaryBuffsListImpl : MultiHashMap<RPGStat<*>, UUID, RPGBuff<*>>(), ImaginaryBuffsList {

    override fun getBuffs(): Collection<RPGBuff<*>> {
        return values.flatMap { it.values }
    }

    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> {
        val res = get(type) ?: return emptyList()
        return res.values as? Collection<RPGBuff<T>> ?: emptyList()
    }




    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<T>? {
        return get(type, id) as? RPGBuff<T>
    }

    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? {
        var value: T? = null
        val buffs = getBuffs(type)
        for (buff in buffs) {
            value = if (value == null) {
                buff.value
            } else {
                type.plus(value, buff.value)
            }
        }
        return value
    }

    override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T {
        return getBuffValueOrNull(type) ?: type.defaultValue
    }

    override fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>) {
        put(buff.stat, buff.id, buff)
    }

    override fun <T : Comparable<T>> addBuff(type: RPGStat<T>, value: T) {
        addBuff(type.new(value))
    }

    override fun <T : Comparable<T>> removeBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> {
        val remove = remove(type)?.values ?: return emptyList()
        return remove as? Collection<RPGBuff<T>> ?: emptyList()
    }

    override fun <T : Comparable<T>> removeBuff(buff: RPGBuff<T>) {
        remove(buff.stat, buff.id)
    }

    override fun clear() {
        super.clear()
    }


    override fun addTo(buffsList: BuffsList): List<RPGBuff<*>> {
        val added = ArrayList<RPGBuff<*>>()
        forEachBuff {
            val buff = it.copy()
            buffsList.addBuff(buff)
            added.add(buff)
        }
        return added
    }

    override fun forEachStat(action: (RPGStat<*>) -> Unit) {
        keys.forEach(action)
    }

    override fun forEachBuff(action: (RPGBuff<*>) -> Unit) {
        for (value in values) {
            for (value2 in value.values) {
                action(value2)
            }
        }
    }

    override fun updateConditional(profile: RPGProfile) {
        // Do nothing
    }

    override fun compareByBuffsValue(other: BuffsList): Int {
        var res = 0
        forEachStat { stat ->
            val thisBuff = getBuffValueOrNull(stat)
            val otherBuff = other.getBuffValueOrNull(stat)
            if (thisBuff == null || otherBuff == null)
                return@forEachStat

            thisBuff as Comparable<Any>
            otherBuff as Comparable<Any>

            if (thisBuff > otherBuff)
                res++
            else if (thisBuff < otherBuff)
                res--
        }

        return res
    }

}




open class AttachableBuffsListImpl : BuffsListImpl(), AttachableBuffsList {
    val attachesMap = HashMap<String, AttachedBuffsList>()

    override val attaches: Iterable<AttachedBuffsList>
        get() = attachesMap.values

    override fun getAttached(id: String): AttachedBuffsList? {
        return attachesMap[id]
    }

    override fun attach(id: String, buffs: ImaginaryBuffsList) {
        val added = buffs.addTo(this)
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

object EmptyBuffsList : BuffsList, MutableMap<String, MutableMap<UUID, RPGBuff<*>>> by HashMap(0) {
    override fun getBuffs(): Collection<RPGBuff<*>> = emptyList()
    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> = emptyList()
    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<T>? = null
    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? = null
    override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T = type.defaultValue
    override fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>) {}
    override fun <T : Comparable<T>> addBuff(type: RPGStat<T>, value: T) {}
    override fun <T : Comparable<T>> removeBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> = emptyList()
    override fun <T : Comparable<T>> removeBuff(buff: RPGBuff<T>) {}
    override fun clear() {}
    override fun forEachStat(action: (RPGStat<*>) -> Unit) {}
    override fun forEachBuff(action: (RPGBuff<*>) -> Unit) {}
    override fun updateConditional(profile: RPGProfile) {}
    override fun compareByBuffsValue(other: BuffsList) = 0
}

object EmptyImaginaryBuffsList : BuffsList by EmptyBuffsList, ImaginaryBuffsList {
    override fun addTo(buffsList: BuffsList) = emptyList<RPGBuff<*>>()
}

