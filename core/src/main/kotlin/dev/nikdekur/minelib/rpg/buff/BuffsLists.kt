package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.RPGManager
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat
import java.util.*

interface BuffsList : Comparable<BuffsList> {

    fun getBuffs(): Collection<RPGBuff<*>>

    fun <T : Comparable<T>> getBuffs(id: String): Collection<RPGBuff<T>>
    fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>>
    fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<*>?


    fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T?
    fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T


    fun <T : Comparable<T>> addBuff(type: RPGStat<T>, value: T)
    fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>)
    fun <T : Comparable<T>> addBuffs(buffs: Iterable<RPGBuff<T>>) {
        buffs.forEach { addBuff(it) }
    }
    fun addBuffs(buffs: ImaginaryBuffsList) {
        buffs.forEachBuff { addBuff(it) }
    }

    fun <T : Comparable<T>> removeBuffs(type: RPGStat<T>): Collection<RPGBuff<T>>
    fun <T : Comparable<T>> removeBuff(buff: RPGBuff<T>)


    fun <T : Comparable<T>> beforeAddBuff(buff: RPGBuff<T>) {
        // Do nothing by default
    }
    fun <T : Comparable<T>> afterAddBuff(buff: RPGBuff<T>) {
        // Do nothing by default
    }

    fun <T : Comparable<T>> beforeRemoveBuff(buff: RPGBuff<T>) {
        // Do nothing by default
    }
    fun <T : Comparable<T>> afterRemoveBuff(buff: RPGBuff<T>) {
        // Do nothing by default
    }

    fun beforeClear() {
        // Do nothing by default
    }
    fun clear()
    fun afterClear() {
        // Do nothing by default
    }

    fun forEachStat(action: (String) -> Unit)
    fun forEachBuff(action: (RPGBuff<*>) -> Unit)

    fun updateConditional(profile: RPGProfile)

    /**
     * Compare this BuffsList with other BuffsList by buffs value
     *
     * Take all buffs from this BuffsList and compare them with buffs from other BuffsList
     *
     * If this BuffsList has more buffs with higher value than other BuffsList, return positive number
     *
     * If this BuffsList has more buffs with lower value than other BuffsList, return negative number
     *
     * @param other BuffsList to compare with
     */
    fun compareByBuffsValue(other: BuffsList): Int {
        var res = 0
        forEachStat {
            val stat = RPGManager.getAnyType(it) ?: return@forEachStat
            val thisBuff = getBuffValueOrNull(stat)
            val otherBuff = other.getBuffValueOrNull(stat)
            if (thisBuff == null || otherBuff == null) {
                return@forEachStat
            }

            if (thisBuff > otherBuff)
                res++
            else if (thisBuff < otherBuff)
                res--
        }

        return res
    }


    /**
     * Compare this BuffsList with other BuffsList
     *
     * By default, use [compareByBuffsValue] to compare
     */
    override fun compareTo(other: BuffsList): Int {
        return compareByBuffsValue(other)
    }
}

interface ImaginaryBuffsList : BuffsList {
    fun addTo(buffsList: BuffsList): List<RPGBuff<*>>
}


data class AttachedBuffsList(val id: String, val buffs: List<RPGBuff<*>>)

interface AttachableBuffsList : BuffsList {
    val attaches: Iterable<AttachedBuffsList>

    fun getAttached(id: String): AttachedBuffsList?

    fun attach(id: String, buffs: ImaginaryBuffsList)
    fun detach(id: String)
    fun detachAll()
}