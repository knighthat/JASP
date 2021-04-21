package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.Menus.MenusManager;
import me.TnKnight.JASP.Menus.PlayerMenuUtility;
import me.TnKnight.JASP.Menus.StatisticMenu;

public class Listeners extends PStorage implements Listener
{

	FileConfiguration cfg = JustAnotherSpawnerPickup.instance.cfg.get();

	private final Double health = 9.139757425672141;
	public static final Double serialNumber = 9.13975715637207;

	@EventHandler
	public void onBreak( BlockBreakEvent e ) {
		if ( !e.getBlock().getType().equals(Material.SPAWNER) )
			return;
		ItemStack spawner = new ItemStack(Material.SPAWNER);
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		sType.setSpawnedType(((CreatureSpawner) e.getBlock().getState()).getSpawnedType());
		sMeta.setBlockState(sType);
		Double distance = (cfg.isDouble("holographic.distance") ? cfg.getDouble("holographic.distance") : 0) - 1;
		Map<String, Double> armorStands = new HashMap<>();
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		super.getArmorStands(e.getBlock().getWorld().getNearbyEntities(loc, 0, 2, 0)).forEach(armorStand -> {
			armorStands.put(armorStand.getCustomName(), armorStand.getLocation().getY());
			armorStand.remove();
		});
		if ( e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) ) {
			List<String> names = new ArrayList<>();
			if ( !armorStands.isEmpty() ) {
				sMeta.setBlockState(super.setSpawn(e.getBlock().getState(), sMeta.getBlockState()));
				Map<String, Double> as = armorStands.entrySet().stream()
						.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								( oldValue, newValue ) -> oldValue, LinkedHashMap::new));
				List<Double> compare = new ArrayList<>(as.values());
				names = new ArrayList<String>(as.keySet());
				if ( Math.round(compare.get(0) - compare.get(1)) == Math.round(cfg.getDouble("holographic.gap")) ) {
					sMeta.setDisplayName(names.get(0));
					names.remove(0);
				}
			}
			sMeta.setLore(names);
			spawner.setItemMeta(sMeta);
			if ( cfg.getBoolean("spawner_description.enable") )
				super.replaceLoreFromItem(spawner);
			e.getPlayer().getInventory().addItem(spawner);
		}
	}

	@EventHandler
	public void onPlace( BlockPlaceEvent e ) {
		if ( !e.getBlock().getType().equals(Material.SPAWNER) )
			return;
		CreatureSpawner iSpawner = (CreatureSpawner) ((BlockStateMeta) e.getItemInHand().getItemMeta()).getBlockState();
		CreatureSpawner bSpawner = (CreatureSpawner) e.getBlock().getState();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JustAnotherSpawnerPickup.instance, new Runnable() {
			@Override
			public void run() {
				bSpawner.setSpawnedType(iSpawner.getSpawnedType());
				bSpawner.update();
			}
		}, 5L);
		Double distance = (cfg.isDouble("holographic.distance") ? cfg.getDouble("holographic.distance") : 0) - 1;
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		if ( e.getItemInHand().getItemMeta().hasDisplayName() )
			spawnArmorStand(loc, e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
		if ( e.getItemInHand().getItemMeta().hasLore() ) {
			ItemMeta sMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
			double gap = cfg.isDouble("holographic.gap") ? cfg.getDouble("holographic.gap") : 0.4;
			double space = cfg.isDouble("holographic.space") ? cfg.getDouble("holographic.space") : 0.2;
			for ( int i = 0 ; i < sMeta.getLore().size() - cfg.getStringList("spawner_description.lore").size() ; i++ )
				spawnArmorStand(loc.add(0, i == 0 ? -gap : -space, 0), sMeta.getLore().get(i));
		}
	}

	private void spawnArmorStand( Location loc, String name ) {
		if ( name.isEmpty() )
			return;
		ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
		as.setGravity(false);
		as.setVisible(false);
		as.setHealth(health);
		as.setCustomNameVisible(true);
		as.setBasePlate(false);
		as.setCollidable(false);
		as.setInvulnerable(true);
		as.setCustomName(name);
	}

	@EventHandler
	public void onInteraction( PlayerInteractEvent e ) {
		if ( e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.SPAWNER) )
			if ( e.getPlayer().hasPermission("jasp.menu.statistic") ) {
				PlayerMenuUtility util = JustAnotherSpawnerPickup.getPlayerMenuUtility(e.getPlayer());
				util.setSpawner(e.getClickedBlock());
				StatisticMenu menu = new StatisticMenu(util);
				menu.open();
			}
	}

	@EventHandler
	public void onMenuInteraction( InventoryClickEvent e ) {
		if ( e.getClickedInventory() == null )
			return;
		if ( e.getInventory().getHolder() instanceof MenusManager ) {
			e.setCancelled(true);
			if ( e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.AIR) )
				return;
			MenusManager menu = (MenusManager) e.getInventory().getHolder();
			menu.MenuInteraction(e);
		}
	}
}
