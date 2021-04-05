package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.JASP.PStorage;

public class ModifyCommand extends SubCommand {

	@Override
	public String getName() {
		return "modify";
	}

	@Override
	public String getDescription() {
		String des = "Modify spawner's statistics.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "/jasp modify [mindelay/maxdelay/playerrange/count/range] <input>";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		if (args.length < 3
				|| !Arrays.asList("mindelay", "maxdelay", "playerrange", "coung", "range").contains(args[1])) {
			player.spigot().sendMessage(getUsage().create());
			return;
		}
		if (!player.hasPermission(getPerm + "." + args[1].toLowerCase())) {
			player.sendMessage(PStorage.noPerm());
			return;
		}
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			String msg = "You are not holding a spawner!";
			if (!getConfig().getString("require_spawner").isEmpty())
				msg = getConfig().getString("require_spawner");
			player.sendMessage(PStorage.setColor(msg));
			return;
		}
		if (!PStorage.isInt(player, args[2]))
			return;
		int time = Integer.parseInt(args[2]);
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		spawner.setAmount(1);
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		switch (args[1].toLowerCase()) {
		case "mindelay":
			if (time > sType.getMaxSpawnDelay()) {
				String msg = "<input> is higher than <max_delay> - max delay!";
				if (!getConfig().getString("min_delay_too_high").isEmpty())
					msg = getConfig().getString("min_delay_too_high");
				msg = msg.replace("<min>", String.valueOf(sType.getMinSpawnDelay()).replace("<max>",
						String.valueOf(sType.getMaxSpawnDelay())));
				player.sendMessage(PStorage.setColor(prefix + msg));
				return;
			}
			sType.setMinSpawnDelay(time);
			break;

		case "maxdelay":
			if (time < sType.getMinSpawnDelay()) {
				String msg = "<input> is low than <min_delay> - min delay!";
				if (!getConfig().getString("max_delay_too_low").isEmpty())
					msg = getConfig().getString("max_delay_too_low");
				msg = msg.replace("<min>", String.valueOf(sType.getMinSpawnDelay()).replace("<max>",
						String.valueOf(sType.getMaxSpawnDelay())));
				player.sendMessage(PStorage.setColor(prefix + msg));
				return;
			}
			sType.setMaxSpawnDelay(time);
			break;
		case "playerrange":
			sType.setRequiredPlayerRange(time);
			break;
		case "count":
			sType.setSpawnCount(time);
			break;
		case "range":
			sType.setSpawnRange(time);
			break;
		}
		sMeta.setBlockState(sType);
		List<String> lore = new ArrayList<>();
		for (int i = 0; i < sMeta.getLore().size() - getConfig().getStringList("spawner_description.lore").size(); i++)
			lore.add(PStorage.setColor(sMeta.getLore().get(i)));
		sMeta.setLore(lore);
		spawner.setItemMeta(sMeta);
		if (getConfig().getBoolean("spawner_description.enable"))
			PStorage.replaceLoreFromItem(spawner);
		player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		player.getInventory().addItem(spawner);
		player.updateInventory();

	}
}
