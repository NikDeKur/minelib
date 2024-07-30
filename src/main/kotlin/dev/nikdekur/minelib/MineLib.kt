package dev.nikdekur.minelib

import dev.nikdekur.minelib.drawing.DrawingManager
import dev.nikdekur.minelib.gui.GUIManager
import dev.nikdekur.minelib.i18n.DefaultMSG
import dev.nikdekur.minelib.i18n.LanguagesManager
import dev.nikdekur.minelib.movement.MovementManager
import dev.nikdekur.minelib.nms.protocol.ProtocolModule
import dev.nikdekur.minelib.pentity.ServerPersonalEntityManager
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.RPGManager
import dev.nikdekur.minelib.scheduler.Scheduler
import kotlin.properties.Delegates

class MineLib : ServerPlugin() {

    override fun afterReload() {
        LanguagesManager.importEnumMessages(DefaultMSG::class.java)
    }

    override val components: Collection<Any>
        get() = listOf(
            // Listeners
            GUIManager,


            // Global modules
            ProtocolModule,
            ServerPersonalEntityManager,
            DrawingManager,
            MovementManager,
            LanguagesManager,
            RPGManager
        )



    companion object {
        @JvmStatic
        lateinit var instance: MineLib
            private set

        @JvmStatic
        @get:JvmName("getSchedulerInstance")
        @set:JvmName("setSchedulerInstance")
        var scheduler: Scheduler by Delegates.notNull()
            private set
    }
}