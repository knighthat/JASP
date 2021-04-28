package me.TnKnight.JASP.Menus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;
import me.TnKnight.JASP.Files.GetFiles;

public abstract class MenusManager extends PStorage implements InventoryHolder
{
	protected Inventory inventory;
	protected PlayerMenuUtility utility;
	protected final String noName = "This item has no name!";

	public MenusManager(PlayerMenuUtility utility) {
		this.utility = utility;
	}

	public abstract String getName();

	public abstract String getSlots();

	public abstract String getFillItem();

	public abstract String getFillItemAmount();

	public abstract void MenuInteraction( InventoryClickEvent e );

	public abstract void setMenuItems();

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void open() {
		inventory = Bukkit.createInventory(this, GetFiles.getInt(GetFiles.FileName.MENUS, getSlots(), 1) * 9,
				GetFiles.getString(GetFiles.FileName.MENUS, getName(), noName, false));
		this.setFillItem();
		this.setMenuItems();
		this.utility.getPlayer().openInventory(inventory);
	}

	public void setFillItem() {
		for ( int i = 0 ; i < inventory.getSize() ; i++ )
			inventory.setItem(i, super.getMaterialFromMenu(getFillItem(),
					GetFiles.getInt(GetFiles.FileName.MENUS, getFillItemAmount(), 1), " "));
	}

	public FileConfiguration getMenus() {
		return JustAnotherSpawnerPickup.instance.menus.get();
	}
}
