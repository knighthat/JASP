package me.TnKnight.JASP;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.TnKnight.JASP.Commands.Interactions;
import me.TnKnight.JASP.Commands.Manager;
import me.TnKnight.JASP.Files.CommandsYML;
import me.TnKnight.JASP.Files.ConfigYML;

public class JustAnotherSpawnerPickup extends JavaPlugin {

	public static JustAnotherSpawnerPickup instance;
	public ConfigYML cfg = new ConfigYML(this);
	public CommandsYML cmds = new CommandsYML(this);
	public UpdateChecker uChecker;

	@Override
	public void onEnable() {
		instance = this;
		uChecker = new UpdateChecker(90460);
		cfg.onStart();
		cmds.onStart();
		getCommand("justanotherspawnerpickup").setExecutor(new Manager());
		getCommand("justanotherspawnerpickup").setTabCompleter(new Interactions(this));
		getServer().getPluginManager().registerEvents(new Listeners(), this);

		// Create a schedule of checking for new update every 3 hours starts when called
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

	public void sendMessage(String message) {
		if (message == null)
			return;
		Bukkit.getConsoleSender().sendMessage(PStorage.setColor(PStorage.prefix + message));
	}
}
