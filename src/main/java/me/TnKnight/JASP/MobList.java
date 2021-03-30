package me.TnKnight.JASP;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;

public enum MobList {

	BAT, BLAZE, CAVE_SPIDER, CHICKEN, COD, COW, CREEPER, DOLPHIN, DONKEY, DROWNED, ELDER_GUARDIAN, ENDERMAN, ENDERMITE,
	EVOKER, GHAST, GUARDIAN, HORSE, HUSK, LLAMA, MAGMA_CUBE, MOOSHROOM, MULE, OCELOT, PARROT, PHANTOM, PIG, POLAR_BEAR,
	PUFFERFISH, RABBIT, SALMON, SHEEP, SHULKER, SILVERFISH, SKELETON, SLIME, SPIDER, SQUID, STRAY, TROPICAL_FISH,
	TURTLE, VEX, VILLAGER, VINDICATOR, WITCH, WITHER_SKELETON, WOLF, ZOMBIE, ZOMBIE_HORSE, ZOMBIE_PIGMAN,
	ZOMBIE_VILLAGER, CAT, FOX, PANDA, PILLAGER, RAVAGER, TRADER_LLAMA, WANDERING_TRADER, BEE, HOGLIN, PIGLIN, STRIDER,
	ZOGLIN, ZOMBIFIED_PIGLIN;

	public static ArrayList<MobList> getAvailables() {
		ArrayList<MobList> list = new ArrayList<>();
		ArrayList<String> eType = new ArrayList<>();
		for (EntityType e : EntityType.values())
			eType.add(e.toString());
		for (MobList mob : MobList.values())
			if (eType.contains(mob.toString()))
				list.add(mob);
		return list;
	}

}
