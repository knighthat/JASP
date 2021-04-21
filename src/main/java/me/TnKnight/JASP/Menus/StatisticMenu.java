package me.TnKnight.JASP.Menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatisticMenu extends MenusManager
{

	public StatisticMenu(PlayerMenuUtility utility) {
		super(utility);
	}

	@Override
	public String getName() {
		return "statistic_menu.information.title";
	}

	@Override
	public String getSlots() {
		return "statistic_menu.information.size";
	}

	@Override
	public String getFillItem() {
		return "statistic_menu.information.fill";
	}

	@Override
	public String getFillItemAmount() {
		return "statistic_menu.information.fill_amount";
	}

	@Override
	public void MenuInteraction( InventoryClickEvent e ) {

	}

	@Override
	public void setMenuItems() {
		CreatureSpawner information = (CreatureSpawner) utility.getSpawner().getState();
		getMenus().getConfigurationSection("statistic_menu").getKeys(false).forEach(section -> {
			String path = "statistic_menu." + section + ".";
			if ( !section.equals("information") ) {
				ItemStack item = getMaterialFromMenu(path + "material", getIntFromMenus(path + "amount", 1), null);
				ItemMeta iMeta = item.getItemMeta();
				String name = getStringFromMenus(path + "name", "This item has no name!");
				final String fromConfig = getStringFromConfig("spawner_description.time_unit", "", false).toUpperCase();
				final int timeUnit = fromConfig.equals("SECOND") ? 20 : fromConfig.equals("MINUTE") ? 1200 : 1;
				name = name.replace("<min_delay>", String.valueOf(information.getMinSpawnDelay() / timeUnit));
				name = name.replace("<max_delay>", String.valueOf(information.getMaxSpawnDelay() / timeUnit));
				name = name.replace("<player_range>", String.valueOf(information.getRequiredPlayerRange()));
				name = name.replace("<range>", String.valueOf(information.getSpawnRange()));
				name = name.replace("<count>", String.valueOf(information.getSpawnCount()));
				name = name.replace("<mob>", information.getSpawnedType().toString());
				iMeta.setDisplayName(name);
				List<String> lore = new ArrayList<>();
				for ( String line : getMenus().getStringList(path + "lore") )
					lore.add(setColor(line));
				iMeta.setLore(lore);
				if ( getBooleanFromMenus(path + "glow", false) ) {
					iMeta.addEnchant(Enchantment.DURABILITY, 1, false);
					iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				item.setItemMeta(iMeta);
				inventory.setItem(getIntFromMenus(path + "position", 1) - 1, item);
			}
		});
	}

}
