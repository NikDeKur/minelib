package dev.nikdekur.minelib.nms.protocol

import com.google.common.collect.MapMaker
import com.mojang.authlib.GameProfile
import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.event.packet.PacketReceiveEvent
import dev.nikdekur.minelib.event.packet.PacketSendEvent
import dev.nikdekur.minelib.event.packet.early.EarlyPacketReceiveEvent
import dev.nikdekur.minelib.event.packet.early.EarlyPacketSendEvent
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.ext.call
import io.netty.channel.*
import net.minecraft.server.v1_12_R1.Packet
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level

/**
 * Represents a very tiny alternative to ProtocolLib.
 *
 * It now supports intercepting packets during login and status ping (such as OUT_SERVER_PING)!
 *
 * @author Kristian
 * @author [Modified] Nik De Kur
 */
object ProtocolModule : MineLibModule, Listener {

    override fun onLoad() {
        try {
            registerChannelHandler()
            registerPlayers()
        } catch (ex: IllegalArgumentException) {
            // Damn you, late bind
            bLogger.info("[ProtocolModule] Delaying server channel injection due to late bind.")
            app.scheduler.runTask {
                registerChannelHandler()
                registerPlayers()
                bLogger.info("[ProtocolModule] Late bind injection successful.")
            }
        }
    }


    override fun onUnload() {
        if (!closed) {
            closed = true

            // Remove our handlers
            for (player in Bukkit.getOnlinePlayers()) {
                uninjectPlayer(player)
            }

            // Clean up Bukkit
            unregisterChannelHandler()
        }
    }

    var counter = AtomicInteger(0)

    // Speedup channel lookup
    private val channelLookup: MutableMap<String, Channel> = MapMaker().weakValues().makeMap()

    // Channels that have already been removed
    private val uninjectedChannels: MutableSet<Channel> = Collections.newSetFromMap(MapMaker().weakKeys().makeMap())

    // List of network markers
    private var networkManagers: List<Any>? = null

    // Injected channel handlers
    private val serverChannels: MutableList<Channel> = ArrayList()
    private var serverChannelHandler: ChannelInboundHandlerAdapter? = null
    private var beginInitProtocol: ChannelInitializer<Channel>? = null
    private var endInitProtocol: ChannelInitializer<Channel>? = null

    // Current handler name
    private val handlerName by lazy {
        "PacketInterceptor-${app.name}-${counter.incrementAndGet()}"
    }

    @Volatile
    var closed: Boolean = false
    

