package me.TnKnight.JASP.Commands;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.JASP.PStorage;
import me.TnKnight.JASP.Files.GetFiles;

public class SetNameCommand extends SubCommand
{

	@Override
	public String getName() {
		return "setname";
	}

	@Override
	public String getDescription() {
		return "Sets a name for your spawner.";
	}

	@Override
	public String getSyntax() {
		return "/jasp setname <name>";
	}

	@Override
	public void onExecute( Player player, String[] args ) {
		if ( argsChecker(player, args, 2, null, 0) )
			return;
		if ( !player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER) ) {
			player.sendMessage(GetFiles.getString(GetFiles.FileName.CONFIG, "require_spawner",
					"You are not holding a spawner!", true));
			return;
		}
		String input = new String();
		for ( int i = 1 ; i < args.length ; i++ )
			input = input.concat(" " + args[i]).trim();
		if ( !player.hasPermission("jasp.namelength.bypass") ) {
			int count = super.removeColor(input).replaceAll(" ", "").toCharArray().length;
			if ( super.hasRGB() )
				for ( Pattern pattern : Arrays.asList(PStorage.HEX_PATTERN, PStorage.RGB_PATTERN) ) {
					Matcher matcher = pattern.matcher(input);
					while ( matcher.find() ) {
						count++;
						String color = input.substring(matcher.start(), matcher.end());
						input = input.replace(color, "");
						matcher = pattern.matcher(input);
					}
				}
			boolean countCode = GetFiles.getBoolean(GetFiles.FileName.CONFIG, "name_length.color_code_counter", false);
			boolean countSpace = GetFiles.getBoolean(GetFiles.FileName.CONFIG, "name_length.space_counter", true);
			for ( char c : setColor(input).toCharArray() )
				if ( (countCode && c == '§') || (countSpace && c == ' ') )
					count++;
			int max = GetFiles.getInt(GetFiles.FileName.CONFIG, "name_length.max", 15);
			int min = GetFiles.getInt(GetFiles.FileName.CONFIG, "name_length.min", 3);
			if ( count > max || count < min ) {
				String maxStr = "Your name is too long. Maximum is <max> &ccharacters.";
				String minStr = "Your name is too short. Minimum is <min> &ccharacters.";
				String msg = GetFiles.getString(GetFiles.FileName.CONFIG,
						count > max ? "name_too_long" : "name_too_short", count > max ? maxStr : minStr, true);
				msg = msg.replace("<min>", String.valueOf(min)).replace("<max>", String.valueOf(max));
				player.sendMessage(super.setColor(msg));
				return;
			}
		}
		if ( player.getInventory().getItemInMainHand().getAmount() > 1 ) {
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
