package dev.nikdekur.minelib.item

import org.bukkit.DyeColor
import org.bukkit.Material
import dev.nikdekur.minelib.i18n.msg.DefaultMSG

object Patterns {
    val EMPTY_SLOT_PATTERN = ItemPattern.from(Material.STAINED_GLASS_PANE)
        .setColor(DyeColor.GRAY)
        .setDisplayName("&8")
        .setTouchable(false)
        .setTag("ITEM", "EMPTY_SLOT")

    val EMPTY_SLOT = EMPTY_SLOT_PATTERN.build(null)

    val INVISIBLE_SLOT = ItemPattern.from(Material.BARRIER)
        .setDisplayName("&8")
        .setTouchable(false)
        .setTag("ITEM", "INVISIBLE_SLOT")

    val ARROW_NEXT = ItemPattern.fromSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
        .setDisplayName(DefaultMSG.ARROW_NEXT)
        .setTouchable(false)
        .setTag("ITEM", "ARROW_NEXT")

    val ARROW_PREVIOUS = ItemPattern.fromSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
        .setDisplayName(DefaultMSG.ARROW_PREV)
        .setTouchable(false)
        .setTag("ITEM", "ARROW_PREVIOUS")

    val BARRIER_ARROW_NEXT = ItemPattern.from(Material.BARRIER)
        .setDisplayName(DefaultMSG.ARROW_NEXT)
        .setTouchable(false)
        .setTag("ITEM", "BARRIER_ARROW_NEXT")

    val BARRIER_ARROW_PREVIOUS = ItemPattern.from(Material.BARRIER)
        .setDisplayName(DefaultMSG.ARROW_PREV)
        .setTouchable(false)
        .setTag("ITEM", "BARRIER_ARROW_PREVIOUS")

    val BARRIER_SHADOW_ARROW_NEXT = ItemPattern.from(Material.BARRIER)
        .setDisplayName(DefaultMSG.ARROW_NEXT)
        .setTouchable(false)
        .setTag("ITEM", "BARRIER_SHADOW_ARROW_NEXT")

    val BARRIER_SHADOW_ARROW_PREVIOUS = ItemPattern.from(Material.BARRIER)
        .setDisplayName(DefaultMSG.ARROW_PREV)
        .setTouchable(false)
        .setTag("ITEM", "BARRIER_SHADOW_ARROW_PREVIOUS")
}