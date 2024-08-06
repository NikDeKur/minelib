package dev.nikdekur.minelib.i18n.parts

import org.bukkit.command.CommandSender
import dev.nikdekur.minelib.ext.getLangMsg

interface MSGNameHolder : MSGPartHolder {

    val nameMSG: dev.nikdekur.minelib.i18n.MSGHolder

    fun getName(sender: CommandSender): String {
        return sender.getLangMsg(nameMSG).text
    }
}