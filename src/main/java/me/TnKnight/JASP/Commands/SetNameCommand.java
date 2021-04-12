package me.TnKnight.JASP.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetNameCommand extends SubCommand {

	@Override
	public String getName() {
		return "setname";
	}

	@Override
	public String getDescription() {
		return "Name your spawner.";
	}

	@Override
	public String getSyntax() {
		return "/jasp setname <name>";
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if (args.length < 2) {
			player.spigot().sendMessage(getUsage().create());
			return;
		}
		String input = new String();
		for (int i = 1; i < args.length; i++)
			input = input.concat(" " + args[i]).trim();
		int max = plugin.cfg.getConfig().isInt("name_length.max") ? plugin.cfg.getConfig().getInt("name_length.max")
				: 15;
		int min = plugin.cfg.getConfig().isInt("name_length.min") ? plugin.cfg.getConfig().getInt("name_length.min")
				: 3;
		boolean space = plugin.cfg.getConfig().isBoolean("name_length.space_counter")
				? plugin.cfg.getConfig().getBoolean("name_length.space_counter")
				: true;
		boolean color = plugin.cfg.getConfig().isBoolean("name_length.color_code_counter")
				? plugin.cfg.getConfig().getBoolean("name_length.color_code_counter")
				: false;
		int count = 0;
		if (color)
			for (char c : super.setColor(input).toCharArray())
				if (String.valueOf(c).equalsIgnoreCase("§"))
					count++;
		for (char c : super.removeColor(input).toCharArray()) {
			count++;
			if (!space && String.valueOf(c).equalsIgnoreCase(" "))
				count--;
		}
		if (count > max || count < min) {
			String maxStr = "Your name is too long. Maximum is <max> &ccharacters.";
			String minStr = "Your name is too short. Minimum is <min> &ccharacters.";
			String msg = super.getStringFromConfig(count > max ? "name_too_long" : "name_too_short",
					count > max ? maxStr : minStr, true);
			player.sendMessage(
					super.setColor(msg.replace("<min>", String.valueOf(min).replace("<max>", String.valueOf(max)))));
		}
		if (player.getInventory().getItemInMainHand().getAmount() > 1) {
			ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
			player.getInventory().getItemInMainHand().setAmount(spawner.getAmount() - 1);
			spawner.setAmount(1);
			ItemMeta sMeta = spawner.getItemMeta();
			sMeta.setDisplayName(super.setColor(input));
			spawner.setItemMeta(sMeta);
			player.getInventory().addItem(spawner);
		} else {
			ItemMeta sMeta = player.getInventory().getItemInMainHand().getItemMeta();
			sMeta.setDisplayName(super.setColor(input));
			player.getInventory().getItemInMainHand().setItemMeta(sMeta);
		}
		player.updateInventory();
		return;
	}

}
