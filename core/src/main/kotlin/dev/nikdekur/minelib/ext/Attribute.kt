@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT
import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import java.util.*

@Serializable
data class AttributeModifierData(
    val attribute: Attribute,
    val operation: AttributeModifier.Operation,
    val slot: EquipmentSlot,
    val amount: Double,
    @Serializable(with = StringUUIDSerializer::class)
    val uuid: UUID
)

inline val Attribute.minecraftId: String
    get() {
        val bukkit = name
        val first = bukkit.indexOf('_')
        val second = bukkit.indexOf('_', first + 1)
        val sb = StringBuilder(bukkit.lowercase(Locale.ENGLISH))
        sb.setCharAt(first, '.')
        if (second != -1) {
            sb.deleteCharAt(second)
            sb.setCharAt(second, bukkit[second + 1])
        }
        return sb.toString()
    }


inline fun ReadWriteNBT.addAttributeModifier(
    data: AttributeModifierData
) {
    val (attribute, operation, slot, amount, uuid) = data
    val list = getCompoundList("AttributeModifiers")
    list.addCompound().apply {

        val attributeNMSId = attribute.minecraftId
        setString("AttributeName", attributeNMSId)
        setString("Name", attributeNMSId)

        setDouble("Amount", amount)
        setInteger("Operation", operation.ordinal)
        setLong("UUIDLeast", uuid.leastSignificantBits)
        setLong("UUIDMost", uuid.mostSignificantBits)
        setString("Slot", slot.minecraftId)
    }
}