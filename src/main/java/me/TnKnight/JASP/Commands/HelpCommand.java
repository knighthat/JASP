package me.TnKnight.JASP.Commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class HelpCommand extends SubCommand {

	public HelpCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getDiscription() {
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
		if (checkPermission && !sender.hasPermission(getPerm)) {
			sender.sendMessage(PStorage.noPerm());
			return;
		}
		ArrayList<String> msges = new ArrayList<>();
		if (!getConfig().getString("help_header").isEmpty())
			msges.add(getConfig().getString("help_header"));
		for (int i = 0; i < Manager.getSubCommands().size(); i++)
			msges.add(Manager.getSubCommands().get(i).getSyntax() + " "
					+ Manager.getSubCommands().get(i).getDiscription());
		if (!getConfig().getString("help_footer").isEmpty())
			msges.add(getConfig().getString("help_footer"));
		for (String msg : msges)
			sender.sendMessage(PStorage.setColor(msg.replace("<prefix>", PStorage.prefix)));
	}

}
