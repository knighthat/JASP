package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;
import me.TnKnight.JASP.Files.GetFiles;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CommandsManager extends PStorage implements CommandExecutor
{

	public CommandsManager() {
		sCommands.add(new HelpCommand());
		sCommands.add(new ReloadCommand());
		sCommands.add(new SetCommand());
		sCommands.add(new MobListCommand());
		sCommands.add(new SetNameCommand());
		sCommands.add(new LoreCommand());
		sCommands.add(new ClearCommand());
		sCommands.add(new ModifyCommand());
		sCommands.add(new GetEggCommand());
	}

	public static ArrayList<SubCommand> sCommands = new ArrayList<>();

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( !(sender instanceof Player) )
			return true;
		Player player = (Player) sender;
		boolean checkPermission = !sender.hasPermission("jasp.*");
		if ( args.length == 0 ) {
			if ( !checkPermission || (checkPermission && sender.hasPermission("jasp.command.help")) )
				get(player, checkPermission, "help").onExecute((Player) sender, args);
			return true;
		} else if ( get(player, checkPermission, args[0]) != null )
			get(player, checkPermission, args[0]).onExecute((Player) sender, args);
		return true;
	}

	public static SubCommand get( Player player, boolean checkPerm, String name ) {
		final String perm = name.equalsIgnoreCase("reload") ? "jasp.admin.reload" : "jasp.command." + name;
		List<String> exceptions = new ArrayList<>(Arrays.asList("getegg"));
		Iterator<SubCommand> sCommands = CommandsManager.sCommands.iterator();
		while ( sCommands.hasNext() ) {
			SubCommand sub = sCommands.next();
			if ( sub.getName().equalsIgnoreCase(name) ) {
				if ( !exceptions.contains(name.toLowerCase()) && checkPerm && !player.hasPermission(perm) ) {
					GetFiles.noPerm(player, perm);
					return null;
				}
				return sub;
			}
		}
		return get(player, true, "help");
	}
}

abstract class SubCommand extends PStorage
{

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract void onExecute( Player player, String[] args );

	protected JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;
	protected String desPath = "commands." + getName() + ".description";
	protected String synPath = "commands." + getName() + ".usage";
	protected String getPerm = "jasp.command." + getName();

	protected ComponentBuilder getUsage() {
		ComponentBuilder builder = new ComponentBuilder(super.setColor(prefix));
		String syntax = super.removeColor(GetFiles.getString(GetFiles.FileName.COMMANDS, synPath, getSyntax(), false));
		builder.append(Interactions.HnC(plugin.cfg.get().getString("usage").replace("<command>", syntax), syntax));
		return builder;
	}

	protected boolean argsChecker( Player player, String[] input, int length, String[] contains, int arg ) {
		boolean pass = input.length >= length;
		if ( pass && contains != null )
			pass = Arrays.asList(contains).contains(input[arg]);
		if ( !pass )
			player.spigot().sendMessage(getUsage().create());
		return !pass;
	}

	protected List<String> getStatistics( List<String> lore ) {
		List<String> new_lore = new ArrayList<>();
		if ( lore.isEmpty() )
			return new_lore;
		Integer hasStats = lore.size()
				- GetFiles.getStringList(GetFiles.FileName.CONFIG, "spawner_description.lore").size();
		if ( hasStats <= 0 )
			return new_lore;
		for ( int i = 0 ; i < (hasStats > 0 ? hasStats : lore.size()) ; i++ )
			new_lore.add(lore.get(i));
		return new_lore;
	}

}
