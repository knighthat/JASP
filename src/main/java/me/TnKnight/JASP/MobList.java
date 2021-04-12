package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;

public enum MobList {

	BAT, BLAZE, CAVE_SPIDER, CHICKEN, COD, COW, CREEPER, DOLPHIN, DONKEY, DROWNED, ELDER_GUARDIAN, ENDERMAN, ENDERMITE,
	EVOKER, GHAST, GUARDIAN, HORSE, HUSK, LLAMA, MAGMA_CUBE, MOOSHROOM, MULE, OCELOT, PARROT, PHANTOM, PIG, POLAR_BEAR,
	PUFFERFISH, RABBIT, SALMON, SHEEP, SHULKER, SILVERFISH, SKELETON, SLIME, SPIDER, SQUID, STRAY, TROPICAL_FISH,
	TURTLE, VEX, VILLAGER, VINDICATOR, WITCH, WITHER_SKELETON, WOLF, ZOMBIE, ZOMBIE_HORSE, ZOMBIE_PIGMAN,
	ZOMBIE_VILLAGER, CAT, FOX, PANDA, PILLAGER, RAVAGER, TRADER_LLAMA, WANDERING_TRADER, BEE, HOGLIN, PIGLIN, STRIDER,
	ZOGLIN, ZOMBIFIED_PIGLIN;

	public static List<MobList> getValues() {
		List<MobList> list = new ArrayList<>();
		List<String> mobs = Arrays.asList(EntityType.values()).stream().map(EntityType::toString)
				.collect(Collectors.toList());
		for (MobList mob : values())
			if (mobs.contains(mob.toString()))
				list.add(mob);
		return list.stream().sorted().collect(Collectors.toList());
	}

	public static List<String> getValuesToString() {
		return getValues().stream().map(MobList::toString).collect(Collectors.toList());
	}
}
