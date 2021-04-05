package me.TnKnight.JASP.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.TnKnight.JASP.MobList;
import me.TnKnight.JASP.PStorage;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class MobListCommand extends SubCommand {

	@Override
	public String getName() {
		return "moblist";
	}

	@Override
	public String getDescription() {
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
		if (sender instanceof Player) {
			ComponentBuilder builder = new ComponentBuilder("");
			for (int loop = 0; loop < MobList.getAvailables().size(); loop++) {
				String cmd = "/jasp set ";
				final String mob = MobList.getAvailables().get(loop).toString().toLowerCase();
				builder.append(Interactions.HnC("&6" + mob, cmd + mob));
				builder.append(PStorage.setColor("&7" + (loop == MobList.getAvailables().size() - 1 ? "." : ", ")));
			}
			builder.append(Interactions.HnC("\n" + Manager.get("set").getSyntax(),
					PStorage.removeColor(Manager.get("set").getSyntax())));
			((Player) sender).spigot().sendMessage(builder.create());
		} else {
			String availables = "";
			for (int i = 0; i < MobList.getAvailables().size(); i++)
				availables = availables + "&6" + MobList.getAvailables().get(i)
						+ (i < MobList.getAvailables().size() - 1 ? "&7, " : "&7.");
			sender.sendMessage(PStorage.setColor(availables.toLowerCase()));
			sender.sendMessage(Manager.get("set").getSyntax());

		}
	}
}
