package dev.nikdekur.minelib.utils

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin

class UpdatedConsoleCommandSender(private val wrappedSender: ConsoleCommandSender, private val hide: Boolean) :
    ConsoleCommandSender {
    private val spigotWrapper: Spigot
    private val msgLog = StringBuilder()

    private inner class Spigot : CommandSender.Spigot() {
        /**
         * Sends this sender a chat component.
         *
         * @param component the components to send
         */
        override fun sendMessage(component: BaseComponent) {
            msgLog.append(BaseComponent.toLegacyText(component)).append('\n')
            wrappedSender.spigot().sendMessage()
        }

        /**
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         */
        override fun sendMessage(vararg components: BaseComponent) {
            msgLog.append(BaseComponent.toLegacyText(*components)).append('\n')
            wrappedSender.spigot().sendMessage(*components)
        }
    }

    val output: String
        get() = messageLogStripColor

    init {
        spigotWrapper = Spigot()
    }

    val messageLog: String
        get() = msgLog.toString()
    val messageLogStripColor: String
        get() = ChatColor.stripColor(msgLog.toString())

    fun clearMessageLog() {
        msgLog.setLength(0)
    }

    override fun sendMessage(message: String) {
        msgLog.append(message).append('\n')
        if (hide) {
            return
        }
        wrappedSender.sendMessage(message)
    }

    override fun sendMessage(messages: Array<String>) {
        wrappedSender.sendMessage(messages)
        for (message in messages) {
            msgLog.append(message).append('\n')
        }
    }

    override fun getServer(): Server {
        return wrappedSender.server
    }

    override fun getName(): String {
        return "OrderFulfiller"
    }

    override fun spigot(): CommandSender.Spigot {
        return spigotWrapper
    }

    override fun isConversing(): Boolean {
        return wrappedSender.isConversing
    }

    override fun acceptConversationInput(input: String) {
        wrappedSender.acceptConversationInput(input)
    }

    override fun beginConversation(conversation: Conversation): Boolean {
        return wrappedSender.beginConversation(conversation)
    }

    override fun abandonConversation(conversation: Conversation) {
        wrappedSender.abandonConversation(conversation)
    }

    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        wrappedSender.abandonConversation(conversation, details)
    }

    override fun sendRawMessage(message: String) {
        msgLog.append(message).append('\n')
        wrappedSender.sendRawMessage(message)
    }

    override fun isPermissionSet(name: String): Boolean {
        return wrappedSender.isPermissionSet(name)
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        return wrappedSender.isPermissionSet(perm)
    }

    override fun hasPermission(name: String): Boolean {
        return wrappedSender.hasPermission(name)
    }

    override fun hasPermission(perm: Permission): Boolean {
        return wrappedSender.hasPermission(perm)
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment {
        return wrappedSender.addAttachment(plugin, name, value)
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        return wrappedSender.addAttachment(plugin)
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        return wrappedSender.addAttachment(plugin, name, value, ticks)
    }

    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        return wrappedSender.addAttachment(plugin, ticks)
    }

    override fun removeAttachment(attachment: PermissionAttachment) {
        wrappedSender.removeAttachment(attachment)
    }

    override fun recalculatePermissions() {
        wrappedSender.recalculatePermissions()
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        return wrappedSender.effectivePermissions
    }

    override fun isOp(): Boolean {
        return wrappedSender.isOp
    }

    override fun setOp(value: Boolean) {
        wrappedSender.isOp = value
    }
}
