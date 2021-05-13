package me.TnKnight.JASP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.Files.GetFiles;
import me.TnKnight.JASP.Menus.MenusManager;
import me.TnKnight.JASP.Menus.PlayerMenuUtility;
import me.TnKnight.JASP.Menus.StatisticMenu;

public class Listeners extends PStorage implements Listener
{
	private final Double loreHealth = 1.380750095079569;
	public static final Double loreSerial = 1.380750060081482;

	private final Double nameHealth = 6.618606880531558;
	public static final Double nameSerial = 6.618607044219971;

	private Double getDistance() {
		return GetFiles.getDouble(GetFiles.FileName.CONFIG, "holographic.distance", 0D) - 1;
	}

	@EventHandler
	public void onChat( AsyncPlayerChatEvent e ) {
		ItemMeta hMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
		hMeta.setDisplayName(super.setColor(e.getMessage()));
		e.getPlayer().getInventory().getItemInMainHand().setItemMeta(hMeta);
		e.getPlayer().updateInventory();
	}

	@EventHandler
	public void onBreak( BlockBreakEvent e ) {
		if ( !e.getBlock().getType().equals(Material.SPAWNER) )
			return;
		ItemStack spawner = new ItemStack(Material.SPAWNER);
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		sType.setSpawnedType(((CreatureSpawner) e.getBlock().getState()).getSpawnedType());
		sMeta.setBlockState(sType);
		Location loc = e.getBlock().getLocation().add(.5, getDistance(), .5);
		Stream<ArmorStand> armorStands = super.getArmorStands(loc.getWorld().getNearbyEntities(loc, 0, 2, 0));
		if ( e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) ) {
			List<String> lore = new ArrayList<>();
			armorStands.forEach(as -> {
				if ( as.getHealth() == nameSerial )
					sMeta.setDisplayName(as.getCustomName());
				if ( as.getHealth() == loreSerial )
					lore.add(as.getCustomName());
				as.remove();
			});
			sMeta.setLore(lore);
			spawner.setItemMeta(sMeta);
			if ( GetFiles.getBoolean(GetFiles.FileName.CONFIG, "spawner_description.enable", true) )
				super.replaceLoreFromItem(spawner);
			loc.getWorld().dropItemNaturally(loc, spawner);
		} else
			armorStands.forEach(as -> as.remove());
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
		Location loc = e.getBlock().getLocation().add(.5, getDistance(), .5);
		if ( e.getItemInHand().getItemMeta().hasDisplayName() )
			spawnArmorStand(loc, e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName(), true);
		if ( e.getItemInHand().getItemMeta().hasLore() ) {
			ItemMeta sMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
			double gap = GetFiles.getDouble(GetFiles.FileName.CONFIG, "holographic.gap", .4);
			double space = GetFiles.getDouble(GetFiles.FileName.CONFIG, "holographic.space", .2);
			for ( int i = 0 ; i < sMeta.getLore().size()
					- GetFiles.getStringList(GetFiles.FileName.CONFIG, "spawner_description.lore").size() ; i++ )
				spawnArmorStand(loc.add(0, i == 0 ? -gap : -space, 0), sMeta.getLore().get(i), false);
		}
	}

	private void spawnArmorStand( Location loc, String name, Boolean isName ) {
		if ( name.isEmpty() )
			return;
		ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
		as.setGravity(false);
		as.setVisible(false);
		as.setHealth(isName ? nameHealth : loreHealth);
		as.setCustomNameVisible(true);
		as.setBasePlate(false);
		as.setCollidable(false);
		as.setInvulnerable(true);
		as.setCustomName(name);
	}

	private Map<Player, Long> cooldown = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteraction( PlayerInteractEvent e ) {
		if ( !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| !e.getClickedBlock().getType().equals(Material.SPAWNER) )
			return;
		Player player = e.getPlayer();
		Material holding = player.getInventory().getItemInMainHand().getType();
		if ( cooldown.containsKey(player) && cooldown.get(player) > System.currentTimeMillis() ) {
			e.setCancelled(true);
			return;
		}
		cooldown.put(player, System.currentTimeMillis() + 500);
		if ( holding.equals(Material.AIR) && player.hasPermission("jasp.menu.statistic") ) {
			PlayerMenuUtility util = JustAnotherSpawnerPickup.getPlayerMenuUtility(player);
			util.setSpawner(e.getClickedBlock());
			StatisticMenu menu = new StatisticMenu(util);
			menu.open();
		}
		if ( holding.toString().endsWith("SPAWN_EGG") ) {
			String holdingString = holding.toString().replace("_SPAWN_EGG", "");
			if ( !player.hasPermission("jasp.changemob." + holdingString)
					&& !player.hasPermission("jasp.changemob.*") ) {
				GetFiles.noPerm(player, "jasp.changemob." + holdingString);
				e.setCancelled(true);
				return;
			}
			switch ( holdingString ) {
				case "MOOSHROOM" :
					holdingString = "MUSHROOM_COW";
				break;
			}
			if ( !MobList.getValuesToString().contains(holdingString) ) {
				player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "unsupported_spawned_type",
						"This creature is not supported!", true));
				e.setCancelled(true);
				return;
			}
			final EntityType spawnedType = EntityType.valueOf(holdingString);
			CreatureSpawner cSpawner = (CreatureSpawner) e.getClickedBlock().getState();
			if ( cSpawner.getSpawnedType().equals(spawnedType) ) {
				String msg = GetFiles.getString(GetFiles.FileName.CONFIG, "same_spawned_type",
						"This spawner's already spawning this type of mob.", true);
				player.sendMessage(msg);
				e.setCancelled(true);
				return;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(JustAnotherSpawnerPickup.instance, new Runnable() {

				@Override
				public void run() {
					cSpawner.setSpawnedType(spawnedType);
					cSpawner.update();
					player.getInventory().getItemInMainHand()
							.setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
				}
			}, 5L);
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
