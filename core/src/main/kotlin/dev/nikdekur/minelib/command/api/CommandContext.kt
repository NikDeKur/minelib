package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.service.PluginComponent
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.extra.SimpleDataType
import dev.nikdekur.ndkore.service.inject
import dev.nikdekur.ornament.i18n.Key
import dev.nikdekur.ornament.i18n.Locale
import dev.nikdekur.ornament.i18n.toLocaleOrNull
import dev.nikdekur.ornament.i18n.withPlaceholders
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.Contract
import java.text.DecimalFormat
import java.time.OffsetDateTime
import kotlin.time.Duration

open class CommandContext(
    override val app: PluginApplication,
    val sender: CommandSender,
    val args: Array<String>
) : CommandSender by sender, PluginComponent {

    val i18n: I18nService by inject(MineLib.Qualifier)

    var commandResult: CommandResult = CommandResult.SUCCESS

    val isPlayer
        get() = sender is Player
    val isConsole
        get() = !isPlayer

    val player
        get() = if (sender is Player) sender else error("CommandSender is not a Player")

    val argsSize
        get() = args.size
    val maxIndex
        get() = argsSize - 1
    
    var position: Int = 0

    fun stop(): Nothing = throw ServerCommand.StopCommand()

    fun throwUsage(): Nothing {
        commandResult = CommandResult.THROW_USAGE
        stop()
    }

    fun selfAsPlayer(): Player {
        return sender as? Player ?: sendError(DefaultMSG.Command.ONLY_FOR_PLAYERS_SYNTAX)
    }


    fun send(key: Key) {
        i18n.sendLangMsg(sender, key)
    }

    fun sendError(key: Key): Nothing {
        send(key)
        stop()
    }





    fun getStringOrNull(): String? {
        return args.getOrNull(position).also {
            position++
        }
    }
    
    fun getString(): String {
        return getStringOrNull() ?: throwUsage()
    }




    fun <T> isExists(
        collection: Collection<T>,
        obj: T,
        onTrue: Key,
        onFalse: Key,
        vararg pair: Pair<String, Any>
    ): Boolean {
        val result = collection.contains(obj)
        if (result)
            send(onTrue)
        else
            send(onFalse)
        return result
    }

    fun <T> isNotExists(
        collection: Collection<T>,
        o: T,
        onTrue: Key,
        onFalse: Key,
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
            sendError(DefaultMSG.INCORRECT_NUMBER.withPlaceholders("number" to intStr))
        } else number ?: def
    }

    @Contract("_, _, _, !null -> !null")
    fun checkDouble(def: Double?): Double? {
        val str = getStringOrNull() ?: return def
        val number = str.toDoubleOrNull()
        return if (number == null && def == null) {
            sendError(DefaultMSG.INCORRECT_NUMBER.withPlaceholders("number" to str))
        } else number ?: def
    }

    fun unknownPlayer(playerName: String?) {
        if (playerName == null || playerName.isBlankOrEmpty())
            send(DefaultMSG.UNKNOWN_PLAYER_NO_NAME)
        else
            send(DefaultMSG.UNKNOWN_PLAYER)
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
        cooldownMSG: Key = DefaultMSG.Command.COOLDOWN,
        vararg placeholders: Pair<String, Any?>
    ) {
        val format = formatSecondsValue(cooldown.inWholeMilliseconds)
        if (placeholders.isEmpty()) {
            send(cooldownMSG)
        } else {
            send(
                cooldownMSG
            )
        }
    }

    fun sendCooldownAndStop(cooldown: Duration): Nothing {
        sendCooldown(cooldown)
        stop()
    }



    fun timedError(message: Key) {
        val time = app.clock.now().format(DateTimeFormat)

        return send(
            message
                .withPlaceholders("time" to time)
        )
    }

    fun timedErrorAndStop(message: Key): Nothing {
        timedError(message)
        stop()
    }


    fun internalError(comment: String): Nothing {
        sendError(
            DefaultMSG.INTERNAL_ERROR
                .withPlaceholders(
                    "time" to OffsetDateTime.now().toString(),
                    "comment" to comment
                )
        )
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
                sendError(
                    DefaultMSG.UNKNOWN_DATATYPE
                        .withPlaceholders("type" to "null")
                )
            }

        val type = SimpleDataType.fromStringOrNull(value)
        if (type == null && default == null) {
            sendError(
                DefaultMSG.UNKNOWN_DATATYPE
                    .withPlaceholders("type" to value)
            )
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

    fun getOnlinePlayerOrNull(): Player? {
        val name = getStringOrNull() ?: return null
        val player = Bukkit.getPlayer(name)
        if (player == null) {
            unknownPlayer(name)
            stop()
        }
        return player
    }

    fun getOnlinePlayer(): Player {
        val name = getString()
        val player = Bukkit.getPlayer(name) ?: sendError(
            DefaultMSG.UNKNOWN_PLAYER
                .withPlaceholders("name" to name)
        )
        return player
    }

    fun getLocale(): Locale {
        val name = getString()
        return name.toLocaleOrNull() ?: sendError(
            DefaultMSG.UNKNOWN_LOCALE_FORMAT
                .withPlaceholders("code" to name)
        )
    }

    fun checkPermission(permission: String) {
        if (sender.hasPermission(permission)) return
        sendError(
            DefaultMSG.Command.NOT_ENOUGH_PERMISSIONS
                .withPlaceholders("permission" to permission)
        )
    }


    companion object {
        @JvmStatic
        val LTFIVE_SECONDS_FORMAT = DecimalFormat("#.#")
        @JvmStatic
        val GTFIVE_SECONDS_FORMAT = DecimalFormat("#")


        val DateTimeFormat = DateTimeComponents.Format {
            date(LocalDate.Formats.ISO)

            char(' ')

            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }
}