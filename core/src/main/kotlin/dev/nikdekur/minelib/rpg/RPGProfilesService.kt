package dev.nikdekur.minelib.rpg
import dev.nikdekur.minelib.rpg.profile.RPGProfile

interface RPGProfilesService {

    /**
     * Get the profile of the given object if it exists.
     *
     * If the profile does not exist, return null.
     * If the object is not a valid type, return null.
     *
     * @param obj The object to get the profile of.
     * @return The profile of the object, or null if it does not exist.
     * @see getProfile
     */
    fun getExistingProfile(obj: Any): RPGProfile?

    /**
     * Get the profile of the given object.
     *
     * If the profile does not exist, create a new one.
     * If the object is not a valid type or cannot have a profile, return null.
     *
     * @param obj The object to get the profile of.
     * @return The profile of the object, or null if it does not exist.
     * @see getExistingProfile
     */
    fun getProfile(obj: Any): RPGProfile?
}