package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners extends PStorage implements Listener {

	FileConfiguration cfg = JustAnotherSpawnerPickup.instance.cfg.getConfig();

	private final Double health = 9.139757425672141;
	public static final Double serial = 9.13975715637207;

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		ItemStack spawner = new ItemStack(Material.SPAWNER);
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		sType.setSpawnedType(((CreatureSpawner) e.getBlock().getState()).getSpawnedType());
		sMeta.setBlockState(sType);
		Double distance = (cfg.isDouble("holographic.distance") ? cfg.getDouble("holographic.distance") : 0) - 1;
		Map<String, Double> armorStands = new HashMap<>();
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		e.getBlock().getWorld().getNearbyEntities(loc, 0, 2, 0).stream()
				.filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND)).forEach(armorStand -> {
					armorStands.put(armorStand.getCustomName(), armorStand.getLocation().getY());
					armorStand.remove();
				});
		if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
			List<String> names = new ArrayList<>();
			if (!armorStands.isEmpty()) {
				sMeta.setBlockState(super.setSpawn(e.getBlock().getState(), sMeta.getBlockState()));
				Map<String, Double> as = armorStands.entrySet().stream()
						.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								(oldValue, newValue) -> oldValue, LinkedHashMap::new));
				List<Double> compare = new ArrayList<>(as.values());
				names = new ArrayList<String>(as.keySet());
				if (Math.round(compare.get(0) - compare.get(1)) == Math.round(cfg.getDouble("holographic.gap"))) {
					sMeta.setDisplayName(names.get(0));
					names.remove(0);
				}
			}
			sMeta.setLore(names);
			spawner.setItemMeta(sMeta);
			if (cfg.getBoolean("spawner_description.enable"))
				super.replaceLoreFromItem(spawner);
			e.getPlayer().getInventory().addItem(spawner);
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		Double distance = (cfg.isDouble("holographic.distance") ? cfg.getDouble("holographic.distance") : 0) - 1;
		Location loc = e.getBlock().getLocation().add(.5, distance, .5);
		if (e.getItemInHand().getItemMeta().hasDisplayName())
			spawnArmorStand(loc, e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
		if (e.getItemInHand().getItemMeta().hasLore()) {
			ItemMeta sMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
			double gap = cfg.isDouble("holographic.gap") ? cfg.getDouble("holographic.gap") : 0.4;
			double space = cfg.isDouble("holographic.space") ? cfg.getDouble("holographic.space") : 0.2;
			for (int i = 0; i < sMeta.getLore().size() - cfg.getStringList("spawner_description.lore").size(); i++)
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
