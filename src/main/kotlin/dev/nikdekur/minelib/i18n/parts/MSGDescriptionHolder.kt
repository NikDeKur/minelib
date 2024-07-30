package dev.nikdekur.minelib.i18n.parts

import dev.nikdekur.minelib.ext.getLangMsg

interface MSGDescriptionHolder : MSGPartHolder {

    val descriptionMSG: dev.nikdekur.minelib.i18n.MSGHolder

    fun getDescription(sender: org.bukkit.command.CommandSender): String {
        return sender.getLangMsg(descriptionMSG).text
    }
}