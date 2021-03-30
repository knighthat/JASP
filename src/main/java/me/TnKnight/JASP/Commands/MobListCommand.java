package me.TnKnight.JASP.Commands;

import org.bukkit.command.CommandSender;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.PStorage;

public class MobListCommand extends SubCommand {

	public MobListCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "moblist";
	}

	@Override
	public String getDiscription() {
		String des = "List all the mobs that are available on this server.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "&6/jasp moblist";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		if (checkPermission && !sender.hasPermission(getPerm)) {
			sender.sendMessage(PStorage.noPerm());
			return;
		}
		String availables = "";
		for (int i = 0; i < MobList.getAvailables().size(); i++)
			availables = availables + "&6" + MobList.getAvailables().get(i)
					+ (i < MobList.getAvailables().size() ? "&7, " : "&7.");
		sender.sendMessage(PStorage.setColor(availables.toLowerCase()));
		sender.sendMessage(Manager.get("set").getSyntax());
	}
}
