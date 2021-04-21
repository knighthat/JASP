package me.TnKnight.JASP.Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.Menu;

public class MenusYml
{

	JustAnotherSpawnerPickup plugin;

	public MenusYml(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	// Create variables
	private File file = null;
	private FileConfiguration cfg = null;

	/**
	 * Starts up by checking if commands.yml is created, then load into memory
	 */
	public void startup() {
		if ( file == null )
			file = new File(plugin.getDataFolder(), "menus.yml");
		if ( !file.exists() )
			plugin.saveResource("menus.yml", false);
		setCommands();
	}

	/**
	 * 
	 * @return if commands.yml is loaded
	 */
	public void reload() {
		if ( file == null || cfg == null )
			startup();
		setCommands();
	}

	/**
	 * Gets a FileConfiguration for this plugin, read through "commands.yml" If
	 * there is a default commands.yml embedded in this plugin, it will be provided
	 * as a default for this Configuration.
	 * 
	 * @returns Plugin's commands.yml configuration
	 */
	public FileConfiguration get() {
		if ( cfg == null )
			reload();
		return cfg;
	}

	private void setCommands() {
		cfg = YamlConfiguration.loadConfiguration(file);
		try {
			Reader defaultCfg = new InputStreamReader(plugin.getResource("menus.yml"), "UTF8");
			if ( defaultCfg != null )
				cfg.setDefaults(YamlConfiguration.loadConfiguration(defaultCfg));
		} catch ( UnsupportedEncodingException e ) {
			plugin.sendMessage("&cCould not load menus.yml, please move or delete file");
			plugin.sendMessage("&cand let plugin create a new file for you!");
		}
		Menu.updateMenu();
	}
}
