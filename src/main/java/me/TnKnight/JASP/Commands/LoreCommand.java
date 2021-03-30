package me.TnKnight.JASP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.JustAnotherSpawnerPickup;
import me.TnKnight.JASP.PStorage;

public class LoreCommand extends SubCommand {

	public LoreCommand(JustAnotherSpawnerPickup plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "lore";
	}

	@Override
	public String getDiscription() {
		String des = "add/set/remove/reset lore of holding spawner.";
		if (getCmds().getString(desPath) != null && !getCmds().getString(desPath).isEmpty())
			des = getCmds().getString(desPath);
		return PStorage.setColor(des);
	}

	@Override
	public String getSyntax() {
		String syntax = "/jasp lore [add/set/remove/reset] [line (if set/remove)] <lore>";
		if (getCmds().getString(synPath) != null && !getCmds().getString(synPath).isEmpty())
			syntax = getCmds().getString(synPath);
		return PStorage.setColor(syntax);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args, boolean checkPermission) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		if (args.length < 2 || !Arrays.asList("add", "set", "remove", "reset").contains(args[1])) {
			player.sendMessage(prefix + getSyntax());
			return;
		}
		int getArgs = args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove") ? 3
				: args[1].equalsIgnoreCase("reset") ? 1 : 2;
		if (args.length < getArgs) {
			player.sendMessage(prefix + getSyntax());
			return;
		}
		final boolean enabled = plugin.cfg.getConfig().getBoolean("spawner_description.enable");

		if (!player.hasPermission(getPerm + "." + args[1].toLowerCase())) {
			player.sendMessage(PStorage.noPerm());
			return;
		}
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			String msg = "You are not holding a spawner!";
			if (!plugin.cfg.getConfig().getString("require_spawner").isEmpty())
				msg = plugin.cfg.getConfig().getString("require_spawner");
			player.sendMessage(PStorage.setColor(msg));
			return;
		}
		int amount = 1;
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		ItemMeta sMeta = spawner.getItemMeta();
		List<String> sLore = new ArrayList<>();
		if (enabled && sMeta.hasLore()
				&& sMeta.getLore().size() >= plugin.cfg.getConfig().getStringList("spawner_description.lore").size())
			for (int i = 0; i < sMeta.getLore().size()
					- plugin.cfg.getConfig().getStringList("spawner_description.lore").size(); i++)
				sLore.add(sMeta.getLore().get(i));
		String str = new String();
		for (int i = getArgs; i < args.length; i++)
			str = str.concat(" " + args[i]).trim();
		switch (args[1].toLowerCase()) {

		case "add":
			sLore.add(PStorage.setColor(str));
			break;

		case "set":
		case "remove":
			int line = 1;
			if (!numberChecking(player, args[getArgs - 1]))
				return;
			line = Integer.parseInt(args[getArgs - 1]) - 1;
			if (line >= sLore.size()) {
				String msg = "<line> is over the roof. Please make it smaller!";
				if (!plugin.cfg.getConfig().getString("out_of_bound").isEmpty())
					msg = plugin.cfg.getConfig().getString("out_of_bound");
				player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<line>", args[2])));
				return;
			}
			if (args[1].equalsIgnoreCase("set")) {
				sLore.set(line, PStorage.setColor(str));
			} else
				sLore.remove(line);
			break;
		case "reset":
			if (args.length > 2 && (args[2].equalsIgnoreCase("all") || numberChecking(player, args[2])))
				amount = args[2].equalsIgnoreCase("all") ? player.getInventory().getItemInMainHand().getAmount()
						: Integer.parseInt(args[2]);
			sLore.clear();
			break;

		default:
			player.sendMessage(prefix + getSyntax());
			break;
		}
		if (enabled)
			for (String line : PStorage
					.replaceLoreFromItem(plugin.cfg.getConfig().getStringList("spawner_description.lore"), spawner))
				sLore.add(line);
		sMeta.setLore(sLore.isEmpty() ? null : sLore);
		spawner.setItemMeta(sMeta);
		spawner.setAmount(amount);
		if (player.getInventory().getItemInMainHand().getAmount() > 1) {
			player.getInventory().getItemInMainHand()
					.setAmount(player.getInventory().getItemInMainHand().getAmount() - amount);
			player.getInventory().addItem(spawner);
		} else
			player.getInventory().setItemInMainHand(spawner);
		player.updateInventory();
		return;
	}

	public static boolean numberChecking(Player player, String input) {
		JustAnotherSpawnerPickup plugin = JustAnotherSpawnerPickup.instance;
		if (!PStorage.isInt(player, input))
			return false;
		if (Integer.parseInt(input) > player.getInventory().getItemInMainHand().getAmount()) {
			String msg = "<line> is over the roof. Please make it smaller!";
			if (!plugin.cfg.getConfig().getString("out_of_bound").isEmpty())
				msg = plugin.cfg.getConfig().getString("out_of_bound");
			player.sendMessage(PStorage.setColor(PStorage.prefix + msg.replace("<input>", input)));
			return false;
		}
		return true;
	}
}
