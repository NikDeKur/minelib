package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld

inline val Location.nmsWorld: WorldServer
    get() = world.nms