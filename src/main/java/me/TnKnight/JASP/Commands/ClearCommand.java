package me.TnKnight.JASP.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.Listeners;
import me.TnKnight.JASP.PStorage;

public class ClearCommand extends SubCommand {

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getDescription() {
		String des = "List all available commands.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "/jasp help";
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
		int radius = PStorage.isInt(player, args[1]) ? Integer.parseInt(args[1]) : 0;
		if (radius <= 0)
			return;
		player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
				.filter(e -> e.getType().equals(EntityType.ARMOR_STAND))
				.filter(e -> ((ArmorStand) e).getHealth() == Listeners.serial).forEach(e -> e.remove());
		String msg = getConfig().getString("clear_message").isEmpty() ? "Done!"
				: getConfig().getString("clear_message");
		player.sendMessage(PStorage.setColor(msg));
	}

}
