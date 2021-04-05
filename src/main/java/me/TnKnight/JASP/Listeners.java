package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener {

	JustAnotherSpawnerPickup plugin;

	public Listeners(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	private final Double health = 9.139757425672141;
	public static final Double serial = 9.13975715637207;

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		Double distance = (plugin.cfg.getConfig().isDouble("holographic.distance")
				? plugin.cfg.getConfig().getDouble("holographic.distance")
				: 0) - 1;
		Map<ArmorStand, Double> armorStands = new HashMap<>();
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		for (Entity entity : e.getBlock().getWorld().getNearbyEntities(loc, 0, 2, 0))
			if (entity.getType().equals(EntityType.ARMOR_STAND) && ((ArmorStand) entity).getHealth() == serial) {
				armorStands.put((ArmorStand) entity, entity.getLocation().getY());
				entity.remove();
			}
		if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
			sMeta.setBlockState(PStorage.setSpawn(e.getBlock().getState(), sMeta.getBlockState()));
			List<String> lore = new ArrayList<>();
			List<String> names = new ArrayList<>();
			armorStands.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.forEachOrdered(value -> names.add(value.getKey().getCustomName()));
			if (!names.isEmpty())
				for (int i = 0; i < names.size(); i++)
					if (i == 0) {
						sMeta.setDisplayName(names.get(i));
					} else
						lore.add(names.get(i));
			sMeta.setLore(lore);
			spawner.setItemMeta(sMeta);
			if (plugin.getConfig().getBoolean("spawner_description.enable"))
				PStorage.replaceLoreFromItem(spawner);
			e.getPlayer().getInventory().addItem(spawner);
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		Double distance = (plugin.cfg.getConfig().isDouble("holographic.distance")
				? plugin.cfg.getConfig().getDouble("holographic.distance")
				: 0) - 1;
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		spawnArmorStand(loc, e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
			ItemMeta sMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
			Double gap = plugin.cfg.getConfig().isDouble("holographic.gap")
					? plugin.cfg.getConfig().getDouble("holographic.gap")
					: 0.4;
			Double space = plugin.cfg.getConfig().isDouble("holographic.space")
					? plugin.cfg.getConfig().getDouble("holographic.space")
					: 0.2;
			for (int i = 0; i < sMeta.getLore().size()
					- plugin.cfg.getConfig().getStringList("spawner_description.lore").size(); i++)
				spawnArmorStand(loc.add(0, i == 0 ? -gap : -space, 0), sMeta.getLore().get(i));

		}
	}

	private void spawnArmorStand(Location loc, String name) {
		if (name.isEmpty())
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
}
