package me.TnKnight.JASP.Files;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class GetFiles
{
	public static FileConfiguration getConfig() {
		return JustAnotherSpawnerPickup.instance.cfg.get();
	}

	public static FileConfiguration getCommands() {
		return JustAnotherSpawnerPickup.instance.cmds.get();
	}

	public static FileConfiguration getMenus() {
		return JustAnotherSpawnerPickup.instance.menus.get();
	}

	public static enum FileName
	{
		CONFIG, COMMANDS, MENUS;
	}

	private static FileConfiguration getFile( FileName fileName ) {
		switch ( fileName ) {
			case COMMANDS :
				return getCommands();
			case CONFIG :
				return getConfig();
			case MENUS :
				return getMenus();
			default :
				return null;
		}
	}

	public static String getString( FileName fileName, String path, String defaultString, boolean addPrefix ) {
		if ( getFile(fileName).get(path) != null && !getFile(fileName).getString(path).isEmpty() )
			defaultString = getFile(fileName).getString(path);
		return PStorage.setColor((addPrefix ? PStorage.prefix : "") + defaultString);
	}

	public static Integer getInt( FileName fileName, String path, Integer defaultInt ) {
		if ( getFile(fileName).get(path) != null && getFile(fileName).isInt(path) )
			defaultInt = getFile(fileName).getInt(path);
		return defaultInt;
	}

	public static boolean getBoolean( FileName fileName, String path, boolean defaultBoolean ) {
		if ( getFile(fileName).get(path) != null && getFile(fileName).isBoolean(path) )
			defaultBoolean = getFile(fileName).getBoolean(path);
		return defaultBoolean;
	}

	public static List<String> getStringList( FileName fileName, String path ) {
		List<String> stringList = new ArrayList<>();
		if ( getFile(fileName).get(path) != null )
			stringList = getFile(fileName).getStringList(path);
		return stringList;
	}
}
