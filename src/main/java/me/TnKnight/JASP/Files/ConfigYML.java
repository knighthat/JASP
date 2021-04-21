package me.TnKnight.JASP.Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class ConfigYML
{

	JustAnotherSpawnerPickup plugin;

	public ConfigYML(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	// Create variables
	private File file = null;
	private FileConfiguration config = null;

	/**
	 * Starts up by checking if config.yml is created, then load into memory
	 */
	public void startup() {
		if ( file == null )
			file = new File(plugin.getDataFolder(), "config.yml");
		if ( !file.exists() )
			plugin.saveResource("config.yml", false);
		setConfig();
	}

	/**
	 * 
	 * @return if config.yml is loaded
	 */
	public void reload() {
		if ( file == null || config == null )
			startup();
		setConfig();
	}

	/**
	 * Gets a FileConfiguration for this plugin, read through "config.yml" If there
	 * is a default config.yml embedded in this plugin, it will be provided as a
	 * default for this Configuration.
	 * 
	 * @returns Plugin's config.yml configuration
	 */
	public FileConfiguration get() {
		if ( config == null )
			reload();
		return config;
	}

	private void setConfig() {
		config = YamlConfiguration.loadConfiguration(file);
		if ( get().getString("version") == null || get().getString("version").isEmpty()
				|| !get().getString("version").equalsIgnoreCase(plugin.getDescription().getVersion()) ) {
			for ( File f : plugin.getDataFolder().listFiles() )
				if ( f.getName().equalsIgnoreCase("config.yml.old") )
					f.delete();
			file.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
			file.delete();
			file = new File(plugin.getDataFolder(), "config.yml");
			plugin.saveResource("config.yml", false);
			plugin.sendMessage("&4Unsupported config version, creating new file...");
			reload();
		}
		try {
			Reader defaultCfg = new InputStreamReader(plugin.getResource("config.yml"), "UTF8");
			if ( defaultCfg != null )
				config.setDefaults(YamlConfiguration.loadConfiguration(defaultCfg));
			if ( !get().getString("prefix").isEmpty() )
				PStorage.prefix = get().getString("prefix") + "&r ";
		} catch ( UnsupportedEncodingException e1 ) {
			plugin.sendMessage("&cCould not load config.yml, please move or delete file");
			plugin.sendMessage("&cand let plugin create a new file for you!");
		}
	}
}
