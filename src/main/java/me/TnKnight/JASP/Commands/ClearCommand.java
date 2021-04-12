package me.TnKnight.JASP.Commands;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.Listeners;

public class ClearCommand extends SubCommand {

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getDescription() {
		return "Clear plugin's floating name and lore";
	}

	@Override
	public String getSyntax() {
		return "/jasp clear <radius>";
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if (args.length < 2) {
			player.spigot().sendMessage(getUsage().create());
			return;
		}
		int radius = super.isInt(player, args[1]) ? Integer.parseInt(args[1]) : 0;
		if (radius <= 0)
			return;
		player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
				.filter(e -> e.getType().equals(EntityType.ARMOR_STAND))
				.filter(e -> ((ArmorStand) e).getHealth() == Listeners.serial).forEach(e -> e.remove());
		player.sendMessage(super.getStringFromConfig("clear_message", "Done!", true));
	}

}
