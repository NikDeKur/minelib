package dev.nikdekur.minelib.i18n.msg

import dev.nikdekur.minelib.ext.getLangMsg
import org.bukkit.command.CommandSender

interface MSGNameHolder : MSGPartHolder {

    val nameMSG: MessageReference

    fun getName(sender: CommandSender): String {
        return sender.getLangMsg(nameMSG).text
    }
}