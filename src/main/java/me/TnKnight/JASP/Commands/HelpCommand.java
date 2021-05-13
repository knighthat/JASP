package me.TnKnight.JASP.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.JASP.Files.GetFiles;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class HelpCommand extends SubCommand
{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "Lists all available commands.";
	}

	@Override
	public String getSyntax() {
		return "/jasp help";
	}

	@Override
	public void onExecute( Player sender, String[] args ) {
		String header = GetFiles.getString(GetFiles.FileName.CONFIG, "help_header", "", true);
		String footer = GetFiles.getString(GetFiles.FileName.CONFIG, "help_footer", "", false);
		ComponentBuilder builder = new ComponentBuilder(header);
		for ( SubCommand sub : CommandsManager.sCommands ) {
			final String syntax = GetFiles.getString(GetFiles.FileName.COMMANDS, sub.synPath, sub.getSyntax(), false);
			final String description = GetFiles.getString(GetFiles.FileName.COMMANDS, sub.desPath, sub.getDescription(),
					false);
			final boolean showPerm = GetFiles.getBoolean(GetFiles.FileName.COMMANDS, "show_permission", true);
			final String permission = GetFiles.getString(GetFiles.FileName.COMMANDS,
					"commands." + sub.getName() + ".permission",
					sub.getName().equalsIgnoreCase("reload") ? "jasp.command.reload" : "jasp.command." + sub.getName(),
					false);
			builder.append(Interactions.HnC("\n" + syntax, super.removeColor(syntax)));
			builder.append(" " + description + (showPerm ? permission : ""));
		}
		sender.spigot().sendMessage(builder.create());
		sender.sendMessage(super.setColor(footer));
		return;
	}
}
