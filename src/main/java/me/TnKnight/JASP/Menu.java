package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.Files.GetFiles;

public class Menu extends PStorage
{
	static Inventory inv;
	static int slots;
	static String title;
	static ItemStack fill;

	public static void updateMenu() {
		final String path = "statistic_menu.information.";
		title = GetFiles.getString(GetFiles.FileName.MENUS, path + "title", "", false);
		slots = GetFiles.getInt(GetFiles.FileName.MENUS, path + "size", 1) * 9;
		fill = getMaterialFromMenu(path + "fill", GetFiles.getInt(GetFiles.FileName.MENUS, path + "fill_amount", 1),
				" ");
	}

	public static void getInventory( Player player, CreatureSpawner information ) {
		FileConfiguration menu = JustAnotherSpawnerPickup.instance.menus.get();
		inv = Bukkit.createInventory(player, slots, setColor(title));
		for ( int i = 0 ; i < slots ; i++ )
			inv.setItem(i, fill);
		menu.getConfigurationSection("statistic_menu").getKeys(false).forEach(section -> {
			String path = "statistic_menu." + section + ".";
			if ( !section.equals("information") ) {
				ItemStack item = getMaterialFromMenu(path + "material",
						GetFiles.getInt(GetFiles.FileName.MENUS, path + "amount", 1), null);
				ItemMeta iMeta = item.getItemMeta();
				String name = GetFiles.getString(GetFiles.FileName.MENUS, path + "name", "This item has no name!",
						false);
				final String fromConfig = GetFiles
						.getString(GetFiles.FileName.CONFIG, "spawner_description.time_unit", "", false).toUpperCase();
				final int timeUnit = fromConfig.equals("SECOND") ? 20 : fromConfig.equals("MINUTE") ? 1200 : 1;
				name = name.replace("<min_delay>", String.valueOf(information.getMinSpawnDelay() / timeUnit));
				name = name.replace("<max_delay>", String.valueOf(information.getMaxSpawnDelay() / timeUnit));
				name = name.replace("<player_range>", String.valueOf(information.getRequiredPlayerRange()));
				name = name.replace("<range>", String.valueOf(information.getSpawnRange()));
				name = name.replace("<count>", String.valueOf(information.getSpawnCount()));
				name = name.replace("<mob>", information.getSpawnedType().toString());
				iMeta.setDisplayName(name);
				List<String> lore = new ArrayList<>();
				for ( String line : menu.getStringList(path + "lore") )
					lore.add(setColor(line));
				iMeta.setLore(lore);
				if ( GetFiles.getBoolean(GetFiles.FileName.MENUS, path + "glow", false) ) {
					iMeta.addEnchant(Enchantment.DURABILITY, 1, false);
					iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				item.setItemMeta(iMeta);
				inv.setItem(GetFiles.getInt(GetFiles.FileName.MENUS, path + "position", 1) - 1, item);
			}
		});
		player.openInventory(inv);
		return;
	}
}
