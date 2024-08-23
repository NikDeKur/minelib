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
    override fun toString() = "Locale(code='$code')"

    val code: String
        get() = "${id}_$region"

    companion object {

        /**
         * Reads the locale from the locale code string.
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
            val codeData = code.split('_')
            if (codeData.size != 2) return null
            val name = codeData[0]
            val region = codeData[1]
            if (name.isEmpty() || region.isEmpty()) return null
            return Locale(name, region)
        }

        /**
         * Reads the locale from the locale code string.
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
