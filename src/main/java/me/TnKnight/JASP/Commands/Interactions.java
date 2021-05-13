package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.PStorage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Interactions extends PStorage implements TabCompleter
{

	JustAnotherSpawnerPickup plugin;

	public Interactions(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
		for ( SubCommand sub : CommandsManager.sCommands )
			subCommands.add(sub.getName());
	}

	private static List<String> subCommands = new ArrayList<>();

	@Override
	public List<String> onTabComplete( CommandSender sender, Command command, String alias, String[] args ) {
		List<String> result = new ArrayList<>();
		List<String> amount = new ArrayList<>(Arrays.asList("1", "2", "5", "10"));
		if ( args.length == 1 )
			for ( String sub : subCommands )
				if ( sub.startsWith(args[0].toLowerCase()) )
					result.add(sub);
		List<String> subs = new ArrayList<>();
		final boolean contains = subCommands.contains(args[0].toLowerCase());
		if ( args.length == 2 && contains ) {
			switch ( args[0].toLowerCase() ) {
				case "set" :
					MobList.getValues().forEach(mob -> subs.add(mob.toString().toLowerCase()));
				break;
				case "lore" :
					for ( String s : new String[] { "add", "set", "remove", "reset" } )
						subs.add(s);
				break;
				case "modify" :
					for ( String s : new String[] { "mindelay", "maxdelay", "playerrange", "count", "range" } )
						subs.add(s);
				break;
				case "setname" :
					subs.add("<name>");
				break;
				case "clear" :
					subs.add("<radius>");
					amount.forEach(amt -> subs.add(amt));
				break;
				case "getegg" :
					for ( String mobString : MobList.getValuesToString() )
						subs.add(mobString);
				break;
			}
			for ( String sub : subs )
				if ( sub.startsWith(args[1].toLowerCase()) )
					result.add(sub);
		}
		List<String> args2 = new ArrayList<>(
				Arrays.asList("add", "set", "remove", "mindelay", "maxdelay", "playerrange", "range", "count"));
		if ( args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("getegg"))
				&& MobList.getValuesToString().contains(args[1].toUpperCase()) ) {
			result.add("<amount>");
			amount.forEach(amt -> subs.add(amt));
			if ( args[0].equalsIgnoreCase("set") )
				subs.add("all");
		} else if ( args.length == 3 && contains && args2.contains(args[1].toLowerCase()) ) {
			switch ( args[1].toLowerCase() ) {
				case "add" :
					subs.add("<lore>");
				break;
				case "count" :
					subs.add("<count>");
					amount.forEach(amt -> subs.add(amt));
				break;
				case "set" :
				case "remove" :
					subs.add("<line>");
					amount.forEach(amt -> subs.add(amt));
				break;
				case "mindelay" :
				case "maxdelay" :
					subs.add("<time>");
					amount.forEach(amt -> subs.add(amt));
				break;
				case "playerrange" :
				case "range" :
					subs.add("<range>");
					amount.forEach(amt -> subs.add(amt));
				break;
			}
			for ( String sub : subs )
				if ( sub.startsWith(args[2].toLowerCase()) )
					result.add(sub);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static TextComponent HnC( String display, String click ) {
		FileConfiguration cfg = JustAnotherSpawnerPickup.instance.cfg.get();
		String hoverDisplay = "Click here to get command: <command>";
		if ( cfg.getString("hoverable_text") != null && !cfg.getString("hoverable_text").isEmpty() )
			hoverDisplay = cfg.getString("hoverable_text");
		TextComponent def = new TextComponent(setColor(display));
		def.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, click));
		ComponentBuilder hoverText = new ComponentBuilder(setColor(hoverDisplay.replace("<command>", click)));
		def.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, hoverText.create()));
		return def;
	}
}
