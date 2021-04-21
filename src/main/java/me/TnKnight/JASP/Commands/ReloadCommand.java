package me.TnKnight.JASP.Commands;

import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand
{

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String getDescription() {
		return "Reload plugin.";
	}

	@Override
	public String getSyntax() {
		return "/jasp reload";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		plugin.cfg.reload();
		plugin.cmds.reload();
		plugin.menus.reload();
		player.sendMessage(super.getStringFromConfig("reload", "Reloaded successfully!", true));
	}

}
