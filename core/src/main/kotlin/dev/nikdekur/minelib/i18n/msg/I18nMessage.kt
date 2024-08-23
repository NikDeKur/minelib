package dev.nikdekur.minelib.i18n.msg

import dev.nikdekur.ndkore.`interface`.Snowflake


interface I18nMessage : Snowflake<String> {
    val defaultText: String
}