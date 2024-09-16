package dev.nikdekur.minelib.i18n.bundle

import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.ndkore.`interface`.Snowflake

interface Bundle : Snowflake<String> {

    val messages: Collection<I18nMessage>

    fun getMessage(
        locale: Locale,
        key: I18nMessage
    ): Message?
}