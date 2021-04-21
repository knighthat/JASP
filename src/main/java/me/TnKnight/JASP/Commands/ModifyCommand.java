package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ModifyCommand extends SubCommand
{

	@Override
	public String getName() {
		return "modify";
	}

	@Override
	public String getDescription() {
		return "Modify spawner's statistics.";
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
			player.sendMessage(super.getStringFromConfig("require_spawner", "You are not holding a spawner!", true));
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
					String msg = super.getStringFromConfig("min_delay_too_high",
							"<input> is higher than <max_delay> - max delay!", true);
					msg = msg.replace("<min>", String.valueOf(sType.getMinSpawnDelay()).replace("<max>",
							String.valueOf(sType.getMaxSpawnDelay())));
					player.sendMessage(msg);
					return;
				}
				sType.setMinSpawnDelay(time);
			break;

			case "maxdelay" :
				if ( time < sType.getMinSpawnDelay() ) {
					String msg = super.getStringFromConfig("max_delay_too_low",
							"<input> is low than <min_delay> - min delay!", true);
					msg = msg.replace("<min>", String.valueOf(sType.getMinSpawnDelay()).replace("<max>",
							String.valueOf(sType.getMaxSpawnDelay())));
					player.sendMessage(msg);
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
		List<String> lore = new ArrayList<>();
		for ( int i = 0 ; i < sMeta.getLore().size()
				- getConfig().getStringList("spawner_description.lore").size() ; i++ )
			lore.add(super.setColor(sMeta.getLore().get(i)));
		sMeta.setLore(lore);
		spawner.setItemMeta(sMeta);
		if ( getConfig().getBoolean("spawner_description.enable") )
			super.replaceLoreFromItem(spawner);
		player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		player.getInventory().addItem(spawner);
		player.updateInventory();

	}
}
