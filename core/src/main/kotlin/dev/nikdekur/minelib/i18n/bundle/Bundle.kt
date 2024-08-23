package dev.nikdekur.minelib.i18n.bundle

import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.ndkore.`interface`.Snowflake
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParser
import dev.nikdekur.ndkore.placeholder.PlaceholderParser

interface Bundle : Snowflake<String> {

    val messages: Collection<I18nMessage>

    fun getMessage(
        locale: Locale,
        key: I18nMessage,
        vararg placeholders: Pair<String, Any?>,
        parser: PlaceholderParser = PatternPlaceholderParser.CURLY_BRACKET
    ): Message?
}