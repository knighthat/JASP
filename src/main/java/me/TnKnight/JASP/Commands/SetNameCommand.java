package me.TnKnight.JASP.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class SetNameCommand extends SubCommand {

	public SetNameCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "setname";
	}

	@Override
	public String getDiscription() {
		String des = "Name your spawner.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "/jasp setname <name>";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		FileConfiguration cfg = plugin.cfg.getConfig();
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		if (checkPermission && !player.hasPermission(getPerm)) {
			player.sendMessage(PStorage.noPerm());
			return;
		}
		if (args.length < 2) {
			player.sendMessage(getSyntax());
			return;
		}
		String input = new String();
		for (int i = 1; i < args.length; i++)
			input = input.concat(" " + args[i]).trim();
		int max = cfg.isInt("name_length.max") ? cfg.getInt("name_length.max") : 15;
		int min = cfg.isInt("name_length.min") ? cfg.getInt("name_length.min") : 3;
		boolean space = cfg.isBoolean("name_length.space_counter") ? cfg.getBoolean("name_length.space_counter") : true;
		boolean color = cfg.isBoolean("name_length.color_code_counter")
				? cfg.getBoolean("name_length.color_code_counter")
				: false;
		int count = 0;
		if (color)
			for (char c : PStorage.setColor(input).toCharArray())
				if (String.valueOf(c).equalsIgnoreCase("§"))
					count++;
		for (char c : PStorage.removeColor(input).toCharArray()) {
			count++;
			if (!space && String.valueOf(c).equalsIgnoreCase(" "))
				count--;
		}
		if (count > max) {
			String msg = "Your name is too long. Maximum is <max> &ccharacters.";
			if (!cfg.getString("name_too_long").isEmpty())
				msg = cfg.getString("name_too_long");
			player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<max>", String.valueOf(max))));
			return;
		}
		if (count < min) {
			String msg = "Your name is too short. Minimum is <min> &ccharacters.";
			if (!cfg.getString("name_too_short").isEmpty())
				msg = cfg.getString("name_too_short");
			player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<min>", String.valueOf(min))));
			return;
		}
		if (player.getInventory().getItemInMainHand().getAmount() > 1) {
			ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
			player.getInventory().getItemInMainHand().setAmount(spawner.getAmount() - 1);
			spawner.setAmount(1);
			ItemMeta sMeta = spawner.getItemMeta();
			sMeta.setDisplayName(PStorage.setColor(input));
			spawner.setItemMeta(sMeta);
			player.getInventory().addItem(spawner);
		} else {
			ItemMeta sMeta = player.getInventory().getItemInMainHand().getItemMeta();
			sMeta.setDisplayName(PStorage.setColor(input));
			player.getInventory().getItemInMainHand().setItemMeta(sMeta);
		}
		player.updateInventory();
		return;
	}

}
