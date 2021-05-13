package me.TnKnight.JASP.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.JASP.Files.GetFiles;

public class ClearCommand extends SubCommand
{

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getDescription() {
		return "Clears plugin's floating name and lore";
	}

	@Override
	public String getSyntax() {
		return "/jasp clear <radius>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 2, null, 0) )
			return;
		int radius = super.isInt(player, args[1]) ? Integer.parseInt(args[1]) : 0;
		if ( radius <= 0 )
			return;
		super.getArmorStands(player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius))
				.forEach(armorStand -> armorStand.remove());
		player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "clear_message", "Done!", true));
	}
}
