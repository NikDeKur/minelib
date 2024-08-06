package dev.nikdekur.minelib.scoreboard
/**
 * Assemble Style.
 *
 * @param isDescending  whether the positions are going down or up.
 * @param startNumber from where to loop from.
 */
enum class AssembleStyle(var isDescending: Boolean, var startNumber: Int) {
    KOHI(true, 15),
    VIPER(true, -1),
    MODERN(false, 1),
    CUSTOM(false, 0)

    ;

    fun reverse(): AssembleStyle {
        return descending(!this.isDescending)
    }

    fun descending(descending: Boolean): AssembleStyle {
        this.isDescending = descending
        return this
    }

    fun startNumber(startNumber: Int): AssembleStyle {
        this.startNumber = startNumber
        return this
    }
}
