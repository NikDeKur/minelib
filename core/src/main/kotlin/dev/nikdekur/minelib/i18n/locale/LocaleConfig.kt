package dev.nikdekur.minelib.i18n.locale

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocaleConfig(
    @SerialName("data_provider")
    @YamlComment(
        "Data Provider used to identify player locale.",
        "Plugins can create their own providers and you may set id of",
        "plugin provider you want to use",
        "Default is `bukkit`, which uses client locale"
    )
    val dataProvider: String = "bukkit"
)