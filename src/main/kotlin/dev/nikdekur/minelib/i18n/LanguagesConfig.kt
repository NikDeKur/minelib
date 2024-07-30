package dev.nikdekur.minelib.i18n

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LanguagesConfig(
    @SerialName("enable_default_english")
    val enableDefaultEnglish: Boolean = true,
    @SerialName("default_language")
    val defaultLanguage: String? = null,
    @SerialName("data_provider")
    val dataProvider: String? = null
)