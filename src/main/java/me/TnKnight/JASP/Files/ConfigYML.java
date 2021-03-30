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

public class ConfigYML {

	JustAnotherSpawnerPickup plugin;

	public ConfigYML(JustAnotherSpawnerPickup plugin) {
		this.plugin = plugin;
	}

	private File file;
	private FileConfiguration cfg;

	public void onStart() {
		if (file == null)
			file = new File(plugin.getDataFolder(), "config.yml");
		if (!file.exists())
			plugin.saveResource("config.yml", false);
		setConfig();
	}

	public void reload() {
		if (file == null || cfg == null)
			onStart();
		setConfig();
	}

	public FileConfiguration getConfig() {
		if (cfg == null)
			reload();
		return cfg;
	}

	private void setConfig() {
		cfg = YamlConfiguration.loadConfiguration(file);
		try {
			Reader defaultCfg = new InputStreamReader(plugin.getResource("config.yml"), "UTF8");
			if (defaultCfg != null)
				cfg.setDefaults(YamlConfiguration.loadConfiguration(defaultCfg));
			if (!getConfig().getString("prefix").isEmpty())
				PStorage.prefix = getConfig().getString("prefix") + "&r ";
		} catch (UnsupportedEncodingException e1) {
			ArrayList<String> msges = new ArrayList<>();
			msges.add("&cCould not load config.yml, please move or delete file");
			msges.add("&cand let plugin create a new file for you!");
			for (String msg : msges)
				Bukkit.getLogger().severe(PStorage.setColor(PStorage.prefix + msg));
		}
	}
}