    private fun createServerChannelHandler() {
        // Handle connected channels
        endInitProtocol = object : ChannelInitializer<Channel>() {
            @Throws(Exception::class)
            override fun initChannel(channel: Channel) {
                try {
                    // This can take a while, so we need to stop the main thread from interfering
                    synchronized(networkManagers!!) {
                        // Stop injecting channels
                        if (!closed) {
                            channel.eventLoop().submit<PacketInterceptor> { injectChannelInternal(channel) }
                        }
                    }
                } catch (e: Exception) {
                    bLogger.log(Level.SEVERE, "Cannot inject incoming channel $channel", e)
                }
            }
        }

        // This is executed before Minecraft's channel handler
        beginInitProtocol = object : ChannelInitializer<Channel>() {
            @Throws(Exception::class)
            override fun initChannel(channel: Channel) {
                channel.pipeline().addLast(endInitProtocol)
            }
        }

        serverChannelHandler = object : ChannelInboundHandlerAdapter() {
            @Throws(Exception::class)
            override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                val channel = msg as Channel

                // Prepare to initialize ths channel
                channel.pipeline().addFirst(beginInitProtocol)
                ctx.fireChannelRead(msg)
            }
        }
    }
    
    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent) {
        if (closed) return

        val channel = getChannel(e.player)

        // Don't inject players that have been explicitly uninjected
        if (!uninjectedChannels.contains(channel)) {
            injectPlayer(e.player)
        }
    }


    private fun registerChannelHandler() {
        val mcServer = getMinecraftServer[Bukkit.getServer()]
        val serverConnection = getServerConnection[mcServer]
        var looking = true

        try {
            val field =
                Reflection.getParameterizedField(serverConnectionClass, MutableList::class.java, networkManagerClass)
            field.isAccessible = true

            @Suppress("UNCHECKED_CAST", "kotlin:S6531")
            networkManagers = field[serverConnection] as List<Any>
        } catch (ex: Exception) {
            bLogger.info("Encountered an exception checking list fields$ex")
            val method =
                Reflection.getTypedMethod(serverConnectionClass, null, MutableList::class.java, serverConnectionClass)

            @Suppress("UNCHECKED_CAST", "kotlin:S6531")
            networkManagers = method.invoke(null, serverConnection) as List<Any>
        }

        requireNotNull(networkManagers) { "Failed to obtain list of network managers" }
        // We need to synchronise against this list
        createServerChannelHandler()

        // Find the correct list, or implicitly throw an exception
        var i = 0
        while (looking) {
            val list = Reflection.getField(
                serverConnection.javaClass, MutableList::class.java, i
            )[serverConnection]

            for (item in list) {
                if (item !is ChannelFuture) break

                // Channel future that contains the server connection
                val serverChannel = item.channel()

                serverChannels.add(serverChannel)
                serverChannel.pipeline().addFirst(serverChannelHandler)
                looking = false
            }
            i++
        }
    }

    private fun unregisterChannelHandler() {
        if (serverChannelHandler == null) return

        for (serverChannel in serverChannels) {
            val pipeline = serverChannel.pipeline()

            // Remove channel handler
            serverChannel.eventLoop().execute {
                try {
                    pipeline.remove(serverChannelHandler)
                } catch (e: NoSuchElementException) {
                    // That's fine
                }
            }
        }
    }

    private fun registerPlayers() {
        for (player in Bukkit.getOnlinePlayers()) {
            injectPlayer(player)
        }
    }

    /**
     * Invoked when the server is starting to send a packet to a player.
     *
     *
     * Note that this is not executed on the main thread.
     *
     * @param receiver - the receiving player, NULL for early login/status packets.
     * @param channel - The channel that received the packet. Never NULL.
     * @param packet - the packet being sent.
     * @return The packet to send instead, or NULL to cancel the transmission.
     */
    fun onPacketOutAsync(receiver: Player?, channel: Channel, packet: Packet<*>): Packet<*>? {
        val event = if (receiver == null)
            EarlyPacketSendEvent(channel, packet)
        else
            PacketSendEvent(receiver, channel, packet)

        event.call()
        if (event.isCancelled) return null
        return event.packet
    }

    /**
     * Invoked when the server has received a packet from a given player.
     *
     *
     * Use [Channel.remoteAddress] to get the remote address of the client.
     *
     * @param sender - the player that sent the packet, NULL for early login/status packets.
     * @param channel - Channel that received the packet. Never NULL.
     * @param packet - the packet being received.
     * @return The packet to receive instead, or NULL to cancel.
     */
    fun onPacketInAsync(sender: Player?, channel: Channel, packet: Packet<*>): Packet<*>? {
        val event = if (sender == null)
            EarlyPacketReceiveEvent(channel, packet)
        else
            PacketReceiveEvent(sender, channel, packet)

        event.call()
        if (event.isCancelled) return null
        return event.packet
    }

    /**
     * Send a packet to a particular player.
     *
     *
     * Note that [.onPacketOutAsync] will be invoked with this packet.
     *
     * @param player - the destination player.
     * @param packet - the packet to send.
     */
    fun sendPacket(player: Player, packet: Packet<*>) {
        sendPacket(getChannel(player), packet)
    }

    /**
     * Send a packet to a particular client.
     *
     *
     * Note that [.onPacketOutAsync] will be invoked with this packet.
     *
     * @param channel - client identified by a channel.
     * @param packet - the packet to send.
     */
    fun sendPacket(channel: Channel, packet: Packet<*>) {
        channel.pipeline().writeAndFlush(packet)
    }

    /**
     * Pretend that a given packet has been received from a player.
     *
     *
     * Note that [.onPacketInAsync] will be invoked with this packet.
     *
     * @param player - the player that sent the packet.
     * @param packet - the packet that will be received by the server.
     */
    fun receivePacket(player: Player, packet: Any?) {
        receivePacket(getChannel(player), packet)
    }

    /**
     * Pretend that a given packet has been received from a given client.
     *
     *
     * Note that [.onPacketInAsync] will be invoked with this packet.
     *
     * @param channel - client identified by a channel.
     * @param packet - the packet that will be received by the server.
     */
    fun receivePacket(channel: Channel?, packet: Any?) {
        channel!!.pipeline().context("encoder").fireChannelRead(packet)
    }


    /**
     * Add a custom channel handler to the given player's channel pipeline, allowing us to intercept sent and received packets.
     *
     *
     * This will automatically be called when a player has logged in.
     *
     * @param player - the player to inject.
     */
    fun injectPlayer(player: Player) {
        injectChannelInternal(getChannel(player)).player = player
    }

    /**
     * Add a custom channel handler to the given channel.
     *
     * @param channel - the channel to inject.
     * @return The intercepted channel, or NULL if it has already been injected.
     */
    fun injectChannel(channel: Channel) {
        injectChannelInternal(channel)
    }

    /**
     * Add a custom channel handler to the given channel.
     *
     * @param channel - the channel to inject.
     * @return The packet interceptor.
     */
    private fun injectChannelInternal(channel: Channel): PacketInterceptor {
        try {
            var interceptor = channel.pipeline()[handlerName] as? PacketInterceptor

            // Inject our packet interceptor
            if (interceptor == null) {
                interceptor = PacketInterceptor()
                channel.pipeline().addBefore("packet_handler", handlerName, interceptor)
                uninjectedChannels.remove(channel)
            }

            return interceptor
        } catch (e: IllegalArgumentException) {
            // Try again
            return channel.pipeline()[handlerName] as PacketInterceptor
        }
    }

    /**
     * Retrieve the Netty channel associated with a player. This is cached.
     *
     * @param player - the player.
     * @return The Netty channel.
     */
    fun getChannel(player: Player): Channel {
        var channel = channelLookup[player.name]

        // Lookup channel again
        if (channel == null) {
            val connection = getConnection[getPlayerHandle.invoke(player)]
            val manager = getManager[connection]

            channelLookup[player.name] = getChannel[manager].also { channel = it }
        }

        return channel!!
    }

    /**
     * Uninject a specific player.
     *
     * @param player - the injected player.
     */
    fun uninjectPlayer(player: Player) {
        uninjectChannel(getChannel(player))
    }

    /**
     * Uninject a specific channel.
     *
     *
     * This will also disable the automatic channel injection that occurs when a player has properly logged in.
     *
     * @param channel - the injected channel.
     */
    fun uninjectChannel(channel: Channel) {
        // No need to guard against this if we're closing
        if (!closed) {
            uninjectedChannels.add(channel)
        }

        // See ChannelInjector in ProtocolLib, line 590
        channel.eventLoop().execute { channel.pipeline().remove(handlerName) }
    }

    /**
     * Determine if TinyProtocol have injected the given player.
     *
     * @param player - the player.
     * @return TRUE if it is, FALSE otherwise.
     */
    fun hasInjected(player: Player): Boolean {
        return hasInjected(getChannel(player))
    }

    /**
     * Determine if TinyProtocol have injected the given channel.
     *
     * @param channel - the channel.
     * @return TRUE if it is, FALSE otherwise.
     */
    fun hasInjected(channel: Channel?): Boolean {
        return channel!!.pipeline()[handlerName] != null
    }
    
    /**
     * Channel handler that is inserted into the player's channel pipeline, allowing us to intercept sent and received packets.
     *
     * @author Kristian
     */
    private class PacketInterceptor : ChannelDuplexHandler() {
        // Updated by the login event
        @Volatile
        var player: Player? = null

        override fun channelRead(ctx: ChannelHandlerContext, msgRaw: Any) {
            // Intercept channel
            val msg = msgRaw as Packet<*>
            var message: Packet<*>? = msg
            val channel = ctx.channel()
            handleLoginStart(channel, message)

            message = onPacketInAsync(player, channel, msg)

            if (message != null) {
                super.channelRead(ctx, message)
            }
        }

        override fun write(ctx: ChannelHandlerContext, msgRaw: Any, promise: ChannelPromise) {
            val msg = msgRaw as Packet<*>

            val message = onPacketOutAsync(player, ctx.channel(), msg)

            if (message != null) {
                super.write(ctx, message, promise)
            }
        }

        private fun handleLoginStart(channel: Channel, packet: Any?) {
            if (PACKET_LOGIN_IN_START.isInstance(packet)) {
                val profile = getGameProfile[packet]
                channelLookup[profile.name] = channel
            }
        }
    }
    // Required Minecraft classes
    private val entityPlayerClass: Class<*> =
        Reflection.getClass("{nms}.EntityPlayer", "net.minecraft.server.level.EntityPlayer")
    private val playerConnectionClass: Class<*> =
        Reflection.getClass("{nms}.PlayerConnection", "net.minecraft.server.network.PlayerConnection")
    private val networkManagerClass: Class<*> =
        Reflection.getClass("{nms}.NetworkManager", "net.minecraft.network.NetworkManager")

    // Used to look up a channel
    private val getPlayerHandle: Reflection.MethodInvoker =
        Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle")
    private val getConnection: Reflection.FieldAccessor<*> =
        Reflection.getField(entityPlayerClass, null, playerConnectionClass)
    private val getManager: Reflection.FieldAccessor<*> =
        Reflection.getField(playerConnectionClass, null, networkManagerClass)
    private val getChannel: Reflection.FieldAccessor<Channel> =
        Reflection.getField(networkManagerClass, Channel::class.java, 0)

    // Looking up ServerConnection
    private val minecraftServerClass: Class<out Any> =
        Reflection.getUntypedClass("{nms}.MinecraftServer", "net.minecraft.server.MinecraftServer")
    private val serverConnectionClass: Class<out Any> =
        Reflection.getUntypedClass("{nms}.ServerConnection", "net.minecraft.server.network.ServerConnection")
    private val getMinecraftServer: Reflection.FieldAccessor<out Any> =
        Reflection.getField("{obc}.CraftServer", minecraftServerClass, 0)
    private val getServerConnection: Reflection.FieldAccessor<out Any> =
        Reflection.getField(minecraftServerClass, serverConnectionClass, 0)

    // Packets we have to intercept
    private val PACKET_LOGIN_IN_START: Class<*> =
        Reflection.getClass("{nms}.PacketLoginInStart", "net.minecraft.network.protocol.login.PacketLoginInStart")
    private val getGameProfile: Reflection.FieldAccessor<GameProfile> =
        Reflection.getField(PACKET_LOGIN_IN_START, GameProfile::class.java, 0)
}