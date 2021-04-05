package me.TnKnight.JASP;

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
		if (!plugin.cfg.getConfig().getString("no_permission").isEmpty())
			msg = plugin.cfg.getConfig().getString("no_permission");
		return setColor(prefix + msg);
	}

	public static BlockState setSpawn(BlockState from, BlockState to) {

		CreatureSpawner take = (CreatureSpawner) from;
		CreatureSpawner give = (CreatureSpawner) to;

		give.setSpawnedType(take.getSpawnedType());
		give.setMinSpawnDelay(take.getMinSpawnDelay());
		give.setMaxSpawnDelay(take.getMaxSpawnDelay());
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

	public static void replaceLoreFromItem(ItemStack item) {
		List<String> new_lore = item.getItemMeta().getLore();
		for (String line : plugin.cfg.getConfig().getStringList("spawner_description.lore"))
			new_lore.add(line);
		BlockStateMeta sMeta = (BlockStateMeta) item.getItemMeta();
		CreatureSpawner info = (CreatureSpawner) sMeta.getBlockState();
		for (int i = 0; i < new_lore.size(); i++)
			new_lore.set(i,
					PStorage.setColor(
							new_lore.get(i).replace("<min_delay>", toString(info.getMinSpawnDelay() / getTimeUnit()))
									.replace("<max_delay>", toString(info.getMaxSpawnDelay() / getTimeUnit()))
									.replace("<player_range>", toString(info.getRequiredPlayerRange() / getTimeUnit()))
									.replace("<count>", toString(info.getSpawnCount() / getTimeUnit()))
									.replace("<range>", toString(info.getSpawnRange() / getTimeUnit()))
									.replace("<mob_name>", info.getSpawnedType().toString())));
		sMeta.setLore(new_lore);
		item.setItemMeta(sMeta);
	}

	private static String toString(int input) {
		return String.valueOf(input);
	}

	public static int getTimeUnit() {
		int time_unit = 1;
		switch (plugin.cfg.getConfig().getString("spawner_description.time_unit").toLowerCase()) {
		case "second":
			time_unit = 20;
			break;
		case "minute":
			time_unit = 1200;
			break;
		default:
			time_unit = 1;
			break;
		}
		return time_unit;
	}
}
