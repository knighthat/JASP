package me.TnKnight.JASP.Commands;

import java.util.Iterator;

import org.bukkit.entity.Player;

import me.TnKnight.JASP.MobList;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class MobListCommand extends SubCommand {

	@Override
	public String getName() {
		return "moblist";
	}

	@Override
	public String getDescription() {
		return "List all the mobs that are available on this server.";
	}

	@Override
	public String getSyntax() {
		return "/jasp moblist";
	}

	@Override
	public void onExecute(Player player, String[] args) {
		final boolean checkPerm = !player.hasPermission("jasp.*");
		ComponentBuilder builder = new ComponentBuilder("");
		Iterator<String> mobs = MobList.getValuesToString().iterator();
		while (mobs.hasNext()) {
			String cmd = "/jasp set ";
			final String mob = mobs.next().toLowerCase();
			builder.append(Interactions.HnC("&6" + mob, cmd + mob));
			builder.append(super.setColor("&7" + (!mobs.hasNext() ? "." : ", ")));
		}
		builder.append(Interactions.HnC("\n" + CommandsManager.get(player, checkPerm, "set").getSyntax(),
				super.removeColor(CommandsManager.get(player, checkPerm, "set").getSyntax())));
		player.spigot().sendMessage(builder.create());
	}
}
