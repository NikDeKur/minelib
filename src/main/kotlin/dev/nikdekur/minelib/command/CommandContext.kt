package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.ext.msToSecs
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.DefaultMSG
import dev.nikdekur.minelib.i18n.Language
import dev.nikdekur.minelib.i18n.LanguagesManager
import dev.nikdekur.minelib.i18n.MSGHolder
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.extra.SimpleDataType
import dev.nikdekur.ndkore.extra.Tools
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.Contract
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class CommandContext(val sender: CommandSender, val args: Array<String>)
    : CommandSender by sender
{


    val isPlayer = sender is Player
    val isConsole = !isPlayer


    val player: Player
        get() = if (sender is Player) sender else error("CommandSender is not a Player")
    var commandResult: CommandResult = CommandResult.SUCCESS
    val argsSize: Int = args.size
    val maxIndex: Int = argsSize - 1

    fun stop(): Nothing {
        throw ServerCommand.StopCommand()
    }

    fun throwUsage(): Nothing {
        commandResult = CommandResult.THROW_USAGE
        stop()
    }


    fun send(msg: MSGHolder, vararg pairs: Pair<String, Any?>) {
        sender.sendLangMsg(msg, *pairs)
    }

    fun sendError(msg: MSGHolder, vararg pairs: Pair<String, Any?>): Nothing {
        send(msg, *pairs)
        stop()
    }





    fun getArgOrNull(pos: Int) = args.getOrNull(pos)
    fun getArg(pos: Int): String {
        return getArgOrNull(pos) ?: throwUsage()
    }




    fun <T> isExists(
        collection: Collection<T>,
        obj: T,
        onTrue: MSGHolder,
        onFalse: MSGHolder,
        vararg pair: Pair<String, Any>
    ): Boolean {
        val result = collection.contains(obj)
        if (result)
            send(onTrue, *pair)
        else
            send(onFalse, *pair)
        return result
    }

    fun <T> isNotExists(
        collection: Collection<T>,
        o: T,
        onTrue: MSGHolder,
        onFalse: MSGHolder,
        vararg pair: Pair<String, Any>
    ): Boolean {
        return !isExists(collection, o, onFalse, onTrue, *pair)
    }
//
//    fun hasBannedSymbols(input: String) {
//        val result = !Tools.hasOnlyLetNumHypUs(input)
//        if (result)
//            sendError(DefaultMSG.HAS_BANNED_SYMBOLS, "string" to input)
//    }

    fun tabComplete(associated: Map<Int, Collection<String>>): Collection<String>? {
        return associated[args.size - 1]
    }

    fun tabComplete(complete: Collection<String>, args: Array<String>): Collection<String>? {
        return if (args.size == 1) complete else null
    }

    @Contract("_, _, _, !null -> !null")
    fun checkInteger(argsPosition: Int, def: Int?): Int? {
        if (argsPosition >= args.size) {
            return def
        }
        val intStr = args[argsPosition]
        val number = intStr.toIntOrNull()
        return if (number == null && def == null) {
            sendError(DefaultMSG.INCORRECT_NUMBER, "number" to intStr)
        } else number ?: def
    }

    @Contract("_, _, _, !null -> !null")
    fun checkDouble(argsPosition: Int, def: Double?): Double? {
        if (argsPosition >= args.size) {
            return def
        }
        val str = args[argsPosition]
        val number = str.toDoubleOrNull()
        return if (number == null && def == null) {
            sendError(DefaultMSG.INCORRECT_NUMBER, "number" to str)
        } else number ?: def
    }

    fun unknownPlayer(playerName: String) {
        send(DefaultMSG.UNKNOWN_PLAYER, "name" to playerName)
    }



    fun formatSecondsValue(ms: Long): String {
        var leftSeconds = ms.msToSecs()
        val decimalFormat: DecimalFormat = if (leftSeconds >= 5) {
            GTFIVE_SECONDS_FORMAT
        } else {
            if (leftSeconds < 0.1) leftSeconds = 0.1
            LTFIVE_SECONDS_FORMAT
        }
        return decimalFormat.format(leftSeconds)
    }

    fun sendCooldown(
        cooldownLeftMs: Long,
        cooldownMSG: MSGHolder = DefaultMSG.COOLDOWN_ON_COMMAND,
        vararg placeholders: Pair<String, Any?>) {
        if (placeholders.isEmpty()) {
            send(cooldownMSG, "time" to formatSecondsValue(cooldownLeftMs))
        } else {
            send(
                cooldownMSG,
                *arrayOf(*placeholders)
                    .plus("time" to formatSecondsValue(cooldownLeftMs))
            )
        }
    }

    fun sendCooldownAndStop(cooldownLeftTicks: Long): Nothing {
        sendCooldown(cooldownLeftTicks)
        stop()
    }


    fun timedError(message: MSGHolder) {
        return send(message, "time" to datetimeFormatter.format(OffsetDateTime.now()))
    }

    fun timedErrorAndStop(message: MSGHolder): Nothing {
        timedError(message)
        stop()
    }


    fun internalError(comment: String) {
        sendError(DefaultMSG.INTERNAL_ERROR, "time" to Tools.packDateTimeBeautiful(), "comment" to comment)
    }

    fun getByteOrNull(pos: Int) = getArgOrNull(pos)?.toByteOrNull()
    fun getByte(pos: Int) = getByteOrNull(pos) ?: throwUsage()

    fun getShortOrNull(pos: Int) = getArgOrNull(pos)?.toShortOrNull()
    fun getShort(pos: Int) = getShortOrNull(pos) ?: throwUsage()

    fun getIntOrNull(pos: Int) = getArgOrNull(pos)?.toIntOrNull()
    fun getInt(pos: Int) = getIntOrNull(pos) ?: throwUsage()

    fun getLongOrNull(pos: Int) = getArgOrNull(pos)?.toLongOrNull()
    fun getLong(pos: Int) = getLongOrNull(pos) ?: throwUsage()

    fun getFloatOrNull(pos: Int) = getArgOrNull(pos)?.toFloatOrNull()
    fun getFloat(pos: Int) = getFloatOrNull(pos) ?: throwUsage()

    fun getDoubleOrNull(pos: Int) = getArgOrNull(pos)?.toDoubleOrNull()
    fun getDouble(pos: Int) = getDoubleOrNull(pos) ?: throwUsage()

    fun getBigIntegerOrNull(pos: Int) = getArgOrNull(pos)?.toBigIntegerOrNull()
    fun getBigInteger(pos: Int) = getBigIntegerOrNull(pos) ?: throwUsage()

    fun getBigDecimalOrNull(pos: Int) = getArgOrNull(pos)?.toBigDecimalOrNull()
    fun getBigDecimal(pos: Int) = getBigDecimalOrNull(pos) ?: throwUsage()

    fun getBooleanOrNull(pos: Int): Boolean? {
        if (maxIndex < pos) return null

        val string = getArgOrNull(pos)?.lowercase() ?: return null
        if (string == "true")
            return true
        else if (string == "false")
            return false

        if (string == "1")
            return true
        else if (string == "0")
            return false

        if (string == "yes" || string == "y")
            return true
        else if (string == "no" || string == "n")
            return false

        return null
    }
    fun getBoolean(pos: Int) = getBooleanOrNull(pos) ?: throwUsage()

    fun getCharOrNull(pos: Int) = getArgOrNull(pos)?.firstOrNull()
    fun getChar(pos: Int) = getCharOrNull(pos) ?: throwUsage()

    fun getDataType(pos: Int, default: SimpleDataType? = SimpleDataType.STRING): SimpleDataType {
        val value = getArgOrNull(pos)
            ?: return default ?: run {
                sendError(DefaultMSG.UNKNOWN_DATATYPE, "type" to "null")
            }

        val type = SimpleDataType.fromStringOrNull(value)
        if (type == null && default == null) {
            sendError(DefaultMSG.UNKNOWN_DATATYPE, "type" to value)
        } else if (type == null) {
            return default!!
        }

        return type
    }


    fun getOfflinePlayerOrNull(argsPosition: Int): OfflinePlayer? {
        val offlinePlayerName = getArgOrNull(argsPosition) ?: return null

        if (offlinePlayerName.isBlankOrEmpty()) {
            unknownPlayer(offlinePlayerName)
            stop()
        }

        @Suppress("DEPRECATION")
        val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName)
        if (offlinePlayer is Player) {
            return offlinePlayer
        }

        if (!offlinePlayer.hasPlayedBefore()) {
            unknownPlayer(offlinePlayerName)
            stop()
        }
        return offlinePlayer
    }
    @Suppress("DEPRECATION")
    fun getOfflinePlayer(argsPosition: Int): OfflinePlayer {
        val offlinePlayerName = getArg(argsPosition)
        val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName)
        if (offlinePlayer is Player) {
            return offlinePlayer
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            unknownPlayer(offlinePlayerName)
        }
        return offlinePlayer
    }
    fun getOnlinePlayer(pos: Int): Player {
        val name = getArg(pos)
        val player = Bukkit.getPlayer(name) ?: sendError(DefaultMSG.UNKNOWN_PLAYER, "name" to name)
        return player
    }

    fun getLanguage(pos: Int): Language {
        val name = getArg(pos)
        val code = Language.Code.fromCode(name) ?: sendError(DefaultMSG.UNKNOWN_LANGUAGE_CODE_FORMAT, "code" to name)
        val language = LanguagesManager[code] ?: sendError(DefaultMSG.UNKNOWN_LANGUAGE, "code" to name)
        return language
    }


    companion object {
        @JvmStatic
        val LTFIVE_SECONDS_FORMAT = DecimalFormat("#.#")
        @JvmStatic
        val GTFIVE_SECONDS_FORMAT = DecimalFormat("#")


        val datetimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    }
}