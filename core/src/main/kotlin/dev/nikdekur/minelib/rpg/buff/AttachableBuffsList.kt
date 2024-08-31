package dev.nikdekur.minelib.rpg.buff

data class AttachedBuffsList(val id: String, val buffs: List<RPGBuffData>)

interface AttachableBuffsList : ActiveBuffsList {
    val attaches: Iterable<AttachedBuffsList>

    fun getAttached(id: String): AttachedBuffsList?

    fun attach(id: String, buffs: ImaginaryBuffsList)
    fun detach(id: String)
    fun detachAll()
}