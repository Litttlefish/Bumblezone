package com.telepathicgrunt.the_bumblezone.modinit;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.platform.PlatformSoundType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BzSounds {
    public static final ResourcefulRegistry<SoundEvent> SOUND_EVENTS = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, Bumblezone.MODID);

    public static final RegistryEntry<SoundEvent> BEEHIVE_AMBIENCE = registerSoundEvent("biomes.beehive_ambience");
    public static final RegistryEntry<SoundEvent> GENERAL_MUSIC = registerSoundEvent("biomes.general_music");
    public static final RegistryEntry<SoundEvent> ANGERED_BEES = registerSoundEvent("music.angered_bees");
    public static final RegistryEntry<SoundEvent> SEMPITERNAL_SANCTUM = registerSoundEvent("music.sempiternal_sanctum");
    public static final RegistryEntry<SoundEvent> RAGING_EVENT = registerSoundEvent("music.raging_event");
    public static final RegistryEntry<SoundEvent> KNOWING_EVENT = registerSoundEvent("music.knowing_event");
    public static final RegistryEntry<SoundEvent> RADIANCE_EVENT = registerSoundEvent("music.radiance_event");
    public static final RegistryEntry<SoundEvent> LIFE_EVENT = registerSoundEvent("music.life_event");
    public static final RegistryEntry<SoundEvent> CALMING_EVENT = registerSoundEvent("music.calming_event");
    public static final RegistryEntry<SoundEvent> CONTINUITY_EVENT = registerSoundEvent("music.continuity_event");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_FLIGHT_OF_THE_BUMBLEBEE_RIMSKY_KORSAKOV = registerSoundEvent("music_disc.flight_of_the_bumblebee_rimsky_korsakov");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_HONEY_BEE_RAT_FACED_BOY = registerSoundEvent("music_disc.honey_bee_rat_faced_boy");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_LA_BEE_DA_LOCA = registerSoundEvent("music_disc.la_bee_da_loca");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_BEE_LAXING_WITH_THE_HOM_BEES = registerSoundEvent("music_disc.bee_laxing_with_the_hom_bees");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_BEE_WARE_OF_THE_TEMPLE = registerSoundEvent("music_disc.bee_ware_of_the_temple");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_KNOWING_RENREN = registerSoundEvent("music_disc.knowing_renren");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_RADIANCE_RENREN = registerSoundEvent("music_disc.radiance_renren");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_LIFE_RENREN = registerSoundEvent("music_disc.life_renren");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_BEENNA_BOX_MONO = registerSoundEvent("music_disc.beenna_box_mono");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_A_LAST_FIRST_LAST_MONO = registerSoundEvent("music_disc.a_last_first_last_mono");
    public static final RegistryEntry<SoundEvent> MUSIC_DISC_DROWNING_IN_DESPAIR_MONO = registerSoundEvent("music_disc.drowning_in_despair_mono");
    public static final RegistryEntry<SoundEvent> BEEHEMOTH_HURT = registerSoundEvent("entity.the_bumblezone.beehemoth.hurt");
    public static final RegistryEntry<SoundEvent> BEEHEMOTH_DEATH = registerSoundEvent("entity.the_bumblezone.beehemoth.death");
    public static final RegistryEntry<SoundEvent> BEEHEMOTH_LOOP = registerSoundEvent("entity.the_bumblezone.beehemoth.loop");
    public static final RegistryEntry<SoundEvent> SENTRY_WATCHER_CRASH = registerSoundEvent("entity.the_bumblezone.sentry_watcher.crash");
    public static final RegistryEntry<SoundEvent> SENTRY_WATCHER_ACTIVATING = registerSoundEvent("entity.the_bumblezone.sentry_watcher.activating");
    public static final RegistryEntry<SoundEvent> SENTRY_WATCHER_MOVING = registerSoundEvent("entity.the_bumblezone.sentry_watcher.moving");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_LASER = registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.laser");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_LASER_CHARGE = registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.laser_charge");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_CRASH_CHARGE = registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.crash_charge");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_CRASHES= registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.crashes");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_SPIN = registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.spin");
    public static final RegistryEntry<SoundEvent> COSMIC_CRYSTAL_ENTITY_SPAWN_EXPLOSION = registerSoundEvent("entity.the_bumblezone.cosmic_crystal_entity.spawn_explosion");
    public static final RegistryEntry<SoundEvent> ELECTRIC_RING_PASSED = registerSoundEvent("entity.the_bumblezone.electric_ring.passed");
    public static final RegistryEntry<SoundEvent> BEE_ESSENCE_CONSUMED = registerSoundEvent("item.the_bumblezone.essence_of_the_bees.consumed");
    public static final RegistryEntry<SoundEvent> BEE_ESSENCE_CONSUMING = registerSoundEvent("item.the_bumblezone.essence_of_the_bees.consuming");
    public static final RegistryEntry<SoundEvent> STINGER_SPEAR_HIT = registerSoundEvent("entity.the_bumblezone.stinger_spear.hit");
    public static final RegistryEntry<SoundEvent> STINGER_SPEAR_HIT_GROUND = registerSoundEvent("entity.the_bumblezone.stinger_spear.hit_ground");
    public static final RegistryEntry<SoundEvent> STINGER_SPEAR_RETURN = registerSoundEvent("entity.the_bumblezone.stinger_spear.return");
    public static final RegistryEntry<SoundEvent> STINGER_SPEAR_THROW = registerSoundEvent("entity.the_bumblezone.stinger_spear.throw");
    public static final RegistryEntry<SoundEvent> BEE_STINGER_HIT = registerSoundEvent("entity.the_bumblezone.bee_stinger.hit");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_SHARD_HIT = registerSoundEvent("entity.the_bumblezone.honey_crystal_shard.hit");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_SHARD_SHATTER = registerSoundEvent("entity.the_bumblezone.honey_crystal_shard.shatter");
    public static final RegistryEntry<SoundEvent> BUMBLE_BEE_CHESTPLATE_FLYING = registerSoundEvent("item.the_bumblezone.bumble_bee_chestplate.fly");
    public static final RegistryEntry<SoundEvent> HONEY_COMPASS_BLOCK_LOCK = registerSoundEvent("item.the_bumblezone.honey_compass.block_lock");
    public static final RegistryEntry<SoundEvent> HONEY_COMPASS_STRUCTURE_LOCK = registerSoundEvent("item.the_bumblezone.honey_compass.structure_lock");
    public static final RegistryEntry<SoundEvent> BEE_CANNON_FIRES = registerSoundEvent("item.the_bumblezone.bee_cannon.fire");
    public static final RegistryEntry<SoundEvent> BUZZING_BRIEFCASE_RELEASES = registerSoundEvent("item.the_bumblezone.buzzing_briefcase.releases");
    public static final RegistryEntry<SoundEvent> CRYSTAL_CANNON_FIRES = registerSoundEvent("item.the_bumblezone.crystal_cannon.fire");
    public static final RegistryEntry<SoundEvent> SUGAR_WATER_DRINK = registerSoundEvent("item.the_bumblezone.sugar_water_bottle.drink");
    public static final RegistryEntry<SoundEvent> ROYAL_JELLY_DRINK = registerSoundEvent("item.the_bumblezone.royal_jelly_bottle.drink");
    public static final RegistryEntry<SoundEvent> ROYAL_JELLY_BLOCK_SLIDE = registerSoundEvent("block.the_bumblezone.royal_jelly_block.slide");
    public static final RegistryEntry<SoundEvent> WASHING_RESIDUES = registerSoundEvent("block.the_bumblezone.washing_honey_residues");
    public static final RegistryEntry<SoundEvent> CRYSTALLINE_FLOWER_USE = registerSoundEvent("block.the_bumblezone.crystalline_flower.use");
    public static final RegistryEntry<SoundEvent> SUPER_CANDLE_WICK_LIT = registerSoundEvent("block.the_bumblezone.super_candle_wick.lit");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_ATTACK = registerSoundEvent("entity.the_bumblezone.honey_slime.attack");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_HURT = registerSoundEvent("entity.the_bumblezone.honey_slime.hurt");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_DEATH = registerSoundEvent("entity.the_bumblezone.honey_slime.death");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_SQUISH = registerSoundEvent("entity.the_bumblezone.honey_slime.squish");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_JUMP = registerSoundEvent("entity.the_bumblezone.honey_slime.jump");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_HURT_SMALL = registerSoundEvent("entity.the_bumblezone.honey_slime.hurt_small");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_DEATH_SMALL = registerSoundEvent("entity.the_bumblezone.honey_slime.death_small");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_SQUISH_SMALL = registerSoundEvent("entity.the_bumblezone.honey_slime.squish_small");
    public static final RegistryEntry<SoundEvent> HONEY_SLIME_JUMP_SMALL = registerSoundEvent("entity.the_bumblezone.honey_slime.jump_small");
    public static final RegistryEntry<SoundEvent> POLLEN_PUFF_THROW = registerSoundEvent("entity.the_bumblezone.pollen_puff.throw");
    public static final RegistryEntry<SoundEvent> DIRT_PELLET_THROW = registerSoundEvent("entity.the_bumblezone.dirt_pellet.throw");
    public static final RegistryEntry<SoundEvent> DIRT_PELLET_HIT = registerSoundEvent("entity.the_bumblezone.dirt_pellet.hit");
    public static final RegistryEntry<SoundEvent> ROOTMIN_ANGRY = registerSoundEvent("entity.the_bumblezone.rootmin.angry");
    public static final RegistryEntry<SoundEvent> ROOTMIN_CURIOUS = registerSoundEvent("entity.the_bumblezone.rootmin.curious");
    public static final RegistryEntry<SoundEvent> ROOTMIN_CURSING = registerSoundEvent("entity.the_bumblezone.rootmin.cursing");
    public static final RegistryEntry<SoundEvent> ROOTMIN_EMBARRASSED = registerSoundEvent("entity.the_bumblezone.rootmin.embarrassed");
    public static final RegistryEntry<SoundEvent> ROOTMIN_SHOCK = registerSoundEvent("entity.the_bumblezone.rootmin.shock");
    public static final RegistryEntry<SoundEvent> ROOTMIN_SHOOT = registerSoundEvent("entity.the_bumblezone.rootmin.shoot");
    public static final RegistryEntry<SoundEvent> BEE_QUEEN_HURT = registerSoundEvent("entity.the_bumblezone.bee_queen.hurt");
    public static final RegistryEntry<SoundEvent> BEE_QUEEN_DEATH = registerSoundEvent("entity.the_bumblezone.bee_queen.death");
    public static final RegistryEntry<SoundEvent> BEE_QUEEN_LOOP = registerSoundEvent("entity.the_bumblezone.bee_queen.loop");
    public static final RegistryEntry<SoundEvent> BEE_QUEEN_HAPPY = registerSoundEvent("entity.the_bumblezone.bee_queen.happy");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_STEP = registerSoundEvent("block.the_bumblezone.honey_crystal_block.step");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_PLACE = registerSoundEvent("block.the_bumblezone.honey_crystal_block.place");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_HIT = registerSoundEvent("block.the_bumblezone.honey_crystal_block.hit");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_FALL = registerSoundEvent("block.the_bumblezone.honey_crystal_block.fall");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_CHIME = registerSoundEvent("block.the_bumblezone.honey_crystal_block.chime");
    public static final RegistryEntry<SoundEvent> HONEY_CRYSTAL_BLOCK_BREAK = registerSoundEvent("block.the_bumblezone.honey_crystal_block.break");
    public static final RegistryEntry<SoundEvent> WINDY_AIR_BLOWS = registerSoundEvent("block.the_bumblezone.windy_air.blows");
    public static final RegistryEntry<SoundEvent> HONEY_COCOON_OPEN = registerSoundEvent("block.the_bumblezone.honey_cocoon.open");

    public static final PlatformSoundType HONEY_CRYSTALS_TYPE = new PlatformSoundType(
            1.0F,
            1.0F,
            BzSounds.HONEY_CRYSTAL_BLOCK_BREAK,
            BzSounds.HONEY_CRYSTAL_BLOCK_STEP,
            BzSounds.HONEY_CRYSTAL_BLOCK_PLACE,
            BzSounds.HONEY_CRYSTAL_BLOCK_HIT,
            BzSounds.HONEY_CRYSTAL_BLOCK_FALL
    );

    private static RegistryEntry<SoundEvent> registerSoundEvent(String path) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, path)));
    }
}
