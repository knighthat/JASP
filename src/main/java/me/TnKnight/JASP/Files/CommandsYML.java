package me.TnKnight.JASP.Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;

public class CommandsYML
{

	JustAnotherSpawnerPickup plugin;

	public CommandsYML(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	private File file = null;
	private FileConfiguration cfg = null;

	public void startup() {
		if ( file == null )
			file = new File(plugin.getDataFolder(), "commands.yml");
		if ( !file.exists() )
			plugin.saveResource("commands.yml", false);
		setCommands();
	}

	public void reload() {
		if ( file == null || cfg == null )
			startup();
		setCommands();
	}

	public FileConfiguration get() {
		if ( cfg == null )
			reload();
		return cfg;
	}

	private void setCommands() {
		cfg = YamlConfiguration.loadConfiguration(file);
		try {
			Reader defaultCfg = new InputStreamReader(plugin.getResource("commands.yml"), "UTF8");
			if ( defaultCfg != null )
				cfg.setDefaults(YamlConfiguration.loadConfiguration(defaultCfg));
		} catch ( UnsupportedEncodingException e ) {
			plugin.sendMessage("&cCould not load commands.yml, please move or delete file");
			plugin.sendMessage("&cand let plugin create a new file for you!");
		}
	}
}
