package dev.nikdekur.minelib.rpg.buff

import java.util.LinkedList

class CollectionImaginaryBuffsList(
    val collection: MutableCollection<Pair<RPGBuff<*>, BuffParameters>> = LinkedList()
) : ImaginaryBuffsList, Collection<Pair<RPGBuff<*>, BuffParameters>> by collection {

    override fun addBuff(
        buff: RPGBuff<*>,
        parameters: BuffParameters
    ) {
        collection.add(buff to parameters)
    }

    override fun toString(): String {
        return "CollectionImaginaryBuffsList(collection=$collection)"
    }
}