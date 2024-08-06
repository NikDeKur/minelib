package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.MineLib.Companion.scheduler
import dev.nikdekur.minelib.rpg.stat.RPGStat

open class RPGTempBuff<T : Comparable<T>>(
    type: RPGStat<T>,
    value: T,
    val durationTicks: Long
) : RPGBuff<T>(type, value) {

    override fun beforeAdd(buffsList: BuffsList) {
        scheduler.runTaskLater(durationTicks) {
            buffsList.removeBuff(this)
        }
    }
}