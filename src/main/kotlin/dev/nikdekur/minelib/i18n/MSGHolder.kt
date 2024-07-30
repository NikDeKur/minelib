package dev.nikdekur.minelib.i18n

import dev.nikdekur.ndkore.`interface`.Snowflake


interface MSGHolder : Snowflake<String> {
    val defaultText: String
}