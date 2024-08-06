package dev.nikdekur.minelib.nms.entity

import org.bukkit.entity.EntityType

interface MineEntity {

    val type: EntityType

    val objectType: Int
    val defaultRenderDistance: Int
    val updateDelay: Int
    val pushable: Boolean
}