package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class Manager implements CommandExecutor {

	JustAnotherSpawnerPickup plugin;

	public Manager(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
		sCommands.add(new HelpCommand(plugin));
		sCommands.add(new ReloadCommand(plugin));
		sCommands.add(new SetCommand(plugin));
		sCommands.add(new MobListCommand(plugin));
		sCommands.add(new SetNameCommand(plugin));
		sCommands.add(new LoreCommand(plugin));
		sCommands.add(new ClearCommand(plugin));

	}

	private static ArrayList<SubCommand> sCommands = new ArrayList<>();

	public static ArrayList<SubCommand> getSubCommands() {
		return sCommands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		boolean checkPermission = !sender.hasPermission("jasp.*");
		if (args.length == 0) {
			if (!checkPermission || (checkPermission && sender.hasPermission("jasp.command.help")))
				get("help").onExecute(sender, args, checkPermission);
			return true;
		} else
			get(args[0]).onExecute(sender, args, checkPermission);
		return true;
	}

	public static SubCommand get(String name) {
		Iterator<SubCommand> sCommands = Manager.sCommands.iterator();

		while (sCommands.hasNext()) {
			SubCommand sub = sCommands.next();
			if (sub.getName().equalsIgnoreCase(name))
				return sub;
		}
		return get("help");
	}
}

abstract class SubCommand {
	JustAnotherSpawnerPickup plugin;

	public SubCommand(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	protected String desPath = "commands." + getName() + ".description";
	protected String synPath = "commands." + getName() + ".usage";
	protected String getPerm = "jasp.command." + getName();
	protected String prefix = PStorage.setColor(PStorage.prefix);

	public abstract String getName();

	public abstract String getDiscription();

	public abstract String getSyntax();

	public abstract void onExecute(CommandSender sender, String[] args, boolean checkPermission);

	public FileConfiguration getCmds() {
		return plugin.cmds.getConfig();
	}

	public FileConfiguration getConfig() {
		return plugin.cfg.getConfig();
	}
}
