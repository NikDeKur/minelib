package dev.nikdekur.minelib.i18n

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface Language {

    val code: Code

    fun getMessage(msg: MSGHolder): Message?

    operator fun get(msg: MSGHolder): Message

    @Serializable(with = CodeSerializer::class)
    data class Code(
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
            fun fromCode(code: String): Code? {
                try {
                    val languageData = code.split('_')
                    if (languageData.size != 2) return null
                    val name = languageData[0]
                    val region = languageData[1]
                    if (name.isEmpty() || region.isEmpty()) return null
                    return Code(name, region)
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
            fun fromCodeOrThrow(code: String): Code {
                return fromCode(code) ?: throw IllegalArgumentException("Invalid language code: $code")
            }

            /**
             * Reads the language code from the file name.
             *
             * @param fileName The file name to read the language code from.
             * @return The language code, language id and language region.
             */
            @JvmStatic
            fun fromFileName(fileName: String): Code? {
                val languageCode = fileName.substringBefore('.')
                return fromCode(languageCode)
            }


            val EN_US = Code("en", "us")
        }
    }
}

object CodeSerializer : KSerializer<Language.Code> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Language.Code", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Language.Code) {
        encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): Language.Code {
        return Language.Code.fromCodeOrThrow(decoder.decodeString())
    }
}
