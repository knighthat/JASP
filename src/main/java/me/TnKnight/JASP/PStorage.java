package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import net.md_5.bungee.api.ChatColor;

public class PStorage {

	private static FileConfiguration getConfig() {
		return JustAnotherSpawnerPickup.instance.cfg.getConfig();
	}

	private static FileConfiguration getCmds() {
		return JustAnotherSpawnerPickup.instance.cmds.getConfig();
	}

	/**
	 * 
	 * @return Prefix of Plugin
	 */
	public static String prefix = "&f[&1J&aA&cS&dP&f] ";

	/**
	 * Adds color(s) to given String by replacing from "&" to "§"
	 *
	 * @param input String to add color
	 * @return A copy of the input string, with coloring
	 */
	public static String setColor(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	/**
	 * Strips the given message of all color codes
	 *
	 * @param input String to strip of color
	 * @return A copy of the input string, without any coloring
	 */
	public static String removeColor(String input) {
		String finale = ChatColor.stripColor(setColor(input));
		return finale;
	}

	/**
	 * 
	 * @param from Block to copy
	 * @param to   Block to paste
	 * @return
	 */
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

	/**
	 * Checks if string is an Integer and not below zero
	 * 
	 * @param player to send message if false
	 * @param string needs to be checked
	 * @return false if not a number or below zero, otherwise true
	 */
	public static boolean isInt(Player player, String string) {
		if (string == null)
			return false;
		try {
			int convert = Integer.parseInt(string);
			if (convert < 0) {
				String msg = "<input> is not allowed to be below zero!";
				if (!getConfig().getString("below_zero").isEmpty())
					msg = getConfig().getString("below_zero");
				player.sendMessage(setColor(prefix + msg.replace("<input>", string)));
				return false;
			}
		} catch (NumberFormatException e) {
			String msg = "<input> is not a number. Try another one!";
			if (!getConfig().getString("not_a_number").isEmpty())
				msg = getConfig().getString("not_a_number");
			player.sendMessage(setColor(prefix + msg.replace("<input>", string)));
			return false;
		}
		return true;
	}

	/**
	 * Loops through the lore of given ItemStack, then replace all variables by its
	 * statistics.
	 * 
	 * @param item has lore needs to be replaced
	 */
	public void replaceLoreFromItem(ItemStack item) {
		List<String> new_lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
		BlockStateMeta sMeta = (BlockStateMeta) item.getItemMeta();
		final CreatureSpawner info = (CreatureSpawner) sMeta.getBlockState();
		final String fromConfig = getConfig().getString("spawner_description.time_unit").toUpperCase();
		final int timeUnit = fromConfig.equals("SECOND") ? 20 : fromConfig.equals("MINUTE") ? 1200 : 1;
		getConfig().getStringList("spawner_description.lore").forEach(line -> {
			final String minDelay = String.valueOf(info.getMinSpawnDelay() / timeUnit);
			final String maxDelay = String.valueOf(info.getMaxSpawnDelay() / timeUnit);
			final String playerRange = String.valueOf(info.getRequiredPlayerRange() / timeUnit);
			final String count = String.valueOf(info.getSpawnCount() / timeUnit);
			final String range = String.valueOf(info.getSpawnRange() / timeUnit);
			final String mobName = info.getSpawnedType().toString();
			new_lore.add(line.replace("<min_delay>", minDelay).replace("<max_delay>", maxDelay)
					.replace("<player_range>", playerRange).replace("<count>", count).replace("<range>", range)
					.replace("<mob_name>", mobName));
		});
		sMeta.setLore(new_lore.stream().map(line -> setColor(line)).collect(Collectors.toList()));
		item.setItemMeta(sMeta);
	}

	public String getStringFromConfig(final String path, String defaultString, final boolean prefix) {
		String def = defaultString;
		if (getConfig().getString(path) != null && !getConfig().getString(path).isEmpty())
			def = getConfig().getString(path);
		return setColor((prefix ? PStorage.prefix : "") + def);
	}

	public String getStringFromCommands(final String path, String defaultString) {
		String def = defaultString;
		if (getCmds().getString(path) != null && !getCmds().getString(path).isEmpty())
			def = getCmds().getString(path);
		return setColor(def);
	}
}