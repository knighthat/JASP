package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import net.md_5.bungee.api.ChatColor;

public class PStorage {

	private static JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;

	public static String prefix = "&f[&1J&aA&cS&dP&f] ";

	public static String setColor(String input) {
		String finale = ChatColor.translateAlternateColorCodes('&', input);
		return finale;
	}

	public static String removeColor(String input) {
		String finale = ChatColor.stripColor(setColor(input));
		return finale;
	}

	public static String noPerm() {
		String msg = "&cYou don't have permission to execute that command!";
		if (plugin.cfg.getConfig().getString("no_permission") != null
				&& !plugin.cfg.getConfig().getString("no_permission").isEmpty())
			msg = plugin.cfg.getConfig().getString("no_permission");
		return setColor(prefix + msg);
	}

	public static BlockState setSpawn(BlockState from, BlockState to) {

		CreatureSpawner take = (CreatureSpawner) from;
		CreatureSpawner give = (CreatureSpawner) to;

		give.setSpawnedType(take.getSpawnedType());
		give.setMaxSpawnDelay(take.getMaxSpawnDelay());
		give.setMinSpawnDelay(take.getMinSpawnDelay());
		give.setRequiredPlayerRange(take.getRequiredPlayerRange());
		give.setSpawnRange(take.getSpawnRange());
		give.setSpawnCount(take.getSpawnCount());

		return to;
	}

	public static String displaySpawnedType(EntityType entity) {
		return PStorage.setColor(plugin.cfg.getConfig().getString("spawned_type").replace("<mob_name>",
				entity.toString().replace("_", " ")));
	}

	public static boolean isInt(Player player, String string) {
		if (string == null) {
			return false;
		}
		try {
			int convert = Integer.parseInt(string);
			if (convert < 0) {
				String msg = "<input> is not allowed to be below zero!";
				if (!plugin.cfg.getConfig().getString("below_zero").isEmpty())
					msg = plugin.cfg.getConfig().getString("below_zero");
				player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<input>", string)));
				return false;
			}
		} catch (NumberFormatException e) {
			String msg = "<input> is not a number. Try another one!";
			if (!plugin.cfg.getConfig().getString("not_a_number").isEmpty())
				msg = plugin.cfg.getConfig().getString("not_a_number");
			player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<input>", string)));
			return false;
		}
		return true;
	}

	public static List<String> replaceLore(List<String> old_lore, int minDelay, int maxDelay, int playerRange,
			int count, int range, EntityType spawnedType) {
		List<String> new_lore = new ArrayList<>();
		for (String line : old_lore)
			new_lore.add(
					setColor(line).replace("<min_delay>", toString(minDelay)).replace("<max_delay>", toString(maxDelay))
							.replace("<player_range>", toString(playerRange)).replace("<count>", toString(count))
							.replace("<range>", toString(range)).replace("<mob_name>", spawnedType.toString()));
		return new_lore;
	}

	public static List<String> replaceLoreFromItem(List<String> old_lore, ItemStack item) {
		List<String> new_lore = new ArrayList<>();
		BlockStateMeta sMeta = (BlockStateMeta) item.getItemMeta();
		CreatureSpawner info = (CreatureSpawner) sMeta.getBlockState();
		old_lore.forEach(line -> new_lore.add(PStorage.setColor(line
				.replace("<min_delay>", toString(info.getMinSpawnDelay()))
				.replace("<max_delay>", toString(info.getMaxSpawnDelay()))
				.replace("<player_range>", toString(info.getRequiredPlayerRange()))
				.replace("<count>", toString(info.getSpawnCount())).replace("<range>", toString(info.getSpawnRange()))
				.replace("<mob_name>", info.getSpawnedType().toString()))));
		return new_lore;
	}

	private static String toString(int input) {
		return String.valueOf(input);
	}
}
