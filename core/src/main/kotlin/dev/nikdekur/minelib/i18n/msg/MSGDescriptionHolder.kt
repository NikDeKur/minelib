package dev.nikdekur.minelib.i18n.msg

import dev.nikdekur.minelib.ext.getLangMsg
import org.bukkit.command.CommandSender

interface MSGDescriptionHolder : MSGPartHolder {

    val descriptionMSG: MessageReference

    fun getDescription(sender: CommandSender): String {
        return sender.getLangMsg(descriptionMSG).text
    }
}