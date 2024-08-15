package dev.nikdekur.minelib.i18n.locale

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocaleConfig(
    @SerialName("default_locale")
    @YamlComment(
        "Locale code of default language to use if player language is undefined",
        "Default is `en_us`"
    )
    val defaultLocale: String = "en_us",


    @SerialName("data_provider")
    @YamlComment(
        "Data Provider used to identify player locale.",
        "Plugins can create their own providers and you may set id of",
        "plugin provider you want to use",
        "Default is `bukkit`, which uses client locale"
    )
    val dataProvider: String = "bukkit"
)