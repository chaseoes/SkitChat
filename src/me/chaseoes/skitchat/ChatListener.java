package me.chaseoes.skitchat;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {

	public final JavaPlugin plugin;
	public final Logger log = Logger.getLogger("Minecraft");

	public ChatListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		// Log first!
		if (plugin.getConfig().getBoolean("settings.logchatmessages")) {
			log.info("[" + plugin.getDescription().getName() +"] [CHAT] " + event.getPlayer().getName() + ": " + event.getMessage());
		}

		// Tons of variables!
		Player player = event.getPlayer();
		String name = player.getName();
		Player[] online = Bukkit.getOnlinePlayers();
		event.setCancelled(true);
		String essentialsformat = event.getFormat()
				.replace("%1$s", player.getDisplayName())
				.replace("%2$s", event.getMessage());
		String friendformat = plugin.getConfig()
				.getString("settings.friendformat")
				.replace("%playername%", player.getDisplayName())
				.replace("%message%", event.getMessage()).replace("&", "§");


		// Private messaging!
		if (SkitChat.pming.contains(name)) {
			player.performCommand("chat pm " + plugin.getConfig().getString("playerdata." + name + ".pmtarget") + " " + event.getMessage());
			return;
		}

		// Twitter style PM's!
		if (event.getMessage().startsWith("@")) {
			String target = event.getMessage().replace("@", "").substring(0, event.getMessage().indexOf(" ") - 1);
			String message = event.getMessage().replace("@", "").substring(event.getMessage().indexOf(" "));
			player.performCommand("chat pm " + target + " " + message);
			System.out.println(target);
			System.out.println(message);
			return;
		}

		// Loop through each online player.
		for (Player players : online) {
			if (!plugin.getConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(player.getName().toLowerCase())) { // Check to see if the online player is ignoring the sender..
				if (getFriendsOf(players).contains(name.toLowerCase())) { // If the online player has the sender as a friend..
					players.sendMessage(friendformat);
				} else { // If the online player does not have the sender as a friend..
					players.sendMessage(essentialsformat);
				}
			}
		}

	}

	public boolean hasFriends(Player player) {
		if (plugin.getConfig().getStringList("friends." + player.getName()) == null) {
			return false; // Aww, no friends.
		}
		return true;
	}

	public boolean hasFriend(Player player, Player tocheck) {
		if (!getFriendsOf(player).contains(tocheck.getName().toLowerCase())) {
			return false;
		}
		return true;
	}

	public List<String> getFriendsOf(Player player) {
		List<String> friends = plugin.getConfig().getStringList(
				"friends." + player.getName().toLowerCase());
		return friends;
	}

	public boolean isIgnoring(Player player, Player target) {
		if (plugin.getConfig().getStringList("ignores." + player.getName().toLowerCase())
				.contains(target)) {
			return true;
		}
		return false;
	}

}
