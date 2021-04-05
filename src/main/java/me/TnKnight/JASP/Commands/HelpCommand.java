package me.TnKnight.JASP.Commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.PStorage;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpCommand extends SubCommand {

	@Override
	public String getName() {
		return "help";
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
		if (checkPermission && !sender.hasPermission(getPerm)) {
			sender.sendMessage(PStorage.noPerm());
			return;
		}
		if (sender instanceof Player) {
			String header = new String();
			String footer = new String();
			if (!getConfig().getString("help_header").isEmpty())
				header = PStorage.setColor(getConfig().getString("help_header"));
			if (!getConfig().getString("help_footer").isEmpty())
				footer = PStorage.setColor(getConfig().getString("help_footer"));
			ComponentBuilder builder = new ComponentBuilder(header);
			for (SubCommand sub : Manager.sCommands) {
				builder.append(Interactions.HnC("\n" + sub.getSyntax(), PStorage.removeColor(sub.getSyntax())));
				TextComponent des = new TextComponent(PStorage.setColor(" " + sub.getDescription()));
				builder.append(des);
			}
			((Player) sender).spigot().sendMessage(builder.create());
			sender.sendMessage(PStorage.setColor(footer));
			return;
		}
		ArrayList<String> msges = new ArrayList<>();
		if (!getConfig().getString("help_header").isEmpty())
			msges.add(getConfig().getString("help_header"));
		for (int i = 0; i < Manager.sCommands.size(); i++)
			msges.add(Manager.sCommands.get(i).getSyntax() + " " + Manager.sCommands.get(i).getDescription());
		if (!getConfig().getString("help_footer").isEmpty())
			msges.add(getConfig().getString("help_footer"));
		for (String msg : msges)
			sender.sendMessage(PStorage.setColor(msg.replace("<prefix>", PStorage.prefix)));
	}

}
