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

import dev.nikdekur.minelib.fastboard.FastReflection.PacketConstructor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier
import java.util.stream.Stream

/**
 * Lightweight packet-based scoreboard API for Bukkit plugins.
 * It can be safely used asynchronously as everything is at packet level.
 *
 *
 * The project is on [GitHub](https://github.com/MrMicky-FR/FastBoard).
 *
 * @author MrMicky
 * @version 2.1.3
 */
abstract class FastBoardBase<T> protected constructor(val player: Player) {

    /**
     * Get the scoreboard id.
     *
     * @return the id
     */
    val id: String = "fb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt())

    private val lines: MutableList<T> = ArrayList<T>()
    private val scores: MutableList<T?> = ArrayList<T?>()

    /**
     * Get the scoreboard title.
     *
     * @return the scoreboard title
     */
    var title: T? = emptyLine()
        private set

    /**
     * Get if the scoreboard is deleted.
     *
     * @return true if the scoreboard is deleted
     */
    var isDeleted: Boolean = false
        private set

    /**
     * Creates a new FastBoard.
     *
     * @param player the owner of the scoreboard
     */
    init {

        try {
            sendObjectivePacket(ObjectiveMode.CREATE)
            sendDisplayObjectivePacket()
        } catch (t: Throwable) {
            throw RuntimeException("Unable to create scoreboard", t)
        }
    }

    /**
     * Update the scoreboard title.
     *
     * @param title the new scoreboard title
     * @throws IllegalArgumentException if the title is longer than 32 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    open fun updateTitle(title: T) {
        if (this.title == title) {
            return
        }

        this.title = title

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard title", t)
        }
    }

    /**
     * Get the scoreboard lines.
     *
     * @return the scoreboard lines
     */
    fun getLines(): MutableList<T?> {
        return ArrayList<T?>(this.lines)
    }

    /**
     * Get the specified scoreboard line.
     *
     * @param line the line number
     * @return the line
     * @throws IndexOutOfBoundsException if the line is higher than `size`
     */
    fun getLine(line: Int): T? {
        checkLineNumber(line, checkInRange = true, checkMax = false)

        return this.lines[line]
    }

    /**
     * Get how a specific line's score is displayed. On 1.20.2 or below, the value returned isn't used.
     *
     * @param line the line number
     * @return the text of how the line is displayed
     * @throws IndexOutOfBoundsException if the line is higher than `size`
     */
    fun getScore(line: Int): Optional<T & Any> {
        checkLineNumber(line, checkInRange = true, checkMax = false)

        return Optional.ofNullable(this.scores[line])
    }

    /**
     * Update a single scoreboard line.
     *
     * @param line the line number
     * @param text the new line text
     * @throws IndexOutOfBoundsException if the line is higher than [size() + 1][.size]
     */
    @Synchronized
    fun updateLine(line: Int, text: T) {
        updateLine(line, text, null)
    }

