package dev.nikdekur.minelib.rpg.buff

class CollectionImaginaryBuffsList(
    val collection: MutableCollection<Pair<RPGBuff<*>, BuffParameters>>
) : ImaginaryBuffsList, Collection<Pair<RPGBuff<*>, BuffParameters>> by collection {

    override fun addBuff(
        buff: RPGBuff<*>,
        parameters: BuffParameters
    ) {
        collection.add(buff to parameters)
    }
}