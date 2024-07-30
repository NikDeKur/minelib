@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.ndkore.ext.map
import dev.nikdekur.ndkore.placeholder.Placeholder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class Language(
    val code: Code,
    val messages: MutableMap<String, String>
) : Placeholder {

    override val placeholderMap: MutableMap<String, Any>
        get() = messages.map({it.key}, {it})


    fun getIfPresent(id: String): String? {
        return messages[id]
    }

    fun getMessage(id: String): Message? {
        return getIfPresent(id)?.let { Message(it) }
    }

    operator fun get(id: String): Message {
        return getMessage(id) ?: run {
            bLogger.warning("Message '${id}' not found in language '$id'! Trying to use default message!")
            Message(LanguagesManager.getDefaultMessage(id))
        }
    }

    inline operator fun get(msg: MSGHolder) = get(msg.id)

    fun hasMessage(id: String): Boolean {
        return messages.containsKey(id)
    }

    fun addMessage(id: String, message: String) {
        require(LanguagesManager.hasDefaultMessage(id)) {
            "Cannot add message with id '$id' to language '$id' because it does not exist in the default language!. Use 'LanguageSystem.addDefaultMessage' before this!"
        }
        messages[id] = message
    }




    @Serializable(with = CodeSerializer::class)
    data class Code(
        val id: String,
        val region: String
    ) {

        override fun toString(): String {
            return "Language.Code($code)"
        }

        val code: String
            get() = "${id}_$region"

        companion object {

            @JvmField
            val EN_US = Code("en", "us")

            @JvmField
            val RU_RU = Code("ru", "ru")

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



