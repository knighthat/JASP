package me.TnKnight.JASP.Commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.Files.GetFiles;

public class GetEggCommand extends SubCommand
{

	@Override
	public String getName() {
		return "getegg";
	}

	@Override
	public String getDescription() {
		return "Grenerates spawn egg that contains indicated mob";
	}

	@Override
	public String getSyntax() {
		return "/jasp getegg <mob> <amount>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 2, null, 0) )
			return;
		if ( !MobList.getValuesToString().contains(args[1].toUpperCase()) )
			return;
		String permNode = "jasp.command.getegg.";
		if ( !player.hasPermission(permNode + args[1].toUpperCase()) && !player.hasPermission(permNode + "*") ) {
			GetFiles.noPerm(player, permNode + args[1].toUpperCase());
			return;
		}
		Material spawn_egg = Material.getMaterial(args[1].toUpperCase() + "_SPAWN_EGG");
		int amount = args.length >= 3 && super.isInt(player, args[2]) ? Integer.parseInt(args[2]) : 1;
		player.getInventory().addItem(new ItemStack(spawn_egg, amount));
		player.updateInventory();
	}

}
