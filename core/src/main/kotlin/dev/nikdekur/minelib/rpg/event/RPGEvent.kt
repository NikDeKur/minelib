package dev.nikdekur.minelib.rpg.event

import org.bukkit.event.Event
import dev.nikdekur.minelib.rpg.profile.RPGProfile

abstract class RPGEvent(val profile: RPGProfile) : Event()