    /**
     * Update a single scoreboard line including how its score is displayed.
     * The score will only be displayed at 1.20.3 and higher.
     *
     * @param line the line number
     * @param text the new line text
     * @param scoreText the new line's score, if null will not change the current value
     * @throws IndexOutOfBoundsException if the line is higher than [size() + 1][.size]
     */
    @Synchronized
    fun updateLine(line: Int, text: T, scoreText: T?) {
        checkLineNumber(line, checkInRange = false, checkMax = false)

        try {
            if (line < size()) {
                this.lines[line] = text
                this.scores[line] = scoreText

                sendLineChange(getScoreByLine(line))

                if (customScoresSupported()) {
                    sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE)
                }

                return
            }

            val newLines = ArrayList(this.lines)
            val newScores = ArrayList(this.scores)

            if (line > size()) {
                for (i in size()..<line) {
                    newLines.add(emptyLine())
                    newScores.add(null)
                }
            }

            newLines.add(text)
            newScores.add(scoreText)

            updateLines(newLines, newScores)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard lines", t)
        }
    }

    /**
     * Remove a scoreboard line.
     *
     * @param line the line number
     */
    @Synchronized
    fun removeLine(line: Int) {
        checkLineNumber(line, checkInRange = false, checkMax = false)

        if (line >= size()) {
            return
        }

        val newLines = ArrayList<T>(this.lines)
        val newScores = ArrayList<T>(this.scores)
        newLines.removeAt(line)
        newScores.removeAt(line)
        updateLines(newLines, newScores)
    }

    /**
     * Update all the scoreboard lines.
     *
     * @param lines the new lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    open fun updateLines(vararg lines: T) {
        updateLines(listOf(*lines))
    }

    /**
     * Update the lines of the scoreboard
     *
     * @param lines the new scoreboard lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateLines(lines: Collection<T>) {
        updateLines(lines, null)
    }

    /**
     * Update the lines and how their score is displayed on the scoreboard.
     * The scores will only be displayed for servers at 1.20.3 and higher.
     *
     * @param lines the new scoreboard lines
     * @param scores the set for how each line's score should be, if null falls back to default (blank)
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalArgumentException if lines and scores are different size
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateLines(lines: Collection<T>, scores: Collection<T?>?) {
        checkLineNumber(lines.size, checkInRange = false, checkMax = true)

        require(!(scores != null && scores.size != lines.size)) { "The size of the scores must match the size of the board" }

        val oldLines = this.lines.toMutableList()
        this.lines.clear()
        this.lines.addAll(lines)

        val oldScores = this.scores.toList()
        this.scores.clear()
        this.scores.addAll(scores ?: Collections.nCopies<T?>(lines.size, null))

        val linesSize = this.lines.size

        try {
            if (oldLines.size != linesSize) {
                val oldLinesCopy = oldLines.toList()

                if (oldLines.size > linesSize) {
                    for (i in oldLinesCopy.size downTo linesSize + 1) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE)
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE)
                        oldLines.removeAt(0)
                    }
                } else {
                    for (i in oldLinesCopy.size until linesSize) {
                        sendScorePacket(i, ScoreboardAction.CHANGE)
                        sendTeamPacket(i, TeamMode.CREATE, null, null)
                    }
                }
            }

            for (i in 0 until linesSize) {
                if (getLineByScore(oldLines, i) != getLineByScore(i)) {
                    sendLineChange(i)
                }
                if (getLineByScore(oldScores, i) != getLineByScore(this.scores, i)) {
                    sendScorePacket(i, ScoreboardAction.CHANGE)
                }
            }
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard lines", t)
        }
    }

    /**
     * Update how a specified line's score is displayed on the scoreboard. A null value will reset the displayed
     * text back to default. The scores will only be displayed for servers on 1.20.3 and higher.
     *
     * @param line the line number
     * @param text the text to be displayed as the score. if null, no score will be displayed
     * @throws IllegalArgumentException if the line number is not in range
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateScore(line: Int, text: T?) {
        checkLineNumber(line, checkInRange = true, checkMax = false)

        this.scores[line] = text

        try {
            if (customScoresSupported()) {
                sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE)
            }
        } catch (e: Throwable) {
            throw RuntimeException("Unable to update line score", e)
        }
    }

    /**
     * Reset a line's score back to default (blank). The score will only be displayed for servers on 1.20.3 and higher.
     *
     * @param line the line number
     * @throws IllegalArgumentException if the line number is not in range
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun removeScore(line: Int) {
        updateScore(line, null)
    }

    /**
     * Update how all lines' scores are displayed. A value of null will reset the displayed text back to default.
     * The scores will only be displayed for servers on 1.20.3 and higher.
     *
     * @param texts the set of texts to be displayed as the scores
     * @throws IllegalArgumentException if the size of the texts does not match the current size of the board
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateScores(vararg texts: T?) {
        updateScores(texts.toList())
    }

    /**
     * Update how all lines' scores are displayed.  A null value will reset the displayed
     * text back to default (blank). Only available on 1.20.3+ servers.
     *
     * @param texts the set of texts to be displayed as the scores
     * @throws IllegalArgumentException if the size of the texts does not match the current size of the board
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateScores(texts: Collection<T?>) {
        require(this.scores.size == this.lines.size) { "The size of the scores must match the size of the board" }

        val newScores = ArrayList(texts)
        for (i in this.scores.indices) {
            if (this.scores[i] == newScores[i]) {
                continue
            }

            this.scores[i] = newScores[i]

            try {
                if (customScoresSupported()) {
                    sendScorePacket(getScoreByLine(i), ScoreboardAction.CHANGE)
                }
            } catch (e: Throwable) {
                throw RuntimeException("Unable to update scores", e)
            }
        }
    }

    /**
     * Get if the server supports custom scoreboard scores (1.20.3+ servers only).
     *
     * @return true if the server supports custom scores
     */
    fun customScoresSupported(): Boolean {
        return BLANK_NUMBER_FORMAT != null
    }

    /**
     * Get the scoreboard size (the number of lines).
     *
     * @return the size
     */
    fun size(): Int {
        return this.lines.size
    }

    /**
     * Delete this FastBoard, and will remove the scoreboard for the associated player if he is online.
     * After this, all uses of [.updateLines] and [.updateTitle] will throw an [IllegalStateException]
     *
     * @throws IllegalStateException if this was already call before
     */
    fun delete() {
        try {
            for (i in this.lines.indices) {
                sendTeamPacket(i, TeamMode.REMOVE)
            }

            sendObjectivePacket(ObjectiveMode.REMOVE)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to delete scoreboard", t)
        }

        this.isDeleted = true
    }

    @Throws(Throwable::class)
    protected abstract fun sendLineChange(score: Int)

    @Throws(Throwable::class)
    protected abstract fun toMinecraftComponent(value: T?): Any?

    protected abstract fun serializeLine(value: T?): String?

    protected abstract fun emptyLine(): T?

    private fun checkLineNumber(line: Int, checkInRange: Boolean, checkMax: Boolean) {
        require(line >= 0) { "Line number must be positive" }

        require(!(checkInRange && line >= lines.size)) { "Line number must be under " + lines.size }

        require(!(checkMax && line >= COLOR_CODES.size - 1)) { "Line number is too high: $line" }
    }

    protected fun getScoreByLine(line: Int): Int {
        return this.lines.size - line - 1
    }

    protected fun getLineByScore(score: Int): T? {
        return getLineByScore(this.lines, score)
    }

    protected fun getLineByScore(lines: List<T?>, score: Int): T? {
        return if (score < lines.size) lines[lines.size - score - 1] else null
    }

    @Throws(Throwable::class)
    protected fun sendObjectivePacket(mode: ObjectiveMode) {
        val packet = PACKET_SB_OBJ.invoke()

        setField(packet, String::class.java, this.id)
        setField(packet, Int::class.javaPrimitiveType, mode.ordinal)

        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, this.title, 1)
            setField(
                packet,
                Optional::class.java,
                Optional.empty<Any?>()
            ) // Number format for 1.20.5+, previously nullable

            if (VersionType.V1_8.isHigherOrEqual) {
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER)
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String::class.java, "", 1)
        }

        sendPacket(packet)
    }

    @Throws(Throwable::class)
    protected fun sendDisplayObjectivePacket() {
        val packet: Any = PACKET_SB_DISPLAY_OBJ.invoke()

        setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT) // Position
        setField(packet, String::class.java, this.id) // Score Name

        sendPacket(packet)
    }

    @Throws(Throwable::class)
    protected fun sendScorePacket(score: Int, action: ScoreboardAction) {
        if (VersionType.V1_17.isHigherOrEqual) {
            sendModernScorePacket(score, action)
            return
        }

        val packet: Any = PACKET_SB_SET_SCORE.invoke()

        setField(packet, String::class.java, COLOR_CODES[score], 0) // Player Name

        if (VersionType.V1_8.isHigherOrEqual) {
            val enumAction: Any? = if (action == ScoreboardAction.REMOVE)
                ENUM_SB_ACTION_REMOVE
            else
                ENUM_SB_ACTION_CHANGE
            setField(packet, ENUM_SB_ACTION, enumAction)
        } else {
            setField(packet, Int::class.javaPrimitiveType, action.ordinal, 1) // Action
        }

        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String::class.java, this.id, 1) // Objective Name
            setField(packet, Int::class.javaPrimitiveType, score) // Score
        }

        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendModernScorePacket(score: Int, action: ScoreboardAction?) {
        val objName: String? = COLOR_CODES[score]
        val enumAction: Any? = if (action == ScoreboardAction.REMOVE)
            ENUM_SB_ACTION_REMOVE
        else
            ENUM_SB_ACTION_CHANGE

        if (PACKET_SB_RESET_SCORE == null) { // Pre 1.20.3
            sendPacket(PACKET_SB_SET_SCORE.invoke(enumAction, this.id, objName, score))
            return
        }

        if (action == ScoreboardAction.REMOVE) {
            sendPacket(PACKET_SB_RESET_SCORE.invoke(objName, this.id))
            return
        }

        val scoreFormat = getLineByScore(this.scores, score)
        val format: Any? = if (scoreFormat != null)
            FIXED_NUMBER_FORMAT?.invoke(toMinecraftComponent(scoreFormat))
        else
            BLANK_NUMBER_FORMAT
        val scorePacket: Any = if (SCORE_OPTIONAL_COMPONENTS)
            PACKET_SB_SET_SCORE.invoke(objName, this.id, score, Optional.empty<Any?>(), Optional.of<Any?>(format!!))
        else
            PACKET_SB_SET_SCORE.invoke(objName, this.id, score, null, format)

        sendPacket(scorePacket)
    }

    @Throws(Throwable::class)
    protected fun sendTeamPacket(score: Int, mode: TeamMode, prefix: T? = null, suffix: T? = null) {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw UnsupportedOperationException()
        }

        val packet: Any = PACKET_SB_TEAM.invoke()

        setField(packet, String::class.java, this.id + ':' + score) // Team name
        setField(
            packet,
            Int::class.javaPrimitiveType,
            mode.ordinal,
            if (VERSION_TYPE == VersionType.V1_8) 1 else 0
        ) // Update mode

        if (mode == TeamMode.REMOVE) {
            sendPacket(packet)
            return
        }

        if (VersionType.V1_17.isHigherOrEqual) {
            val team: Any = PACKET_SB_SERIALIZABLE_TEAM!!.invoke()
            // Since the packet is initialized with null values, we need to change more things.
            setComponentField(team, null, 0) // Display name
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING) // Color
            setComponentField(team, prefix, 1) // Prefix
            setComponentField(team, suffix, 2) // Suffix
            setField(team, String::class.java, "always", 0) // Visibility
            setField(team, String::class.java, "always", 1) // Collisions
            setField(packet, Optional::class.java, Optional.of<Any?>(team))
        } else {
            setComponentField(packet, prefix, 2) // Prefix
            setComponentField(packet, suffix, 3) // Suffix
            setField(packet, String::class.java, "always", 4) // Visibility for 1.8+
            setField(packet, String::class.java, "always", 5) // Collisions for 1.9+
        }

        if (mode == TeamMode.CREATE) {
            setField(
                packet,
                MutableCollection::class.java,
                mutableListOf(COLOR_CODES[score])
            ) // Players in the team
        }

        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendPacket(packet: Any?) {
        check(!this.isDeleted) { "This FastBoard is deleted" }

        if (this.player.isOnline) {
            val entityPlayer: Any = PLAYER_GET_HANDLE.invoke(this.player)
            val playerConnection: Any = PLAYER_CONNECTION.invoke(entityPlayer)
            SEND_PACKET.invoke(playerConnection, packet)
        }
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(`object`: Any, fieldType: Class<*>?, value: Any?) {
        setField(`object`, fieldType, value, 0)
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(packet: Any, fieldType: Class<*>?, value: Any?, count: Int) {
        var i = 0
        for (field in PACKETS[packet.javaClass]!!) {
            if (field.type == fieldType && count == i++) {
                field[packet] = value
            }
        }
    }

    @Throws(Throwable::class)
    private fun setComponentField(packet: Any, value: T?, count: Int) {
        if (!VersionType.V1_13.isHigherOrEqual) {
            val line = if (value != null) serializeLine(value) else ""
            setField(packet, String::class.java, line, count)
            return
        }

        var i = 0
        for (field in PACKETS[packet.javaClass]!!) {
            if ((field.type == String::class.java || field.type == CHAT_COMPONENT_CLASS) && count == i++) {
                field[packet] = toMinecraftComponent(value)
            }
        }
    }

    enum class ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    enum class TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    enum class ScoreboardAction {
        CHANGE, REMOVE
    }

    internal enum class VersionType {
        V1_7, V1_8, V1_13, V1_17;

        val isHigherOrEqual: Boolean
            get() = VERSION_TYPE.ordinal >= ordinal
    }

    companion object {
        private val PACKETS: MutableMap<Class<*>?, Array<Field>?> = HashMap<Class<*>?, Array<Field>?>(8)
        protected val COLOR_CODES: Array<String> = ChatColor.entries.toTypedArray()
            .map { it.toString() }
            .toTypedArray()
        private val VERSION_TYPE: VersionType

        // Packets and components
        private val CHAT_COMPONENT_CLASS: Class<*>
        private val CHAT_FORMAT_ENUM: Class<*>
        private val RESET_FORMATTING: Any?
        private val PLAYER_CONNECTION: MethodHandle
        private val SEND_PACKET: MethodHandle
        private val PLAYER_GET_HANDLE: MethodHandle
        private val FIXED_NUMBER_FORMAT: MethodHandle?

        // Scoreboard packets
        private val PACKET_SB_OBJ: PacketConstructor
        private val PACKET_SB_DISPLAY_OBJ: PacketConstructor
        private val PACKET_SB_TEAM: PacketConstructor
        private val PACKET_SB_SERIALIZABLE_TEAM: PacketConstructor?
        private val PACKET_SB_SET_SCORE: MethodHandle
        private val PACKET_SB_RESET_SCORE: MethodHandle?
        private val SCORE_OPTIONAL_COMPONENTS: Boolean

        // Scoreboard enums
        private val DISPLAY_SLOT_TYPE: Class<*>
        private val ENUM_SB_HEALTH_DISPLAY: Class<*>?
        private val ENUM_SB_ACTION: Class<*>?
        private val BLANK_NUMBER_FORMAT: Any?
        private val SIDEBAR_DISPLAY_SLOT: Any?
        private val ENUM_SB_HEALTH_DISPLAY_INTEGER: Any?
        private val ENUM_SB_ACTION_CHANGE: Any?
        private val ENUM_SB_ACTION_REMOVE: Any?

        init {
            try {
                val lookup = MethodHandles.lookup()

                VERSION_TYPE = if (FastReflection.isRepackaged()) {
                    VersionType.V1_17
                } else if (FastReflection.nmsOptionalClass(null, "ScoreboardServer\$Action").isPresent
                    || FastReflection.nmsOptionalClass(null, "ServerScoreboard\$Method").isPresent
                ) {
                    VersionType.V1_13
                } else if (FastReflection.nmsOptionalClass(null, "IScoreboardCriteria\$EnumScoreboardHealthDisplay")
                        .isPresent
                    || FastReflection.nmsOptionalClass(null, "ObjectiveCriteria\$RenderType").isPresent
                ) {
                    VersionType.V1_8
                } else {
                    VersionType.V1_7
                }

                val gameProtocolPackage = "network.protocol.game"
                val craftPlayerClass = FastReflection.obcClass("entity.CraftPlayer")
                val entityPlayerClass = FastReflection.nmsClass("server.level", "EntityPlayer", "ServerPlayer")
                val playerConnectionClass =
                    FastReflection.nmsClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl")
                val packetClass = FastReflection.nmsClass("network.protocol", "Packet")
                val packetSbObjClass = FastReflection.nmsClass(
                    gameProtocolPackage,
                    "PacketPlayOutScoreboardObjective",
                    "ClientboundSetObjectivePacket"
                )
                val packetSbDisplayObjClass = FastReflection.nmsClass(
                    gameProtocolPackage,
                    "PacketPlayOutScoreboardDisplayObjective",
                    "ClientboundSetDisplayObjectivePacket"
                )
                val packetSbScoreClass = FastReflection.nmsClass(
                    gameProtocolPackage,
                    "PacketPlayOutScoreboardScore",
                    "ClientboundSetScorePacket"
                )
                val packetSbTeamClass = FastReflection.nmsClass(
                    gameProtocolPackage,
                    "PacketPlayOutScoreboardTeam",
                    "ClientboundSetPlayerTeamPacket"
                )
                val sbTeamClass = if (VersionType.V1_17.isHigherOrEqual)
                    FastReflection.innerClass(
                        packetSbTeamClass
                    ) { innerClass: Class<*>? -> !innerClass!!.isEnum }
                else
                    null
                val playerConnectionField = Arrays.stream(entityPlayerClass.getFields())
                    .filter { field: Field? -> field!!.type.isAssignableFrom(playerConnectionClass) }
                    .findFirst().orElseThrow(Supplier { NoSuchFieldException() })
                val sendPacketMethod = Stream.concat(
                    Arrays.stream(playerConnectionClass.getSuperclass().getMethods()),
                    Arrays.stream(playerConnectionClass.getMethods())
                )
                    .filter { m: Method? -> m!!.parameterCount == 1 && m.parameterTypes[0] == packetClass }
                    .findFirst().orElseThrow(Supplier { NoSuchMethodException() })
                val displaySlotEnum = FastReflection.nmsOptionalClass("world.scores", "DisplaySlot")
                CHAT_COMPONENT_CLASS = FastReflection.nmsClass("network.chat", "IChatBaseComponent", "Component")
                CHAT_FORMAT_ENUM = FastReflection.nmsClass(null, "EnumChatFormat", "ChatFormatting")
                DISPLAY_SLOT_TYPE = displaySlotEnum.orElse(Int::class.javaPrimitiveType)!!
                RESET_FORMATTING = FastReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21)
                SIDEBAR_DISPLAY_SLOT =
                    if (displaySlotEnum.isPresent) FastReflection.enumValueOf(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) else 1
                PLAYER_GET_HANDLE =
                    lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass))
                PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField)
                SEND_PACKET = lookup.unreflect(sendPacketMethod)
                PACKET_SB_OBJ = FastReflection.findPacketConstructor(packetSbObjClass, lookup)
                PACKET_SB_DISPLAY_OBJ = FastReflection.findPacketConstructor(packetSbDisplayObjClass, lookup)

                val numberFormat = FastReflection.nmsOptionalClass("network.chat.numbers", "NumberFormat")
                val packetSbSetScore: MethodHandle
                var packetSbResetScore: MethodHandle? = null
                var fixedFormatConstructor: MethodHandle? = null
                var blankNumberFormat: Any? = null
                var scoreOptionalComponents = false

                if (numberFormat.isPresent) { // 1.20.3
                    val blankFormatClass = FastReflection.nmsClass("network.chat.numbers", "BlankFormat")
                    val fixedFormatClass = FastReflection.nmsClass("network.chat.numbers", "FixedFormat")
                    val resetScoreClass = FastReflection.nmsClass(gameProtocolPackage, "ClientboundResetScorePacket")
                    val scoreType = MethodType.methodType(
                        Void.TYPE,
                        String::class.java,
                        String::class.java,
                        Int::class.javaPrimitiveType,
                        CHAT_COMPONENT_CLASS,
                        numberFormat.get()
                    )
                    val scoreTypeOptional = MethodType.methodType(
                        Void.TYPE,
                        String::class.java,
                        String::class.java,
                        Int::class.javaPrimitiveType,
                        Optional::class.java,
                        Optional::class.java
                    )
                    val removeScoreType = MethodType.methodType(Void.TYPE, String::class.java, String::class.java)
                    val fixedFormatType: MethodType = MethodType.methodType(Void.TYPE, CHAT_COMPONENT_CLASS)
                    val blankField = Arrays.stream(blankFormatClass.getFields())
                        .filter { f: Field? -> f!!.type == blankFormatClass }.findAny()
                    // Fields are of type Optional in 1.20.5+
                    val optionalScorePacket =
                        FastReflection.optionalConstructor(packetSbScoreClass, lookup, scoreTypeOptional)
                    fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType)
                    packetSbSetScore = if (optionalScorePacket.isPresent)
                        optionalScorePacket.get()
                    else
                        lookup.findConstructor(packetSbScoreClass, scoreType)
                    scoreOptionalComponents = optionalScorePacket.isPresent
                    packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType)
                    blankNumberFormat = if (blankField.isPresent) blankField.get()[null] else null
                } else if (VersionType.V1_17.isHigherOrEqual) {
                    val enumSbAction =
                        FastReflection.nmsClass("server", "ScoreboardServer\$Action", "ServerScoreboard\$Method")
                    val scoreType = MethodType.methodType(
                        Void.TYPE,
                        enumSbAction,
                        String::class.java,
                        String::class.java,
                        Int::class.javaPrimitiveType
                    )
                    packetSbSetScore = lookup.findConstructor(packetSbScoreClass, scoreType)
                } else {
                    packetSbSetScore = lookup.findConstructor(packetSbScoreClass, MethodType.methodType(Void.TYPE))
                }

                PACKET_SB_SET_SCORE = packetSbSetScore
                PACKET_SB_RESET_SCORE = packetSbResetScore
                PACKET_SB_TEAM = FastReflection.findPacketConstructor(packetSbTeamClass, lookup)
                PACKET_SB_SERIALIZABLE_TEAM =
                    if (sbTeamClass == null) null else FastReflection.findPacketConstructor(sbTeamClass, lookup)
                FIXED_NUMBER_FORMAT = fixedFormatConstructor
                BLANK_NUMBER_FORMAT = blankNumberFormat
                SCORE_OPTIONAL_COMPONENTS = scoreOptionalComponents

                for (clazz in listOf(
                    packetSbObjClass,
                    packetSbDisplayObjClass,
                    packetSbScoreClass,
                    packetSbTeamClass,
                    sbTeamClass
                )) {
                    if (clazz == null) {
                        continue
                    }
                    val fields = clazz.getDeclaredFields()
                        .filter { field: Field? -> !Modifier.isStatic(field!!.modifiers) }
                        .toTypedArray()
                    for (field in fields) {
                        field.setAccessible(true)
                    }
                    PACKETS.put(clazz, fields)
                }

                if (VersionType.V1_8.isHigherOrEqual) {
                    val enumSbActionClass = if (VersionType.V1_13.isHigherOrEqual)
                        "ScoreboardServer\$Action"
                    else
                        "PacketPlayOutScoreboardScore\$EnumScoreboardAction"
                    ENUM_SB_HEALTH_DISPLAY = FastReflection.nmsClass(
                        "world.scores.criteria",
                        "IScoreboardCriteria\$EnumScoreboardHealthDisplay",
                        "ObjectiveCriteria\$RenderType"
                    )
                    ENUM_SB_ACTION = FastReflection.nmsClass("server", enumSbActionClass, "ServerScoreboard\$Method")
                    ENUM_SB_HEALTH_DISPLAY_INTEGER = FastReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0)
                    ENUM_SB_ACTION_CHANGE = FastReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0)
                    ENUM_SB_ACTION_REMOVE = FastReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1)
                } else {
                    ENUM_SB_HEALTH_DISPLAY = null
                    ENUM_SB_ACTION = null
                    ENUM_SB_HEALTH_DISPLAY_INTEGER = null
                    ENUM_SB_ACTION_CHANGE = null
                    ENUM_SB_ACTION_REMOVE = null
                }
            } catch (t: Throwable) {
                throw ExceptionInInitializerError(t)
            }
        }
    }
}
