package me.TnKnight.JASP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class ReloadCommand extends SubCommand {

	public ReloadCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String getDiscription() {
		String des = "Reload plugin.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "/jasp reload";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		if (checkPermission && !sender.hasPermission("jasp.admin.reload")) {
			sender.sendMessage(PStorage.noPerm());
			return;
		}
		plugin.cfg.reload();
		plugin.cmds.reload();
		String msg = "&aReloaded successfully!";
		if (getConfig().getString("reload") != null && !getConfig().getString("reload").isEmpty())
			msg = getConfig().getString("reload");
		sender.sendMessage(PStorage.setColor(PStorage.prefix + msg));
		if (sender instanceof Player)
			Bukkit.getLogger().warning(PStorage.setColor(PStorage.prefix + msg));
	}

}
