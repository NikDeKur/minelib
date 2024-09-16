@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg


import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT
import dev.nikdekur.minelib.ext.editNBT
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.item.NBTRPGItem
import dev.nikdekur.minelib.rpg.item.RPGItem
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.ext.getInstanceFieldOrNull
import dev.nikdekur.ndkore.reflect.ClassPathClassFinder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

class RuntimeRPGService(
    override val app: ServerPlugin
) : PluginService(), RPGService {

    override val bindClass
        get() = RPGService::class


    lateinit var json: Json
    lateinit var conditionSerializer: PolymorphicModuleBuilder<Condition<*>>
    lateinit var anySerializer: PolymorphicModuleBuilder<Any>

    val stats: MutableMap<String, RPGStat<*>> = HashMap()
    val itemsCache: MutableMap<UUID, RPGItem> = ConcurrentHashMap()


    override fun onEnable() {
        json = Json {
            serializersModule = SerializersModule {
                polymorphic(Condition::class) {
                    conditionSerializer = this
                }

                polymorphic(Any::class) {
                    anySerializer = this
                }

                contextual(RPGStat::class, RPGStat.Serializer(this@RuntimeRPGService))
            }
        }

        ClassPathClassFinder.find(app.clazzLoader, STATS_PACKAGE) { it: Class<*> ->
            if (!RPGStat::class.java.isAssignableFrom(it)) return@find

            val instance = it.getInstanceFieldOrNull() as? RPGStat<*> ?: return@find
            registerStat(instance)
        }
    }

    override fun onDisable() {
        stats.clear()
        itemsCache.clear()
    }


    override fun registerStat(type: RPGStat<*>) {
        stats.addById(type)
    }

    override fun getStat(id: String): RPGStat<*>? {
        return stats[id]
    }

    override fun registerCondition(clazz: Class<out Condition<*>>) {
        val kotlin = clazz.kotlin
        val type = object : KType {
            override val arguments = emptyList<KTypeProjection>()
            override val classifier = kotlin
            override val isMarkedNullable = false
            override val annotations = emptyList<Annotation>()
        }

        // Do `run` for multi suppression
        @Suppress("UNCHECKED_CAST", "kotlin:S6530")
        run {
            val serializer = json.serializersModule.serializer(type) as KSerializer<Condition<*>>
            conditionSerializer.subclass(kotlin as KClass<Condition<*>>, serializer)
            anySerializer.subclass(kotlin, serializer)
        }
    }

    override fun getItem(stack: ItemStack): RPGItem {
        fun ReadWriteItemNBT.new(id: UUID? = null) = run {
            val realId = id ?: UUID.randomUUID()
            return@run NBTRPGItem(realId, json, stack).also {
                debug("Created new RPGItem with id $id")
                if (id == null)
                    setUUID("id", realId)
                itemsCache[realId] = it
            }
        }

        return stack.editNBT {
            val id = getUUID("id")
            id?.let(itemsCache::get) ?: new(id)
        }
    }



    companion object {
        const val STATS_PACKAGE = "dev.nikdekur.minelib.rpg.stat"
    }
}