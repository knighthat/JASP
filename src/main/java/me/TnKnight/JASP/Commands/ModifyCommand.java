package me.TnKnight.JASP.Commands;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.JASP.Files.GetFiles;

public class ModifyCommand extends SubCommand
{

	@Override
	public String getName() {
		return "modify";
	}

	@Override
	public String getDescription() {
		return "Modifies spawner's statistics.";
	}

	@Override
	public String getSyntax() {
		return "/jasp modify [mindelay/maxdelay/playerrange/count/range] <input>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 3, new String[] { "mindelay", "maxdelay", "playerrange", "count", "range" }, 1) )
			return;
		if ( !player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER) ) {
			player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "require_spawner",
					"You are not holding a spawner!", true));
			return;
		}
		if ( !super.isInt(player, args[2]) )
			return;
		int time = Integer.parseInt(args[2]);
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		spawner.setAmount(1);
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		switch ( args[1].toLowerCase() ) {
			case "mindelay" :
				if ( time > sType.getMaxSpawnDelay() ) {
					sendError(player, "min_delay_too_high", "<input> is higher than <max_delay> - max delay!",
							sType.getMinSpawnDelay(), true);
					return;
				}
				sType.setMinSpawnDelay(time);
			break;

			case "maxdelay" :
				if ( time < sType.getMinSpawnDelay() ) {
					sendError(player, "max_delay_too_low", "<input> is low than <min_delay> - min delay!",
							sType.getMaxSpawnDelay(), false);
					return;
				}
				sType.setMaxSpawnDelay(time);
			break;
			case "playerrange" :
				sType.setRequiredPlayerRange(time);
			break;
			case "count" :
				sType.setSpawnCount(time);
			break;
			case "range" :
				sType.setSpawnRange(time);
			break;
		}
		sMeta.setBlockState(sType);
		sMeta.setLore(getStatistics(sMeta.getLore()));
		spawner.setItemMeta(sMeta);
		if ( GetFiles.getBoolean(GetFiles.FileName.CONFIG, "spawner_description.enable", false) )
			super.replaceLoreFromItem(spawner);
		player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		player.getInventory().addItem(spawner);
		player.updateInventory();
	}

	private void sendError( Player player, String path, String defaultMsg, Integer replacement, boolean minDelay ) {
		String msg = GetFiles.getString(GetFiles.FileName.CONFIG, path, defaultMsg, true);
		msg = msg.replace(minDelay ? "<min>" : "<max>", String.valueOf(replacement));
		player.sendMessage(msg);
	}
}
