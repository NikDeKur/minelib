package dev.nikdekur.minelib.rpg
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.service.PluginService

interface RPGProfilesService : PluginService {

    fun getProfile(obj: Any): RPGProfile?
}