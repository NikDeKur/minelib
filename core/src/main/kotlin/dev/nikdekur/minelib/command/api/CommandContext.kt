package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.LanguagesService
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.koin.MineLibKoinComponent
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.extra.SimpleDataType
import dev.nikdekur.ndkore.extra.Tools
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.Contract
import org.koin.core.component.inject
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

open class CommandContext(val sender: CommandSender, val args: Array<String>)
    : CommandSender by sender, MineLibKoinComponent
{

    val languageService: LanguagesService by inject()


    val isPlayer = sender is Player
    val isConsole = !isPlayer


    val player: Player
        get() = if (sender is Player) sender else error("CommandSender is not a Player")
    var commandResult: CommandResult = CommandResult.SUCCESS
    val argsSize: Int = args.size
    val maxIndex: Int = argsSize - 1
    
    val position: Int = 0

    fun stop(): Nothing = throw ServerCommand.StopCommand()

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





    fun getStringOrNull() = args.getOrNull(position)
    
    fun getString(): String {
        return getStringOrNull() ?: throwUsage()
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

    fun tabComplete(associated: Map<Int, Collection<String>>): Collection<String>? {
        return associated[args.size - 1]
    }

    fun tabComplete(complete: Collection<String>, args: Array<String>): Collection<String>? {
        return if (args.size == 1) complete else null
    }

    @Contract("_, _, _, !null -> !null")
    fun checkInteger(def: Int?): Int? {
        val intStr = getStringOrNull() ?: return def
        val number = intStr.toIntOrNull()
        return if (number == null && def == null) {
            sendError(DefaultMSG.INCORRECT_NUMBER, "number" to intStr)
        } else number ?: def
    }

    @Contract("_, _, _, !null -> !null")
    fun checkDouble(def: Double?): Double? {
        val str = getStringOrNull() ?: return def
        val number = str.toDoubleOrNull()
        return if (number == null && def == null) {
            sendError(DefaultMSG.INCORRECT_NUMBER, "number" to str)
        } else number ?: def
    }

    fun unknownPlayer(playerName: String) {
        send(DefaultMSG.UNKNOWN_PLAYER, "name" to playerName)
    }



    fun formatSecondsValue(leftSecs: Long): String {
        var leftSecs = leftSecs.toDouble()
        val decimalFormat: DecimalFormat = if (leftSecs >= 5) {
            GTFIVE_SECONDS_FORMAT
        } else {
            if (leftSecs < 0.1) leftSecs = 0.1
            LTFIVE_SECONDS_FORMAT
        }
        return decimalFormat.format(leftSecs)
    }

    fun sendCooldown(
        cooldown: Duration,
        cooldownMSG: MSGHolder = DefaultMSG.COOLDOWN_ON_COMMAND,
        vararg placeholders: Pair<String, Any?>
    ) {
        val format = formatSecondsValue(cooldown.inWholeMilliseconds)
        if (placeholders.isEmpty()) {
            send(cooldownMSG, "time" to format)
        } else {
            send(
                cooldownMSG,
                *arrayOf(*placeholders)
                    .plus("time" to format)
            )
        }
    }

    fun sendCooldownAndStop(cooldown: Duration): Nothing {
        sendCooldown(cooldown)
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

    fun getByteOrNull() = getStringOrNull()?.toByteOrNull()
    fun getByte() = getByteOrNull() ?: throwUsage()

    fun getShortOrNull() = getStringOrNull()?.toShortOrNull()
    fun getShort() = getShortOrNull() ?: throwUsage()

    fun getIntOrNull() = getStringOrNull()?.toIntOrNull()
    fun getInt() = getIntOrNull() ?: throwUsage()

    fun getLongOrNull() = getStringOrNull()?.toLongOrNull()
    fun getLong() = getLongOrNull() ?: throwUsage()

    fun getFloatOrNull() = getStringOrNull()?.toFloatOrNull()
    fun getFloat() = getFloatOrNull() ?: throwUsage()

    fun getDoubleOrNull() = getStringOrNull()?.toDoubleOrNull()
    fun getDouble() = getDoubleOrNull() ?: throwUsage()

    fun getBigIntegerOrNull() = getStringOrNull()?.toBigIntegerOrNull()
    fun getBigInteger() = getBigIntegerOrNull() ?: throwUsage()

    fun getBigDecimalOrNull() = getStringOrNull()?.toBigDecimalOrNull()
    fun getBigDecimal() = getBigDecimalOrNull() ?: throwUsage()

    fun getBooleanOrNull(): Boolean? {
        val string = getStringOrNull()?.lowercase() ?: return null
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
    fun getBoolean() = getBooleanOrNull() ?: throwUsage()

    fun getCharOrNull() = getStringOrNull()?.firstOrNull()
    fun getChar() = getCharOrNull() ?: throwUsage()

    fun getDataType(default: SimpleDataType? = SimpleDataType.STRING): SimpleDataType {
        val value = getStringOrNull()
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


    fun getOfflinePlayerOrNull(): OfflinePlayer? {
        val offlinePlayerName = getStringOrNull() ?: return null

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
    fun getOfflinePlayer(): OfflinePlayer {
        val offlinePlayerName = getString()
        val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName)
        if (offlinePlayer is Player) {
            return offlinePlayer
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            unknownPlayer(offlinePlayerName)
        }
        return offlinePlayer
    }
    fun getOnlinePlayer(): Player {
        val name = getString()
        val player = Bukkit.getPlayer(name) ?: sendError(DefaultMSG.UNKNOWN_PLAYER, "name" to name)
        return player
    }

    fun getLocale(): Locale {
        val name = getString()
        return Locale.fromCode(name) ?: sendError(DefaultMSG.UNKNOWN_LANGUAGE_CODE_FORMAT, "code" to name)
    }


    companion object {
        @JvmStatic
        val LTFIVE_SECONDS_FORMAT = DecimalFormat("#.#")
        @JvmStatic
        val GTFIVE_SECONDS_FORMAT = DecimalFormat("#")


        val datetimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    }
}