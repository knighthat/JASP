package me.TnKnight.JASP;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.TnKnight.JASP.Commands.Manager;
import me.TnKnight.JASP.Files.CommandsYML;
import me.TnKnight.JASP.Files.ConfigYML;

public class JustAnotherSpawnerPickup extends JavaPlugin {

	public static JustAnotherSpawnerPickup instance;
	public ConfigYML cfg = new ConfigYML(this);
	public CommandsYML cmds = new CommandsYML(this);

	@Override
	public void onEnable() {
		instance = this;
		cfg.onStart();
		cmds.onStart();
		getCommand("justanotherspawnerpickup").setExecutor(new Manager(this));
		Bukkit.getPluginManager().registerEvents(new Listeners(this), this);

		// Finish Loading
		String started = PStorage.setColor(PStorage.prefix + " &aPlugin Started &a&lSuccessfully&a!");
		Bukkit.getConsoleSender().sendMessage(started);
	}

	@Override
	public void onDisable() {

		// Finish Disabling
		String ended = PStorage.setColor(PStorage.prefix + " &cPlugin &c&lDisabled&c!");
		Bukkit.getConsoleSender().sendMessage(ended);
	}
}
