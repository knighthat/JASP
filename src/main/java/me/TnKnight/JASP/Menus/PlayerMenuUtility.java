package me.TnKnight.JASP.Menus;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerMenuUtility
{

	private Player player;
	private Block spawner;

	public PlayerMenuUtility(Player player) {
		this.player = player;
	}

	public Block getSpawner() {
		return spawner;
	}

	public void setSpawner( Block spawner ) {
		this.spawner = spawner;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer( Player player ) {
		this.player = player;
	}

}
