package me.TnKnight.JASP;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.Files.GetFiles;
import net.md_5.bungee.api.ChatColor;

public class PStorage
{
	public static String prefix = "&f[&1J&aA&cS&dP&f] ";

	private static final String range = "([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])";

	public static final Pattern HEX_PATTERN = Pattern.compile("#[0-9a-zA-z]{6}");
	public static final Pattern RGB_PATTERN = Pattern
			.compile("#[(][ ]{0,1}" + range + "[,][ ]{0,1}" + range + "[,][ ]{0,1}" + range + "[)]");

	public static String setColor( String input ) {
		if ( hasRGB() )
			for ( Pattern pattern : Arrays.asList(HEX_PATTERN, RGB_PATTERN) ) {
				Matcher match = pattern.matcher(input);
				while ( match.find() ) {
					String color = input.substring(match.start(), match.end());
					if ( pattern.equals(RGB_PATTERN) ) {
						String code = color.replace("#(", "").replace(")", "");
						int R = Integer.parseInt(code.split(",")[0].trim());
						int G = Integer.parseInt(code.split(",")[1].trim());
						int B = Integer.parseInt(code.split(",")[2].trim());
						input = input.replace(color, String.valueOf(ChatColor.of(new Color(R, G, B))));
					} else
						input = input.replace(color, String.valueOf(ChatColor.of(color)));
					match = pattern.matcher(input);
				}
			}
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static String removeColor( String input ) {
		return ChatColor.stripColor(setColor(input));
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
				String msg = GetFiles.getString(GetFiles.FileName.CONFIG, "below_zero",
						"<input> is not allowed to be below zero!", true);
				player.sendMessage(msg.replace("<input>", string));
				return false;
			}
		} catch ( NumberFormatException e ) {
			String msg = GetFiles.getString(GetFiles.FileName.CONFIG, "not_a_number",
					"<input> is not a number. Try another one!", true);
			player.sendMessage(msg.replace("<input>", string));
			return false;
		}
		return true;
	}

	public static void replaceLoreFromItem( ItemStack item ) {
		List<String> new_lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
		BlockStateMeta sMeta = (BlockStateMeta) item.getItemMeta();
		final CreatureSpawner info = (CreatureSpawner) sMeta.getBlockState();
		final String fromConfig = GetFiles
				.getString(GetFiles.FileName.CONFIG, "spawner_description.time_unit", "SECOND", false).toUpperCase();
		final int timeUnit = fromConfig.equals("SECOND") ? 20 : fromConfig.equals("MINUTE") ? 1200 : 1;
		GetFiles.getStringList(GetFiles.FileName.CONFIG, "spawner_description.lore").forEach(line -> {
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

	public static ItemStack getMaterialFromMenu( String itemMaterial, final int amount, String name ) {
		itemMaterial = GetFiles.getString(GetFiles.FileName.MENUS, itemMaterial, "DIRT", false);
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

	public static Integer colorCodeCounter( Player player, String input, final boolean countCodes,
			final boolean countSpaces ) {
		int count = removeColor(input).replaceAll(" ", "").toCharArray().length;
		if ( hasRGB() )
			for ( Pattern pattern : Arrays.asList(RGB_PATTERN, HEX_PATTERN) ) {
				Matcher matcher = pattern.matcher(input);
				while ( matcher.find() ) {
					count++;
					String color = input.substring(matcher.start(), matcher.end());
					input = input.replace(color, "");
					matcher = pattern.matcher(input);
				}
			}
		for ( char c : setColor(input).toCharArray() )
			if ( (countCodes && c == '§') || (countSpaces && c == ' ') )
				count++;
		return count;
	}

	public static final boolean hasRGB() {
		String version = Bukkit.getServer().getVersion();
		version = version.substring(version.lastIndexOf(':') + 2);
		version = version.replace(version.substring(version.lastIndexOf('.')), "");
		return Integer.parseInt(version.replace(")", "").replace("1.", "")) >= 16;
	}
}