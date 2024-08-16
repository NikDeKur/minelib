package dev.nikdekur.minelib.i18n.locale

import dev.nikdekur.minelib.i18n.locale.Locale.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Serializer::class)
data class Locale(
    val id: String,
    val region: String
) {
    override fun toString() = "Language.Code($code)"

    val code: String
        get() = "${id}_$region"

    companion object {
        /**
         * Reads the language code from the language code string.
         *
         * Example code: en_us, ru_ru
         *
         * If an error occurs while reading the language code, it will return null.
         *
         * @param code The language code string.
         * @return The language code, language id and language region.
         */
        @JvmStatic
        fun fromCode(code: String): Locale? {
            try {
                val languageData = code.split('_')
                if (languageData.size != 2) return null
                val name = languageData[0]
                val region = languageData[1]
                if (name.isEmpty() || region.isEmpty()) return null
                return Locale(name, region)
            } catch (e: Exception) {
                return null
            }
        }

        /**
         * Reads the language code from the language code string.
         *
         * Example code: en_us, ru_ru
         *
         * @param code The language code string.
         * @return The language code, language id and language region.
         * @throws IllegalArgumentException If the language code is invalid.
         */
        @JvmStatic
        fun fromCodeOrThrow(code: String): Locale {
            return fromCode(code) ?: throw IllegalArgumentException("Invalid language code: $code")
        }

        /**
         * Reads the language code from the file name.
         *
         * @param fileName The file name to read the language code from.
         * @return The language code, language id and language region.
         */
        @JvmStatic
        fun fromFileName(fileName: String): Locale? {
            val languageCode = fileName.substringBefore('.')
            return fromCode(languageCode)
        }


        val EN_US = Locale("en", "us")
    }


    object Serializer : KSerializer<Locale> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) {
            encoder.encodeString(value.code)
        }

        override fun deserialize(decoder: Decoder): Locale {
            return fromCodeOrThrow(decoder.decodeString())
        }
    }
}