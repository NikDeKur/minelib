package dev.nikdekur.minelib.i18n

enum class DefaultMSG(override val defaultText: String) : MSGHolder {
    /**
     * Placeholders:
     * 1. {string}
     */
    HAS_BANNED_SYMBOLS("&cThe string '{string}&c' contains forbidden symbols!"),
    /**
     * Placeholders:
     * 1. {number}
     */
    INCORRECT_NUMBER("&cThe number '{number}&c' is not recognized!"),
    /**
     * Placeholders:
     * 1. {player}
     */
    UNKNOWN_PLAYER("&cPlayer '{name}&c' not found!"),
    UNKNOWN_PLAYER_NO_NAME("&cPlayer not found!"),

    /**
     * Placeholders:
     * 1. {type}
     */
    UNKNOWN_DATATYPE("&cData type '{type}&c' not found!"),
    /**
     * Placeholders:
     * 1. {time}
     * 1. {comment}
     */
    INTERNAL_ERROR("&cAn internal error occurred! Contact the administration! [{time}] {comment}"),
    /**
     * Placeholders:
     * 1. {time}
     */
    COMMAND_TAB_ERROR("&cAn unknown error occurred while tab-completing the command. Contact the administration! {time}"),
    /**
     * Placeholders:
     * 1. {time}
     */
    COMMAND_ERROR("&cAn unknown error occurred while executing the command. Contact the administration! {time}"),
    /**
     * Placeholders:
     * 1. {seconds}
     */
    COOLDOWN_ON_COMMAND("&cWait &6{seconds} &cseconds before executing this command!"),

    NOT_ENOUGH_PERMISSIONS("&cYou do not have enough permissions!"),
    ONLY_FOR_PLAYERS("&cThis command is only available for players!"),
    ONLY_FOR_PLAYERS_SYNTAX("&cThis syntax is only available for players!"),

    /**
     * Placeholders:
     * 1. {code}
     */
    UNKNOWN_LANGUAGE_CODE_FORMAT("&cUnknown language code format '{code}'! Example of correct format: 'en_us' or 'ru_ru'!"),

    /**
     * Placeholders:
     * 1. {code}
     */
    UNKNOWN_LANGUAGE("&cLanguage with '{code}' not found!"),

    ARROW_NEXT("&6Next page"),
    ARROW_PREV("&6Previous page"),


    // RPG SECTION
    RPG_STAT_NAME_SPEED_BONUS("Speed"),
    RPG_STAT_NAME_BUFF_SPEED_BONUS("&f{buff.name} &6+{buff.value}"),

    RPG_STAT_NAME_HEALTH("Max Health"),
    RPG_STAT_NAME_BUFF_HEALTH("&f{buff.name}: &6+{buff.value}"),

    RPG_STAT_NAME_MAX_HEALTH("Max Health"),
    RPG_STAT_NAME_BUFF_MAX_HEALTH("&f{buff.name}: &6+{buff.value}"),

    RPG_STAT_NAME_ARMOR("Armor"),
    RPG_STAT_NAME_BUFF_ARMOR("&f{buff.name}: &6+{buff.value}"),

    RPG_STAT_NAME_REGENERATION("Regeneration"),
    RPG_STAT_NAME_BUFF_REGENERATION("&f{buff.name}: &6+{buff.value}"),

    RPG_STAT_NAME_REGENERATION_MULTIPLIER("Regeneration"),
    RPG_STAT_NAME_BUFF_REGENERATION_MULTIPLIER("&f{buff.name}: &6x{buff.value}"),

    RPG_STAT_NAME_DAMAGE("Damage"),
    RPG_STAT_NAME_BUFF_DAMAGE("&f{buff.name}: &6+{buff.value}"),

    RPG_STAT_NAME_DAMAGE_MULTIPLIER("Damage"),
    RPG_STAT_NAME_BUFF_DAMAGE_MULTIPLIER("&f{buff.name}: &6x{buff.value}"),

    ;


    override val id = name


}