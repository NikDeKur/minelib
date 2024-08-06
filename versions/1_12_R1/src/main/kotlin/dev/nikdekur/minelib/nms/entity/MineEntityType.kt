package dev.nikdekur.minelib.nms.entity

import dev.nikdekur.ndkore.ext.constructTyped
import net.minecraft.server.v1_12_R1.*
import org.bukkit.entity.EntityType

/**
 * An extended version of [EntityType] that contains more useful information about the entity.
 *
 * @property type The [EntityType] of the entity.
 * @property nmsClass The NMS class of the entity.
 * @property objectTypeOrNull The object type of the entity (used in [PacketBuilder.Entity]). Null if not used.
 * @property defaultRenderDistanceOrNull The default render distance of the entity. Null if not used.
 * @property updateDelayOrNull The update delay of the entity. Null if not used.
 * @property pushableOrNull If the entity is pushable. Null if not used.
 */
enum class MineEntityType(
    override val type: EntityType,
    val nmsClass: Class<out Entity>? = null,
    val objectTypeOrNull: Int?,
    val defaultRenderDistanceOrNull: Int?,
    val updateDelayOrNull: Int?,
    val pushableOrNull: Boolean?
) : MineEntity {

    DROPPED_ITEM(EntityType.DROPPED_ITEM, EntityItem::class.java, 2, 64, 20, true),
    EXPERIENCE_ORB(EntityType.EXPERIENCE_ORB, EntityExperienceOrb::class.java, null, 160, 20, true),
    AREA_EFFECT_CLOUD(EntityType.AREA_EFFECT_CLOUD, EntityAreaEffectCloud::class.java, 3, 256, 20, true),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, EntityGuardianElder::class.java, null, 80, 3, true),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, EntitySkeletonWither::class.java, null, 80, 3, true),
    STRAY(EntityType.STRAY, EntitySkeletonStray::class.java, null, 80, 3, true),
    EGG(EntityType.EGG, EntityEgg::class.java, 62, 64, 10, true),
    LEASH_HITCH(EntityType.LEASH_HITCH, EntityLeash::class.java, null, 160, Integer.MAX_VALUE, false),
    PAINTING(EntityType.PAINTING, EntityPainting::class.java, null, 160, Integer.MAX_VALUE, false),
    ARROW(EntityType.ARROW, EntityArrow::class.java, null, 64, 20, true),
    SNOWBALL(EntityType.SNOWBALL, EntitySnowball::class.java, null, 64, 10, true),
    FIREBALL(EntityType.FIREBALL, EntityLargeFireball::class.java, null, 64, 10, true),
    SMALL_FIREBALL(EntityType.SMALL_FIREBALL, EntitySmallFireball::class.java, null, 64, 10, true),
    ENDER_PEARL(EntityType.ENDER_PEARL, EntityEnderPearl::class.java, null, 64, 10, true),
    ENDER_SIGNAL(EntityType.ENDER_SIGNAL, EntityEnderSignal::class.java, null, 64, 4, true),
    SPLASH_POTION(EntityType.SPLASH_POTION, EntityPotion::class.java, null, 64, 10, true),
    THROWN_EXP_BOTTLE(EntityType.THROWN_EXP_BOTTLE, EntityThrownExpBottle::class.java, 75, 64, 10, true),
    ITEM_FRAME(EntityType.ITEM_FRAME, EntityItemFrame::class.java, 71, 64, 20, true),
    WITHER_SKULL(EntityType.WITHER_SKULL, EntityWitherSkull::class.java, null, 64, 10, true),
    PRIMED_TNT(EntityType.PRIMED_TNT, EntityTNTPrimed::class.java, 50, 160, 10, true),
    FALLING_BLOCK(EntityType.FALLING_BLOCK, EntityFallingBlock::class.java, 70, 160, 20, true),
    FIREWORK(EntityType.FIREWORK, EntityFireworks::class.java, null, 64, 10, true),
    HUSK(EntityType.HUSK, EntityZombieHusk::class.java, null, 80, 3, true),
    SPECTRAL_ARROW(EntityType.SPECTRAL_ARROW, EntitySpectralArrow::class.java, 91, 64, 10, true),
    SHULKER_BULLET(EntityType.SHULKER_BULLET, EntityShulkerBullet::class.java, 67, 80, 3, true),
    DRAGON_FIREBALL(EntityType.DRAGON_FIREBALL, EntityDragonFireball::class.java, 93, 80, 3, true),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager::class.java, null, 80, 3, true),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, EntityHorseSkeleton::class.java, null, 80, 3, true),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, EntityHorseZombie::class.java, null, 80, 3, true),
    ARMOR_STAND(EntityType.ARMOR_STAND, EntityArmorStand::class.java, 78, 160, 3, true),
    DONKEY(EntityType.DONKEY, EntityHorseDonkey::class.java, null, 80, 3, true),
    MULE(EntityType.MULE, EntityHorseMule::class.java, null, 80, 3, true),
    EVOKER_FANGS(EntityType.EVOKER_FANGS, EntityEvokerFangs::class.java, null, 160, 2, false),
    EVOKER(EntityType.EVOKER, EntityEvoker::class.java, null, 80, 3, true),
    VEX(EntityType.VEX, EntityVex::class.java, null, 80, 3, true),
    VINDICATOR(EntityType.VINDICATOR, EntityVindicator::class.java, null, 80, 3, true),
    ILLUSIONER(EntityType.ILLUSIONER, EntityIllagerIllusioner::class.java, null, 80, 3, true),
    MINECART_COMMAND(EntityType.MINECART_COMMAND, EntityMinecartCommandBlock::class.java, null, 80, 3, true),
    BOAT(EntityType.BOAT, EntityBoat::class.java, 1, 80, 3, true),
    MINECART(EntityType.MINECART, EntityMinecartRideable::class.java, 10, 80, 3, true),
    MINECART_CHEST(EntityType.MINECART_CHEST, EntityMinecartChest::class.java, null, 80, 3, true),
    MINECART_FURNACE(EntityType.MINECART_FURNACE, EntityMinecartFurnace::class.java, null, 80, 3, true),
    MINECART_TNT(EntityType.MINECART_TNT, EntityMinecartTNT::class.java, null, 80, 3, true),
    MINECART_HOPPER(EntityType.MINECART_HOPPER, EntityMinecartHopper::class.java, null, 80, 3, true),
    MINECART_MOB_SPAWNER(EntityType.MINECART_MOB_SPAWNER, EntityMinecartMobSpawner::class.java, null, 80, 3, true),
    CREEPER(EntityType.CREEPER, EntityCreeper::class.java, null, 80, 3, true),
    SKELETON(EntityType.SKELETON, EntitySkeleton::class.java, null, 80, 3, true),
    SPIDER(EntityType.SPIDER, EntitySpider::class.java, null, 80, 3, true),
    GIANT(EntityType.GIANT, EntityGiantZombie::class.java, null, 80, 3, true),
    ZOMBIE(EntityType.ZOMBIE, EntityZombie::class.java, null, 80, 3, true),
    SLIME(EntityType.SLIME, EntitySlime::class.java, null, 80, 3, true),
    GHAST(EntityType.GHAST, EntityGhast::class.java, null, 80, 3, true),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, EntityPigZombie::class.java, null, 80, 3, true),
    ENDERMAN(EntityType.ENDERMAN, EntityEnderman::class.java, null, 80, 3, true),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, EntityCaveSpider::class.java, null, 80, 3, true),
    SILVERFISH(EntityType.SILVERFISH, EntitySilverfish::class.java, null, 80, 3, true),
    BLAZE(EntityType.BLAZE, EntityBlaze::class.java, null, 80, 3, true),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, EntityMagmaCube::class.java, null, 80, 3, true),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, EntityEnderDragon::class.java, null, 160, 3, true),
    WITHER(EntityType.WITHER, EntityWither::class.java, null, 80, 3, true),
    BAT(EntityType.BAT, EntityBat::class.java, null, 80, 3, true),
    WITCH(EntityType.WITCH, EntityWitch::class.java, null, 80, 3, true),
    ENDERMITE(EntityType.ENDERMITE, EntityEndermite::class.java, null, 80, 3, true),
    GUARDIAN(EntityType.GUARDIAN, EntityGuardian::class.java, null, 80, 3, true),
    SHULKER(EntityType.SHULKER, EntityShulker::class.java, null, 80, 3, true),
    PIG(EntityType.PIG, EntityPig::class.java, null, 80, 3, true),
    SHEEP(EntityType.SHEEP, EntitySheep::class.java, null, 80, 3, true),
    COW(EntityType.COW, EntityCow::class.java, null, 80, 3, true),
    CHICKEN(EntityType.CHICKEN, EntityChicken::class.java, null, 80, 3, true),
    SQUID(EntityType.SQUID, EntitySquid::class.java, null, 64, 3, true),
    WOLF(EntityType.WOLF, EntityWolf::class.java, null, 80, 3, true),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, EntityMushroomCow::class.java, null, 80, 3, true),
    SNOWMAN(EntityType.SNOWMAN, EntitySnowman::class.java, null, 80, 3, true),
    OCELOT(EntityType.OCELOT, EntityOcelot::class.java, null, 80, 3, true),
    IRON_GOLEM(EntityType.IRON_GOLEM, EntityIronGolem::class.java, null, 80, 3, true),
    HORSE(EntityType.HORSE, EntityHorse::class.java, null, 80, 3, true),
    RABBIT(EntityType.RABBIT, EntityRabbit::class.java, null, 80, 3, true),
    POLAR_BEAR(EntityType.POLAR_BEAR, EntityPolarBear::class.java, null, 80, 3, true),
    LLAMA(EntityType.LLAMA, EntityLlama::class.java, null, 80, 3, true),
    LLAMA_SPIT(EntityType.LLAMA_SPIT, EntityLlamaSpit::class.java, null, 64, 10, true),
    PARROT(EntityType.PARROT, EntityParrot::class.java, null, 80, 3, true),
    VILLAGER(EntityType.VILLAGER, EntityVillager::class.java, null, 80, 3, true),
    ENDER_CRYSTAL(EntityType.ENDER_CRYSTAL, EntityEnderCrystal::class.java, 51, 256, Integer.MAX_VALUE, false),
    LINGERING_POTION(EntityType.LINGERING_POTION, EntityPotion::class.java, null, 160, 20, true),
    FISHING_HOOK(EntityType.FISHING_HOOK, EntityFishingHook::class.java, null, 64, 5, true),
    LIGHTNING(EntityType.LIGHTNING, EntityLightning::class.java, null, 160, Integer.MAX_VALUE, false),
    WEATHER(EntityType.WEATHER, EntityWeather::class.java, null, 160, Integer.MAX_VALUE, false),
    PLAYER(EntityType.PLAYER, EntityPlayer::class.java, null, 512, 2, true),
    COMPLEX_PART(EntityType.COMPLEX_PART, EntityComplexPart::class.java, null, null, null, null),
    TIPPED_ARROW(EntityType.TIPPED_ARROW, EntityTippedArrow::class.java, 60, 64, 20, true),
    UNKNOWN(EntityType.UNKNOWN, null, null, null, null, null)


    ;

    override val objectType: Int
        get() = this.objectTypeOrNull ?: throw IllegalArgumentException("Unknown object type for entity: ${this.name}")

    override val defaultRenderDistance: Int
        get() = this.defaultRenderDistanceOrNull ?: throw IllegalArgumentException("Unknown default render distance for entity: ${this.name}")

    override val updateDelay: Int
        get() = this.updateDelayOrNull ?: throw IllegalArgumentException("Unknown update delay for entity: ${this.name}")

    override val pushable: Boolean
        get() = this.pushableOrNull ?: throw IllegalArgumentException("Unknown pushable for entity: ${this.name}")

    fun newNMSInstance(world: World): Entity {
        val nms = this.nmsClass
        check(nms != null) { "Unknown entity type: ${this.name}" }
        return nms.constructTyped(ENTITY_DEFAULT_CONSTRUCTOR, world)
    }

    companion object {
        val ENTITY_DEFAULT_CONSTRUCTOR = arrayOf(World::class.java)
        val BY_NMS_CLASS = HashMap<String, MineEntityType>().apply {
            MineEntityType.entries.forEach {
                val clazz = it.nmsClass ?: return@forEach
                this[clazz.name] = it
            }
        }

        fun from(type: EntityType): MineEntityType {
            return valueOf(type.name)
        }

        fun fromNMS(entity: Entity): MineEntityType? {
            return BY_NMS_CLASS[entity.javaClass.name]
        }
    }
}
