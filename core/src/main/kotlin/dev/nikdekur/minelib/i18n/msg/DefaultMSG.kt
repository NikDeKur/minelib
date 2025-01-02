package dev.nikdekur.minelib.i18n.msg

import dev.nikdekur.ornament.i18n.Bundle
import dev.nikdekur.ornament.i18n.Key as OrnamentKey

object DefaultMSG {

    val bundle = Bundle("default")

    fun Key(id: String, vararg default: String) =
        OrnamentKey(key = id)
            .withBundle(bundle)
            .withDefault(default.joinToString("\n"))

    /**
     * Placeholders:
     * 1. {string}
     */
    val HAS_BANNED_SYMBOLS = Key("has_banned_symbols", "&cThe string '{string}&c' contains forbidden symbols!")
    /**
     * Placeholders:
     * 1. {number}
     */
    val INCORRECT_NUMBER = Key("incorrect_number", "&cThe number '{number}&c' is not recognized!")
    /**
     * Placeholders:
     * 1. {player}
     */
    val UNKNOWN_PLAYER = Key("unknown_player", "&cPlayer '{name}&c' not found!")
    val UNKNOWN_PLAYER_NO_NAME = Key("unknown_player_no_name", "&cPlayer not found!")

    /**
     * Placeholders:
     * 1. {type}
     */
    val UNKNOWN_DATATYPE = Key("unknown_datatype", "&cData type '{type}&c' not found!")
    /**
     * Placeholders:
     * 1. {time}
     * 1. {comment}
     */
    val INTERNAL_ERROR = Key("internal_error", "&cAn internal error occurred! Contact the administration! [{time}] {comment}")

    /**
     * Placeholders:
     * 1. {code}
     */
    val UNKNOWN_LOCALE_FORMAT = Key("unknown_locale_format", "&cUnknown locale code format '{code}'! Example of correct format: 'en_us', 'en_gb'!")


    object Command {
        val TAB_ERROR = Key("command.tab_error", "&cAn unknown error occurred while tab-completing the command. Contact the administration! {time}")
        val ERROR = Key("command.error", "&cAn unknown error occurred while executing the command. Contact the administration! {time}")
        val COOLDOWN = Key("command.cooldown", "&cWait &6{seconds} &cseconds before executing this command!")
        val NOT_ENOUGH_PERMISSIONS = Key("command.not_enough_permissions", "&cYou do not have enough permissions to execute this command!")
        val ONLY_FOR_PLAYERS = Key("command.only_for_players", "&cThis command is only available for players!")
        val ONLY_FOR_PLAYERS_SYNTAX = Key("command.only_for_players_syntax", "&cThis syntax is only available for players!")
    }


    object Arrow {
        val NEXT = Key("arrow.next", "&6Next page")
        val PREV = Key("arrow.prev", "&6Previous page")
    }

    object Cmd {
        object MineLib {
            val Usage = Key("cmd.minelib.usage", "&cUsage: /minelib <reload>")
            val Info = Key("cmd.minelib.info",
                "&b~~~~~ MineLib &f{version} &7by &bNik De Kur ~~~~~",
                "",
                "&7- &bCommands: &f{commands}",
                "&7- &bListeners: &f{listeners}",
                "&7- &bServices: &f{services}",
                "&7- &bUptime: &f{uptime}",
                "",
                "&b- &bSub Commands:",
                "&7  - &b/minelib reload &7- Reloads the plugin",
                "&b~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            )
        }
    }

    object RPG {
        object Stat {
            object Speed {
                val NAME = Key("rpg.stat.speed.name", "Speed")
                val BUFF_NAME = Key("rpg.stat.speed.buff_name", "&f{buff.name} &6+{buff.value}")
            }
            
            object Health {
                val NAME = Key("rpg.stat.health.name", "Health")
                val BUFF_NAME = Key("rpg.stat.health.buff_name", "&f{buff.name}: &6+{buff.value}")
            }
            
            object MaxHealth {
                val NAME = Key("rpg.stat.max_health.name", "Max Health")
                val BUFF_NAME = Key("rpg.stat.max_health.buff_name", "&f{buff.name}: &6+{buff.value}")
            }
            
            object Protection {
                val NAME = Key("rpg.stat.protection.name", "Armor")
                val BUFF_NAME = Key("rpg.stat.protection.buff_name", "&f{buff.name}: &6+{buff.value}")
            }
            
            object Regeneration {
                val NAME = Key("rpg.stat.regeneration.name", "Regeneration")
                val BUFF_NAME = Key("rpg.stat.regeneration.buff_name", "&f{buff.name}: &6+{buff.value}")
            }
            
            object RegenerationMultiplier {
                val NAME = Key("rpg.stat.regeneration_multiplier.name", "Regeneration")
                val BUFF_NAME = Key("rpg.stat.regeneration_multiplier.buff_name", "&f{buff.name}: &6x{buff.value}")
            }
            
            object RegenerationDelay {
                val NAME = Key("rpg.stat.regeneration_delay.name", "Regeneration Delay")
                val BUFF_NAME = Key("rpg.stat.regeneration_delay.buff_name", "&f{buff.name}: &6-{buff.value}")
            }
            
            object Damage {
                val NAME = Key("rpg.stat.damage.name", "Damage")
                val BUFF_NAME = Key("rpg.stat.damage.buff_name", "&f{buff.name}: &6+{buff.value}")
            }
            
            object DamageMultiplier {
                val NAME = Key("rpg.stat.damage_multiplier.name", "Damage")
                val BUFF_NAME = Key("rpg.stat.damage_multiplier.buff_name", "&f{buff.name}: &6x{buff.value}")
            }
        }
    }



    @JvmStatic
    val keys: List<OrnamentKey>
        get() = mutableListOf<OrnamentKey>(
            HAS_BANNED_SYMBOLS,
            INCORRECT_NUMBER,
            UNKNOWN_PLAYER,
            UNKNOWN_PLAYER_NO_NAME,
            UNKNOWN_DATATYPE,
            INTERNAL_ERROR,
            UNKNOWN_LOCALE_FORMAT,

            Command.TAB_ERROR,
            Command.ERROR,
            Command.COOLDOWN,
            Command.NOT_ENOUGH_PERMISSIONS,
            Command.ONLY_FOR_PLAYERS,
            Command.ONLY_FOR_PLAYERS_SYNTAX,

            Arrow.NEXT,
            Arrow.PREV,

            Cmd.MineLib.Usage,
            Cmd.MineLib.Info,

            RPG.Stat.Speed.NAME,
            RPG.Stat.Speed.BUFF_NAME,
            RPG.Stat.Health.NAME,
            RPG.Stat.Health.BUFF_NAME,
            RPG.Stat.MaxHealth.NAME,
            RPG.Stat.MaxHealth.BUFF_NAME,
            RPG.Stat.Protection.NAME,
            RPG.Stat.Protection.BUFF_NAME,
            RPG.Stat.Regeneration.NAME,
            RPG.Stat.Regeneration.BUFF_NAME,
            RPG.Stat.RegenerationMultiplier.NAME,
            RPG.Stat.RegenerationMultiplier.BUFF_NAME,
            RPG.Stat.RegenerationDelay.NAME,
            RPG.Stat.RegenerationDelay.BUFF_NAME,
            RPG.Stat.Damage.NAME,
            RPG.Stat.Damage.BUFF_NAME,
            RPG.Stat.DamageMultiplier.NAME,
            RPG.Stat.DamageMultiplier.BUFF_NAME
        )
}