package me.TnKnight.JASP.Commands;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpCommand extends SubCommand
{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "List all available commands.";
	}

	@Override
	public String getSyntax() {
		return "/jasp help";
	}

	@Override
	public void onExecute( Player sender, String[] args ) {
		String header = super.getStringFromConfig("help_header", "", true);
		String footer = super.getStringFromConfig("help_footer", "", false);
		ComponentBuilder builder = new ComponentBuilder(header);
		for ( SubCommand sub : CommandsManager.sCommands ) {
			final String syntax = super.getStringFromCommands(sub.synPath, sub.getSyntax());
			final String description = super.getStringFromCommands(sub.desPath, sub.getDescription());
			final String permission = super.getStringFromCommands("commands." + sub.getName() + ".permission",
					sub.getName().equalsIgnoreCase("reload") ? "jasp.command.reload" : "jasp.command." + sub.getName());
			builder.append(Interactions.HnC("\n" + syntax, super.removeColor(syntax)));
			TextComponent des = new TextComponent(super.setColor(" " + description + " " + permission));
			builder.append(des);
		}
		sender.spigot().sendMessage(builder.create());
		sender.sendMessage(super.setColor(footer));
		return;
	}
}
