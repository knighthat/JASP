package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.PStorage;

public class SetCommand extends SubCommand {

	public SetCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getDiscription() {
		String des = "Set a specific mod to holding spawner.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "&6/jasp set <mob name>";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		if (checkPermission && !player.hasPermission(getPerm)) {
			player.sendMessage(PStorage.noPerm());
			return;
		}
		if (args.length < 2) {
			player.sendMessage(getSyntax());
			return;
		}
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			String msg = "You are not holding a spawner!";
			if (!plugin.cfg.getConfig().getString("require_spawner").isEmpty())
				msg = plugin.cfg.getConfig().getString("require_spawner");
			player.sendMessage(PStorage.setColor(msg));
			return;
		}
		for (int i = 0; i < MobList.getAvailables().size(); i++)
			if (MobList.getAvailables().get(i).toString().equalsIgnoreCase(args[1])) {
				ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
				BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
				CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
				final String spawnedType = sType.getSpawnedType().toString();
				sType.setSpawnedType(EntityType.valueOf(MobList.getAvailables().get(i).toString()));
				sMeta.setBlockState(sType);
				if (plugin.cfg.getConfig().getBoolean("spawner_description.enable"))
					if (sMeta.hasLore()) {
						List<String> lore = new ArrayList<>();
						sMeta.getLore().forEach(
								string -> lore.add(string.replace(spawnedType, sType.getSpawnedType().toString())));
						sMeta.setLore(lore);
					} else {
						List<String> lore = new ArrayList<>();
						final CreatureSpawner info = (CreatureSpawner) sMeta.getBlockState();
						int time_unit = 1;
						switch (plugin.cfg.getConfig().getString("spawner_description.time_unit").toLowerCase()) {
						case "second":
							time_unit = 20;
							break;
						case "minute":
							time_unit = 1200;
							break;
						default:
							time_unit = 1;
							break;
						}
						for (String new_lore : PStorage.replaceLore(lore, info.getMinSpawnDelay() / time_unit,
								info.getMaxSpawnDelay() / time_unit, info.getRequiredPlayerRange(),
								info.getSpawnCount(), info.getSpawnRange(), info.getSpawnedType()))
							lore.add(new_lore);
						sMeta.setLore(lore);
					}
				spawner.setItemMeta(sMeta);
				if (player.getInventory().getItemInMainHand().getAmount() > 1) {
					int amount = 1;
					if (args.length >= 3)
						if (args[2].equalsIgnoreCase("all")) {
							amount = player.getInventory().getItemInMainHand().getAmount();
						} else if (PStorage.isInt(player, args[2]))
							amount = Integer.parseInt(args[2]);
					spawner.setAmount(amount);
					player.getInventory().getItemInMainHand()
							.setAmount(player.getInventory().getItemInMainHand().getAmount() - amount);
					player.getInventory().addItem(spawner);
				} else
					player.getInventory().setItemInMainHand(spawner);
				player.updateInventory();
				break;
			} else if (i == MobList.getAvailables().size() - 1) {
				Manager.get("moblist").onExecute(sender, new String[] { "moblist" }, true);
				break;
			}
	}

}
