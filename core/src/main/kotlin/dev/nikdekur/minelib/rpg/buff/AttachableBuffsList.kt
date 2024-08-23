package dev.nikdekur.minelib.rpg.buff

data class AttachedBuffsList(val id: String, val buffs: List<RPGBuff<*>>)

interface AttachableBuffsList : ActiveBuffsList {
    val attaches: Iterable<AttachedBuffsList>

    fun getAttached(id: String): AttachedBuffsList?

    fun attach(id: String, buffs: BuffsList)
    fun detach(id: String)
    fun detachAll()
}