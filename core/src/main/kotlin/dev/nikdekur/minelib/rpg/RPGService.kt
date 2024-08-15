package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat
import java.util.UUID

interface RPGService {

    fun registerStat(stat: RPGStat<*>)

    fun <T : Comparable<T>> getStat(id: String): RPGStat<T>?

    /**
     * Add profile to registry by specified id.
     *
     * The reason why it doesn't use the profile id is because we can provide alias-ids for the profile.
     * The simple example of it, it's adding player's current RPGProfile in registry by player's UUID,
     * to have a way to get it by player's UUID.
     *
     * @param id The id to store the profile by.
     * @param profile The profile to store.
     */
    fun addProfile(id: UUID, profile: RPGProfile)
    fun removeProfile(profileId: UUID)
    fun getProfile(profileId: UUID): RPGProfile?
}


