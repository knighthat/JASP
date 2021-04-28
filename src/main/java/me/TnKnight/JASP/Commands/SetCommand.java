package me.TnKnight.JASP.Commands;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.Files.GetFiles;

public class SetCommand extends SubCommand
{

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getDescription() {
		return "Set a specific mod to holding spawner.";
	}

	@Override
	public String getSyntax() {
		return "/jasp set <mob name>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 2, null, 0) )
			return;
		if ( !player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER) ) {
			player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "require_spawner",
					"You are not holding a spawner!", true));
			return;
		}
		if ( !MobList.getValuesToString().contains(args[1].toUpperCase()) ) {
			CommandsManager.get(player, !player.hasPermission("jasp.*"), "moblist").onExecute(player,
					new String[] { "moblist" });
			return;
		}
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		BlockStateMeta sMeta = (BlockStateMeta) spawner.getItemMeta();
		CreatureSpawner sType = (CreatureSpawner) sMeta.getBlockState();
		sType.setSpawnedType(EntityType.valueOf(args[1].toUpperCase()));
		sMeta.setBlockState(sType);
		sMeta.setLore(hasStatistic(sMeta.getLore()));
		spawner.setItemMeta(sMeta);
		if ( GetFiles.getBoolean(GetFiles.FileName.CONFIG, "spawner_description.enable", false) )
			super.replaceLoreFromItem(spawner);
		int amount = args.length < 3 ? 1
				: (args[2].equalsIgnoreCase("all") ? spawner.getAmount()
						: (super.isInt(player, args[2]) ? Integer.parseInt(args[2]) : 1));
		player.getInventory().getItemInMainHand().setAmount(spawner.getAmount() - amount);
		spawner.setAmount(amount);
		player.getInventory().addItem(spawner);
		player.updateInventory();
		return;

	}

}
