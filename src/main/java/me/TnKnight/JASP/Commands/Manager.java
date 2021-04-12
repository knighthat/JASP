package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Manager extends PStorage implements CommandExecutor {

	public Manager() {
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
		if (!(sender instanceof Player))
			return true;
		Player player = (Player) sender;
		boolean checkPermission = !sender.hasPermission("jasp.*");
		if (args.length == 0) {
			if (!checkPermission || (checkPermission && sender.hasPermission("jasp.command.help")))
				get(player, checkPermission, "help").onExecute((Player) sender, args);
			return true;
		} else if (get(player, checkPermission, args[0]) != null)
			get(player, checkPermission, args[0]).onExecute((Player) sender, args);
		return true;
	}

	public static SubCommand get(Player player, boolean checkPerm, String name) {
		FileConfiguration cfg = JustAnotherSpawnerPickup.instance.cfg.getConfig();
		final String perm = name.equalsIgnoreCase("reload") ? "jasp.admin.reload" : "jasp.command." + name;
		Iterator<SubCommand> sCommands = Manager.sCommands.iterator();
		while (sCommands.hasNext()) {
			SubCommand sub = sCommands.next();
			if (sub.getName().equalsIgnoreCase(name)) {
				if (checkPerm && !player.hasPermission(perm)) {
					String msg = "You don't have permission [<perm>] to execute that command!";
					if (cfg.getString("no_permission") != null && !cfg.getString("no_permission").isEmpty())
						msg = cfg.getString("no_permission");
					player.sendMessage(setColor(PStorage.prefix + msg.replace("<perm>", perm)));
					return null;
				}
				return sub;
			}
		}
		return get(player, true, "help");
	}
}

abstract class SubCommand extends PStorage {

	protected JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;
	protected String desPath = "commands." + getName() + ".description";
	protected String synPath = "commands." + getName() + ".usage";
	protected String getPerm = "jasp.command." + getName();

	protected FileConfiguration getConfig() {
		return plugin.cfg.getConfig();
	}

	protected ComponentBuilder getUsage() {
		ComponentBuilder builder = new ComponentBuilder(prefix);
		String syntax = super.removeColor(super.getStringFromCommands(synPath, getSyntax()));
		builder.append(
				Interactions.HnC(plugin.cfg.getConfig().getString("usage").replace("<command>", syntax), syntax));
		return builder;
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract void onExecute(Player player, String[] args);
}
