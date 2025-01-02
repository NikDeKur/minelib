package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.ndkore.ext.r_ClassMethods
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

abstract class RawTestCommand : ServiceServerCommand() {
    override val argsRequirement = 1
    override val usageMSG = DefaultMSG.INTERNAL_ERROR

    val optionsStr: List<String>
    val options: Map<String, Method> = r_ClassMethods
        .filter {
            val method = it.value
            method.parameterCount == 1 && method.parameterTypes[0] == CommandContext::class.java
        }
        .also { op -> optionsStr = op.values.map { it.name } }

    override fun CommandContext.onCommand() {
        val option = getString()
        val method = options[option]
        if (method == null) {
            sendSimpleMessage("Unknown test option '$option'! Use tab complete to see available options.")
            return
        }

        try {
            method.invoke(this@RawTestCommand, this)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String>? {
        return if (argsSize == 1) optionsStr.toMutableList() else null
    }
}