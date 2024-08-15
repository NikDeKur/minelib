package dev.nikdekur.minelib.i18n.msg

import dev.nikdekur.ndkore.`interface`.Snowflake


interface MSGHolder : Snowflake<String> {
    val bundle: String
    val defaultText: String
}