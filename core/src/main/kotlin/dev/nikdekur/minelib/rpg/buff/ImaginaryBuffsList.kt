package dev.nikdekur.minelib.rpg.buff

interface ImaginaryBuffsList : Collection<Pair<RPGBuff<*>, BuffParameters>> {

    fun addBuff(buff: RPGBuff<*>, parameters: BuffParameters = BuffParameters())
}