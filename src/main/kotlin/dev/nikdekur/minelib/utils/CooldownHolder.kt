package dev.nikdekur.minelib.utils

import java.time.Duration

interface CooldownHolder {

    /**
     * Whether the cooldowns are currently in the pass cooldown mode
     *
     * If true, cooldowns will not be saved and will be ignored
     */
    val passCooldown: Boolean
        get() = false

    /**
     * Cooldown Map, where key is the cooldown key and value is the time when the cooldown ends (ms)
     * Stored as Double to be compatible with GSON
     */
    val cooldowns: MutableMap<String, Double>

    /**
     * Set cooldown for the key to the specified time
     * @param key the cooldown key
     * @param endsMs the time when the cooldown ends (ms)
     */
    fun setCooldownUpTo(key: String, endsMs: Long) {
        if (passCooldown) return
        cooldowns[key] = endsMs.toDouble()
    }

    /**
     * Set cooldown for the key for the specified time
     * @param key the cooldown key
     * @param durationMs the duration of the cooldown (ms)
     */
    fun setCooldown(key: String, durationMs: Long) {
        setCooldownUpTo(key, System.currentTimeMillis() + durationMs)
    }

    /**
     * Set cooldown for the key for the specified duration
     * @param key the cooldown key
     * @param duration the duration of the cooldown
     */
    fun setCooldown(key: String, duration: Duration) {
        setCooldownUpTo(key, System.currentTimeMillis() + duration.toMillis())
    }

    /**
     * Reset the cooldown for the key
     * @param key the cooldown key
     */
    fun resetCooldown(key: String) {
        cooldowns.remove(key)
    }

    /**
     * Get the time when the cooldown ends for the key
     * @param key the cooldown key
     * @return the time when the cooldown ends (ms) or null if the cooldown is not set or has already ended
     */
    fun getCooldownUpTo(key: String): Long? {
        if (passCooldown) return null
        val v = cooldowns[key]
        if (v != null && v < System.currentTimeMillis()) {
            cooldowns.remove(key)
            return null
        }
        return v?.toLong()
    }

    /**
     * Get the duration of the cooldown for the key
     * @param key the cooldown key
     * @return the left duration of the cooldown or null if the cooldown is not set or has already ended
     */
    fun getCooldown(key: String): Long? {
        return getCooldownUpTo(key)?.let {
            it - System.currentTimeMillis()
        }
    }

    /**
     * Get the duration of the cooldown for the key
     * @param key the cooldown key
     * @return the left duration of the cooldown or null if the cooldown is not set or has already ended
     */
    fun getCooldownDuration(key: String): Duration? {
        return getCooldown(key)?.let { Duration.ofMillis(it) }
    }
}