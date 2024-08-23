package dev.nikdekur.minelib.i18n.config

import kotlinx.serialization.Serializable

@Serializable
data class MetaDataConfig(
    val defaults: Map<String, String> = emptyMap(),
)
