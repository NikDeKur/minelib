//package dev.nikdekur.minelib.rpg.profile
//
//import dev.nikdekur.minelib.ext.walkingSpeed
//import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
//import dev.nikdekur.minelib.rpg.stat.RPGMaxHealthStat
//import dev.nikdekur.minelib.rpg.stat.RPGSpeedStat
//import org.bukkit.OfflinePlayer
//import org.bukkit.attribute.Attribute
//
//open class RPGSimpleOfflinePlayerProfile(
//    open val player: OfflinePlayer
//) : RPGSimpleProfile() {
//
//    override val id by player::uniqueId
//
//    init { stats.apply {
//        afterStatChange(RPGMaxHealthStat) {
//            updateMaxHealth(maxHealth)
//
//            // If max health increases, we increase the health by the same amount
//            if (isIncrease) {
//                val added = newValue - oldValue
//                heal(added)
//            } else {
//                val taken = oldValue - newValue
//                if (health > maxHealth)
//                    stats.take(RPGHealthStat, taken)
//            }
//        }
//
//        afterStatChange(RPGHealthStat) {
//            updateHealth(health)
//        }
//
//        afterStatChange(RPGSpeedStat) {
//            updateSpeed(speed)
//        }
//
//        // RPGManager.addProfile(id, this@RPGSimpleOfflinePlayerProfile)
//    } }
//
//    private fun updateSpeed(value: Int) {
//        player.player?.walkingSpeed = value / 100.0f
//    }
//
//    private fun updateHealth(value: Double) {
//        player.player?.health = value
//    }
//
//    private fun updateMaxHealth(value: Double) {
//        player.player?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = value
//    }
//
//
//
//    override fun apply() {
//        super.apply()
//        updateMaxHealth(maxHealth)
//        updateHealth(health)
//        updateSpeed(speed)
//    }
//
//    override fun unApply() {
//        super.unApply()
//        updateMaxHealth(20.0)
//        updateHealth(20.0)
//        updateSpeed(20)
//    }
//}