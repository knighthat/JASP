package me.TnKnight.JASP.Commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.Files.GetFiles;

public class LoreCommand extends SubCommand
{

	@Override
	public String getName() {
		return "lore";
	}

	@Override
	public String getDescription() {
		return "add/set/remove/reset lore of holding spawner.";
	}

	@Override
	public String getSyntax() {
		return "/jasp lore [add/set/remove/reset] [line (if set/remove)] <lore>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 2, new String[] { "add", "set", "remove", "reset" }, 1) )
			return;
		int getArgs = args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove") ? 3
				: args[1].equalsIgnoreCase("reset") ? 1 : 2;
		if ( args.length < getArgs ) {
			player.spigot().sendMessage(getUsage().create());
			return;
		}
		final boolean enabled = GetFiles.getBoolean(GetFiles.FileName.CONFIG, "spawner_description.enable", false);
		if ( !player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER) ) {
			player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "require_spawner",
					"You are not holding a spawner!", true));
			return;
		}
		int amount = 1;
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		ItemMeta sMeta = spawner.getItemMeta();
		List<String> sLore = hasStatistic(sMeta.getLore());
		String str = new String();
		for ( int i = getArgs ; i < args.length ; i++ )
			str = str.concat(" " + args[i]).trim();
		switch ( args[1].toLowerCase() ) {

			case "add" :
				sLore.add(super.setColor(str));
			break;

			case "set" :
			case "remove" :
				int line = 1;
				if ( !numberChecking(player, args[getArgs - 1]) )
					return;
				line = Integer.parseInt(args[getArgs - 1]) - 1;
				if ( line >= sLore.size() ) {
					String msg = GetFiles.getString(GetFiles.FileName.CONFIG, "out_of_bound",
							"<line> is over the roof. Please make it smaller!", true);
					player.sendMessage(msg.replace("<line>", args[2]));
					return;
				}
				if ( args[1].equalsIgnoreCase("set") ) {
					sLore.set(line, super.setColor(str));
				} else
					sLore.remove(line);
			break;
			case "reset" :
				if ( args.length > 2 && (args[2].equalsIgnoreCase("all") || numberChecking(player, args[2])) )
					amount = args[2].equalsIgnoreCase("all") ? player.getInventory().getItemInMainHand().getAmount()
							: Integer.parseInt(args[2]);
				sLore.clear();
			break;

			default :
				player.spigot().sendMessage(getUsage().create());
			break;
		}
		sMeta.setLore(sLore.isEmpty() ? null : sLore);
		spawner.setItemMeta(sMeta);
		if ( enabled )
			super.replaceLoreFromItem(spawner);
		spawner.setAmount(amount);
		if ( player.getInventory().getItemInMainHand().getAmount() > 1 ) {
			player.getInventory().getItemInMainHand()
					.setAmount(player.getInventory().getItemInMainHand().getAmount() - amount);
			player.getInventory().addItem(spawner);
		} else
			player.getInventory().setItemInMainHand(spawner);
		player.updateInventory();
		return;
	}

	private boolean numberChecking( Player player, String input ) {
		if ( !super.isInt(player, input) )
			return false;
		if ( Integer.parseInt(input) > player.getInventory().getItemInMainHand().getAmount() ) {
			String msg = GetFiles.getString(GetFiles.FileName.CONFIG, "out_of_bound",
					"<line> is over the roof. Please make it smaller!", true);
			player.sendMessage(msg.replace("<line>", input));
			return false;
		}
		return true;
	}
}
