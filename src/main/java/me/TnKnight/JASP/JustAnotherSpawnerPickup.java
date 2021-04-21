package me.TnKnight.JASP;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.TnKnight.JASP.Commands.Interactions;
import me.TnKnight.JASP.Commands.CommandsManager;
import me.TnKnight.JASP.Files.CommandsYML;
import me.TnKnight.JASP.Files.ConfigYML;
import me.TnKnight.JASP.Files.MenusYml;
import me.TnKnight.JASP.Menus.PlayerMenuUtility;

public class JustAnotherSpawnerPickup extends JavaPlugin
{

	public static JustAnotherSpawnerPickup instance;
	public ConfigYML cfg = new ConfigYML(this);
	public CommandsYML cmds = new CommandsYML(this);
	public MenusYml menus = new MenusYml(this);
	public UpdateChecker uChecker;

	private static final Map<Player, PlayerMenuUtility> playerMenuUtility = new HashMap<>();

	@Override
	public void onEnable() {
		instance = this;
		uChecker = new UpdateChecker(90460);
		cfg.startup();
		cmds.startup();
		menus.startup();

		getCommand("justanotherspawnerpickup").setExecutor(new CommandsManager());
		getCommand("justanotherspawnerpickup").setTabCompleter(new Interactions(this));
		getServer().getPluginManager().registerEvents(new Listeners(), this);

		// Create a schedule of checking for new update every 3 hours starts
		// when called
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				uChecker.check4Update();
			}

		}, 0, 216000);

		// Finish Loading
		sendMessage("&aPlugin Started Successfully!");
	}

	@Override
	public void onDisable() {
		// Finish Disabling
		sendMessage("&cPlugin Disabled!");
	}

	public void sendMessage( String message ) {
		if ( message == null )
			return;
		Bukkit.getConsoleSender().sendMessage(PStorage.setColor(PStorage.prefix + message));
	}

	public static PlayerMenuUtility getPlayerMenuUtility( Player player ) {
		if ( playerMenuUtility.containsKey(player) )
			return playerMenuUtility.get(player);
		PlayerMenuUtility utility;
		utility = new PlayerMenuUtility(player);
		playerMenuUtility.put(player, utility);
		return utility;
	}

}
