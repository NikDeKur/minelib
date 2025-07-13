package dev.nikdekur.minelib.i18n.locale

import dev.nikdekur.ornament.i18n.Key
import dev.nikdekur.ornament.i18n.Locale
import org.bukkit.entity.Player

interface LocaleProvider {
    fun translate(key: Key): String
}

data class StaticLocaleProvider(
    val locale: Locale,
    val translation: (key: Key) -> String
) : LocaleProvider {
    override fun translate(key: Key) = translation(key.withLocale(locale))
}

data class DynamicLocaleProvider(
    val player: Player,
    val locale: (player: Player) -> Locale,
    val translation: (key: Key) -> String
) : LocaleProvider {
    override fun translate(key: Key): String {
        val locale = locale(player)
        return translation(key.withLocale(locale))
    }
}