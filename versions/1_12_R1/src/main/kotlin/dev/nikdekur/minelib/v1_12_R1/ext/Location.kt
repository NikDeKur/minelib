package dev.nikdekur.minelib.v1_12_R1.ext

import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location

inline val Location.nmsWorld: WorldServer
    get() = world.nms