package dev.nikdekur.minelib.i18n.msg

import org.bukkit.command.CommandSender
import dev.nikdekur.minelib.ext.getLangMsg

interface MSGNameHolder : MSGPartHolder {

    val nameMSG: MSGHolder

    fun getName(sender: CommandSender): String {
        return sender.getLangMsg(nameMSG).text
    }
}