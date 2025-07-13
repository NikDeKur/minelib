/*
 * This file is part of FastBoard, licensed under the MIT License.
 *
 * Copyright (c) 2019-2023 MrMicky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.nikdekur.minelib.fastboard

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Array
import kotlin.math.min

/**
 * {@inheritDoc}
 */
open class FastBoard(player: Player) : FastBoardBase<String>(player) {
    /**
     * {@inheritDoc}
     */
    override fun updateTitle(title: String) {
        require(!(!VersionType.V1_13.isHigherOrEqual && title.length > 32)) { "Title is longer than 32 chars" }

        super.updateTitle(title)
    }

    /**
     * {@inheritDoc}
     */
    override fun updateLines(vararg lines: String) {

        if (!VersionType.V1_13.isHigherOrEqual) {
            var lineCount = 0
            for (s in lines) {
                require(s.length <= 30) { "Line $lineCount is longer than 30 chars" }
                lineCount++
            }
        }

        super.updateLines(*lines)
    }

    @Throws(Throwable::class)
    override fun sendLineChange(score: Int) {
        val maxLength = if (hasLinesMaxLength()) 16 else 1024
        val line = getLineByScore(score)
        var prefix: String?
        var suffix = ""

        if (line.isNullOrEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET
        } else if (line.length <= maxLength) {
            prefix = line
        } else {
            // Prevent splitting color codes
            val index = if (line[maxLength - 1] == ChatColor.COLOR_CHAR)
                (maxLength - 1)
            else
                maxLength
            prefix = line.substring(0, index)
            val suffixTmp = line.substring(index)
            var chatColor: ChatColor? = null

            if (suffixTmp.length >= 2 && suffixTmp[0] == ChatColor.COLOR_CHAR) {
                chatColor = ChatColor.getByChar(suffixTmp[1])
            }

            val color = ChatColor.getLastColors(prefix)
            val addColor = chatColor == null || chatColor.isFormat()

            suffix = (if (addColor) (color.ifEmpty { ChatColor.RESET.toString() }) else "") + suffixTmp
        }

        if (prefix.length > maxLength || suffix.length > maxLength) {
            // Something went wrong, just cut to prevent client crash/kick
            prefix = prefix.substring(0, min(maxLength, prefix.length))
            suffix = suffix.substring(0, min(maxLength, suffix.length))
        }

        sendTeamPacket(score, TeamMode.UPDATE, prefix, suffix)
    }

    @Throws(Throwable::class)
    override fun toMinecraftComponent(line: String?): Any? {
        if (line.isNullOrEmpty()) {
            return EMPTY_MESSAGE
        }

        return Array.get(MESSAGE_FROM_STRING.invoke(line), 0)
    }

    override fun serializeLine(value: String?): String? {
        return value
    }

    override fun emptyLine(): String {
        return ""
    }

    /**
     * Return if the player has a prefix/suffix characters limit.
     * By default, it returns true only in 1.12 or lower.
     * This method can be overridden to fix compatibility with some versions support plugin.
     *
     * @return max length
     */
    protected fun hasLinesMaxLength(): Boolean {
        return !VersionType.V1_13.isHigherOrEqual
    }


    companion object {
        private val MESSAGE_FROM_STRING: MethodHandle
        private val EMPTY_MESSAGE: Any?

        init {
            try {
                val lookup = MethodHandles.lookup()
                val craftChatMessageClass = FastReflection.obcClass("util.CraftChatMessage")
                MESSAGE_FROM_STRING =
                    lookup.unreflect(craftChatMessageClass.getMethod("fromString", String::class.java))
                EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0)
            } catch (t: Throwable) {
                throw ExceptionInInitializerError(t)
            }
        }
    }
}
