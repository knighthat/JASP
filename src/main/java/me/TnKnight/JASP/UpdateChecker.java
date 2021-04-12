package me.TnKnight.JASP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

	private JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;
	private int spigotProjectID;
	private URL website;
	private String currentVersion;
	private String webVersion;

	public UpdateChecker(int spigotProjectID) {
		this.spigotProjectID = spigotProjectID;
		currentVersion = plugin.getDescription().getVersion();
		try {
			website = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + spigotProjectID);
		} catch (MalformedURLException e) {
			plugin.sendMessage("&eHaving trouble with update checker. Please report the issue");
			plugin.sendMessage("&eto the owner of the plugin for fixing. Thank you!");
		}
	}

	public String getURL() {
		return "https://www.spigotmc.org/resources/" + spigotProjectID;
	}

	public void check4Update() {
		try {
			plugin.sendMessage("&bChecking for new update...");
			URLConnection connection;
			connection = website.openConnection();
			webVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
			if (!currentVersion.equals(webVersion)) {
				plugin.sendMessage("&cNew version " + webVersion + " is now available! Please visit website");
				plugin.sendMessage("&c" + getURL() + " for the lastest version.");
			} else
				plugin.sendMessage("&aYou are up to date!");
		} catch (IOException e) {
			plugin.sendMessage("&eHaving trouble with update checker. Please report the issue");
			plugin.sendMessage("&eto the owner of the plugin for fixing. Thank you!");
		}
	}
}
