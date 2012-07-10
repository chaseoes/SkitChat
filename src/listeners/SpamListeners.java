package listeners;

import java.util.Date;

import me.chaseoes.skitchat.SkitChat;
import me.chaseoes.skitchat.utilities.PlayerData;
import me.chaseoes.skitchat.utilities.Utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpamListeners implements Listener {
	  @SuppressWarnings("unused")
	private SkitChat plugin;
	  public SpamListeners(SkitChat instance)
	  {
	    this.plugin = instance;
	  }
	// Command Listener
	@EventHandler(priority=EventPriority.HIGHEST)
	public void ChatHandler(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Long current = Long.valueOf(new Date().getTime());
		current = Long.valueOf(current.longValue() / 1000L);
		boolean blocked = false;
		if (!player.hasPermission("skitchat.spamoverride")) {
			if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {
				if (!PlayerData.getInstance().hasMoved(player) && (Utilities.getInstance().plugin.getConfig().getBoolean("settings.spam.forcemove"))) {
					player.sendMessage("§cPlease move before using commands!");
					event.setCancelled(true);
					return;
				}
				
				if (PlayerData.getInstance().getLastMessage(player) == null) {
					PlayerData.getInstance().setLastMessage(player, event.getMessage());
					return;
				}

				if (current.longValue() - PlayerData.getInstance().getLastMessageTime(player) < Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay")) {
					player.sendMessage("§cYou must wait " + Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") + " seconds between commands! (" + (Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") - (current.longValue() - PlayerData.getInstance().getLastMessageTime(player))) + " remaining)");
					event.setCancelled(true);
					blocked = true;
					return;
				} else {
					PlayerData.getInstance().setLastMessageTime(player, current.longValue());
				}
				
				if ((PlayerData.getInstance().getLastMessage(player).equals(event.getMessage())) && (Utilities.getInstance().plugin.getConfig().getBoolean("settings.spam.nodupecommands"))) {
					player.sendMessage("§cYou cannot send duplicate commands!");
					event.setCancelled(true);
					return;
				}
				
				if (!blocked) {
					PlayerData.getInstance().setLastMessage(player, event.getMessage());
				}
			}
		}
	}

	// Chat Listener
	@EventHandler(priority=EventPriority.HIGH)
	public void chatSpamCheck(PlayerChatEvent event) {
		Player player = event.getPlayer();
		Long current = Long.valueOf(new Date().getTime());
		current = Long.valueOf(current.longValue() / 1000L);
		boolean blocked = false;
		if (!player.hasPermission("skitchat.spamoverride")) {
			if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {
				if (!PlayerData.getInstance().hasMoved(player) && (Utilities.getInstance().plugin.getConfig().getBoolean("settings.spam.forcemove"))) {
					System.out.println(PlayerData.getInstance().hasMoved(event.getPlayer()));
					player.sendMessage("§cPlease move before chatting!");
					event.setCancelled(true);
					return;
				}
				
				if (PlayerData.getInstance().getLastMessage(player) == null) {
					PlayerData.getInstance().setLastMessage(player, event.getMessage());
					return;
				}

				if (current.longValue() - PlayerData.getInstance().getLastMessageTime(player) < Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay")) {
					player.sendMessage("§cYou must wait " + Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") + " seconds between messages! (" + (Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") - (current.longValue() - PlayerData.getInstance().getLastMessageTime(player))) + " remaining)");
					event.setCancelled(true);
					blocked = true;
					return;
				} else {
					PlayerData.getInstance().setLastMessageTime(player, current.longValue());
				}
				
				if ((PlayerData.getInstance()
						.getLastMessage(player)
						.equals(event.getMessage())) && (Utilities
								.getInstance()
								.plugin.getConfig()
								.getBoolean("settings.spam.nodupemessages"))) {
					player.sendMessage("§cYou cannot send duplicate messages!");
					event.setCancelled(true);
					blocked = true;
					return;
				}
				
				if (!blocked) {
					PlayerData.getInstance().setLastMessage(player, event.getMessage());
				}
			}
		}
	}
	
	// Login Listener
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHandler(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("skitchat.spamoverride")) {
			if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {
				if (PlayerData.getInstance().getLastLeave(player) == null) return;
				Long current = Long.valueOf(new Date().getTime());
				current = Long.valueOf(current.longValue() / 1000L);
				if (!player.hasPermission("skitchat.spamoverride")) {
					if (current.longValue() - PlayerData.getInstance().getLastLeave(player) < Utilities.getInstance().plugin.getConfig().getInt("settings.spam.rejointime")) {
						event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Please wait " + (Utilities.getInstance().plugin.getConfig().getInt("settings.spam.rejointime") - (current.longValue() - PlayerData.getInstance().getLastLeave(player))) + " seconds before joining!");
					}
				}
			}
		}
		if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
			event.setKickMessage(Utilities.getInstance().colorize(Utilities.getInstance().plugin.getConfig().getString("settings.spam.whitelistmessage")));
		}
	}
	
	// Quit Listener
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHandler(PlayerQuitEvent event) {
		PlayerData.getInstance().setLastLeave(event.getPlayer(), new Date().getTime() / 1000L);
		PlayerData.getInstance().setMoved(event.getPlayer(), false);
		System.out.println(PlayerData.getInstance().hasMoved(event.getPlayer()));
	}
	
	// Move Listener
	@EventHandler(priority=EventPriority.HIGHEST)
	public void MoveHandler(PlayerMoveEvent event) {
		if (!PlayerData.getInstance().hasMoved(event.getPlayer()) || PlayerData.getInstance().hasMoved(event.getPlayer()) == null) {
			PlayerData.getInstance().setMoved(event.getPlayer(), true);
		}
	}
	
}
