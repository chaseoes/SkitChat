package me.chaseoes.skitchat.listeners;

import me.chaseoes.skitchat.utilities.PlayerData;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
	@EventHandler(priority=EventPriority.HIGHEST)
	public void MoveHandler(PlayerMoveEvent event) {
		if (event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
		if (!PlayerData.getInstance().hasMoved(event.getPlayer()) || PlayerData.getInstance().hasMoved(event.getPlayer()) == null) {
			PlayerData.getInstance().setMoved(event.getPlayer(), true);
		}
	}
}
