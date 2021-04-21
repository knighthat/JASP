package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class PStorage
{

	private static FileConfiguration getConfig() {
		return JustAnotherSpawnerPickup.instance.cfg.get();
	}

	private static FileConfiguration getCmds() {
		return JustAnotherSpawnerPickup.instance.cmds.get();
	}

	private static FileConfiguration getMenus() {
		return JustAnotherSpawnerPickup.instance.menus.get();
	}

	public static String prefix = "&f[&1J&aA&cS&dP&f] ";

	public static String setColor( String input ) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static String removeColor( String input ) {
		String finale = ChatColor.stripColor(setColor(input));
		return finale;
	}

	public static BlockState setSpawn( BlockState from, BlockState to ) {
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

	public static boolean isInt( Player player, String string ) {
		if ( string == null )
			return false;
		try {
			int convert = Integer.parseInt(string);
			if ( convert < 0 ) {
				String msg = getStringFromConfig("below_zero", "<input> is not allowed to be below zero!", true);
				player.sendMessage(msg.replace("<input>", string));
				return false;
			}
		} catch ( NumberFormatException e ) {
			String msg = getStringFromConfig("not_a_number", "<input> is not a number. Try another one!", true);
			player.sendMessage(msg.replace("<input>", string));
			return false;
		}
		return true;
	}

	public static void replaceLoreFromItem( ItemStack item ) {
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

	public static String getStringFromConfig( final String path, String defaultString, final boolean prefix ) {
		String def = defaultString;
		if ( getConfig().get(path) != null && !getConfig().getString(path).isEmpty() )
			def = getConfig().getString(path);
		return setColor((prefix ? PStorage.prefix : "") + def);
	}

	public static String getStringFromCommands( final String path, String defaultString ) {
		String def = defaultString;
		if ( getCmds().get(path) != null && !getCmds().getString(path).isEmpty() )
			def = getCmds().getString(path);
		return setColor(def);
	}

	public static String getStringFromMenus( final String path, String defaultString ) {
		String def = defaultString;
		if ( getMenus().get(path) != null && !getMenus().getString(path).isEmpty() )
			def = getMenus().getString(path);
		return setColor(def);
	}

	public static Integer getIntFromMenus( final String path, Integer defaultInt ) {
		int def = defaultInt;
		if ( getMenus().get(path) != null && getMenus().isInt(path) )
			def = getMenus().getInt(path);
		return def;
	}

	public static boolean getBooleanFromMenus( final String path, boolean defaultBoolean ) {
		boolean def = defaultBoolean;
		if ( getMenus().get(path) != null && getMenus().isBoolean(path) )
			def = getMenus().getBoolean(path);
		return def;
	}

	public static ItemStack getMaterialFromMenu( String itemMaterial, final int amount, String name ) {
		itemMaterial = getStringFromMenus(itemMaterial, "DIRT");
		Material material = Material.DIRT;
		switch ( itemMaterial.toLowerCase() ) {
			case "head" :
				itemMaterial = "PLAYER_HEAD";
			break;
			case "mob_spawner" :
				itemMaterial = "SPAWNER";
			break;
			case "none" :
				material = Material.AIR;
			break;
			default :
				itemMaterial = itemMaterial.toUpperCase();
			break;
		}
		List<Material> materials = new ArrayList<>(Arrays.asList(Material.values()));
		if ( materials.stream().map(Material::toString).collect(Collectors.toList()).contains(itemMaterial) )
			material = Material.getMaterial(itemMaterial);
		ItemStack item = new ItemStack(material);
		if ( !material.equals(Material.AIR) ) {
			ItemMeta iMeta = item.getItemMeta();
			if ( name != null )
				iMeta.setDisplayName(setColor(name));
			item.setItemMeta(iMeta);
			item.setAmount(amount);
		}
		return item;
	}

	public static Stream<ArmorStand> getArmorStands( Collection<Entity> entities ) {
		return entities.stream().filter(e -> e.getType().equals(EntityType.ARMOR_STAND))
				.filter(e -> ((ArmorStand) e).getHealth() == Listeners.serialNumber).map(e -> (ArmorStand) e);
	}
}