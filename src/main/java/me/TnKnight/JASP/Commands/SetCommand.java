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

import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.PStorage;

public class SetCommand extends SubCommand {

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getDescription() {
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
			player.spigot().sendMessage(getUsage().create());
			return;
		}
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			String msg = "You are not holding a spawner!";
			if (!getConfig().getString("require_spawner").isEmpty())
				msg = getConfig().getString("require_spawner");
			player.sendMessage(PStorage.setColor(msg));
			return;
		}
		for (int i = 0; i < MobList.getAvailables().size(); i++)
			if (MobList.getAvailables().get(i).toString().equalsIgnoreCase(args[1])) {
				ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
				BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
				CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
				sType.setSpawnedType(EntityType.valueOf(MobList.getAvailables().get(i).toString()));
				sMeta.setBlockState(sType);
				List<String> lore = new ArrayList<>();
				for (int s = 0; s < sMeta.getLore().size()
						- getConfig().getStringList("spawner_description.lore").size(); s++)
					lore.add(PStorage.setColor(sMeta.getLore().get(s)));
				sMeta.setLore(lore);
				spawner.setItemMeta(sMeta);
				if (getConfig().getBoolean("spawner_description.enable"))
					PStorage.replaceLoreFromItem(spawner);
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
				return;
			}
		Manager.get("moblist").onExecute(sender, new String[] { "moblist" }, true);
	}

}
