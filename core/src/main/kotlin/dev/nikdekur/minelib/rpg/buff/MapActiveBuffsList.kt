package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.minelib.rpg.condition.RPGConditionalBuff
import dev.nikdekur.ndkore.map.MutableSetsMap
import dev.nikdekur.ndkore.map.add
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.map.remove

open class MapActiveBuffsList : MapBuffsList(), ActiveBuffsList {

    val conditionToBuffsMap: MutableSetsMap<ConditionType, RPGConditionalBuff<*>> = HashMap()

    override fun addBuff(buff: RPGBuff<*>) {
        map.put(buff.stat, buff.id, buff)
        if (buff is RPGConditionalBuff<*>) {
            buff.types.forEach {
                conditionToBuffsMap.add(it, buff)
            }
        }
    }

    override fun removeBuff(buff: RPGBuff<*>) {
        map.remove(buff.stat, buff.id)
        if (buff is RPGConditionalBuff<*>) {
            buff.types.forEach {
                conditionToBuffsMap[it]?.remove(buff)
            }
        }
    }


    override fun updateConditional(type: ConditionType) {
        conditionToBuffsMap[type]?.forEach {
            if (it.check()) addBuff(it)
            else removeBuff(it)
        }
    }

    override fun clear() {
        map.clear()
        conditionToBuffsMap.clear()
    }
}
