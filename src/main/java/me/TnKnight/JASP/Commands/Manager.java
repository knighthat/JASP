package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Manager implements CommandExecutor {

	JustAnotherSpawnerPickup plugin;

	public Manager(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
		sCommands.add(new HelpCommand());
		sCommands.add(new ReloadCommand());
		sCommands.add(new SetCommand());
		sCommands.add(new MobListCommand());
		sCommands.add(new SetNameCommand());
		sCommands.add(new LoreCommand());
		sCommands.add(new ClearCommand());
		sCommands.add(new ModifyCommand());
	}

	public static ArrayList<SubCommand> sCommands = new ArrayList<>();

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

	JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;

	protected String desPath = "commands." + getName() + ".description";
	protected String synPath = "commands." + getName() + ".usage";
	protected String getPerm = "jasp.command." + getName();
	protected String prefix = PStorage.setColor(PStorage.prefix);

	protected FileConfiguration getConfig() {
		return plugin.cfg.getConfig();
	}

	protected FileConfiguration getCmds() {
		return plugin.cmds.getConfig();
	}

	protected ComponentBuilder getUsage() {
		ComponentBuilder builder = new ComponentBuilder(prefix);
		builder.append(Interactions.HnC(getConfig().getString("usage").replace("<command>", getSyntax()),
				PStorage.removeColor(getSyntax())));
		return builder;
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract void onExecute(CommandSender sender, String[] args, boolean checkPermission);
}
