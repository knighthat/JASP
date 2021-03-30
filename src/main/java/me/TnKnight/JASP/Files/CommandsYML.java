package me.TnKnight.JASP.Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class CommandsYML {
	
	JustAnotherSpawnerPickup plugin;
	public CommandsYML(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}
	
	private File file;
	private FileConfiguration cfg;
	
	public void onStart() {
		if(file == null)
			file = new File(plugin.getDataFolder(), "commands.yml");
		if(!file.exists())
			plugin.saveResource("commands.yml", false);
		setCommands();
	}
	
	public boolean reload() {
		if(file == null || cfg == null)
			onStart();
		setCommands();
		return true;
	}

	public FileConfiguration getConfig() {
		if(cfg == null)
			reload();
		return cfg;
	}
	
	private void setCommands() {
		cfg = YamlConfiguration.loadConfiguration(file);
		try {
			Reader defaultCfg = new InputStreamReader(plugin.getResource("commands.yml"), "UTF8");
			if(defaultCfg != null)
				cfg.setDefaults(YamlConfiguration.loadConfiguration(defaultCfg));
		} catch (UnsupportedEncodingException e) {
			ArrayList<String> msges = new ArrayList<>();
			msges.add("&cCould not load config.yml, please move or delete file");
			msges.add("&cand let plugin create a new file for you!");
			for(String msg : msges)
				Bukkit.getLogger().severe(PStorage.setColor(PStorage.prefix + msg));
		}
	}
}
