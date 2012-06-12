package me.chaseoes.skitchat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {

	public final JavaPlugin plugin;
	public final Logger log = Logger.getLogger("Minecraft");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static HashMap<String, PlayerDataHolder> data = new HashMap();

	public ChatListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if (plugin.getServer().getOnlinePlayers().length > plugin.getConfig().getInt("settings.min-playercount")) {
			try {
				// SPAM checking!
				PlayerDataHolder pdata = (PlayerDataHolder) data.get(event.getPlayer().getName());
				if ((!event.getPlayer().hasPermission("skitchat.spamoverride"))) {
					if ((!((PlayerDataHolder) data.get(event.getPlayer().getName())).moved) && (plugin.getConfig().getBoolean("settings.forcemove"))) {
						event.getPlayer().sendMessage("§cPlease move before chatting!");
						event.setCancelled(true);
						return;
					}

					Long current = Long.valueOf(new Date().getTime());
					current = Long.valueOf(current.longValue() / 1000L);

					if (plugin.getServer().getOnlinePlayers().length > plugin.getConfig().getInt("settings.min-playercount")) {
						boolean blocked = false;
						if (current.longValue() - pdata.lastMessageTime < plugin.getConfig().getLong("settings.chatdelay")) {
							event.getPlayer().sendMessage("§cYou must wait " + plugin.getConfig().getLong("settings.chatdelay") + " seconds between messages! (" + (plugin.getConfig().getLong("settings.chatdelay") - (current.longValue() - pdata.lastMessageTime)) + " remaining)");
							event.setCancelled(true);
							blocked = true;
						}
						else {
							pdata.lastMessageTime = current.longValue();
						}
						if ((pdata.lastMessage.equals(event.getMessage())) && (plugin.getConfig().getBoolean("settings.nodupemessages"))) {
							event.getPlayer().sendMessage("§cYou cannot send duplicate messages!");
							event.setCancelled(true);
						}
						if (!blocked)
							pdata.lastMessage = event.getMessage(); 
					}
				}
			} catch (NullPointerException ex) { 
			}
		}

		if (!event.isCancelled()) {
			// Log first!
			if (plugin.getConfig().getBoolean("settings.logchatmessages")) {
				log.info("[" + plugin.getDescription().getName() +"] [CHAT] " + event.getPlayer().getName() + ": " + event.getMessage());
			}


			// Tons of variables!
			event.setCancelled(true);
			Player player = event.getPlayer();
			String name = player.getName();
			Player[] online = Bukkit.getOnlinePlayers();
			String groupformat = plugin.getConfig().getString("groups." + SkitChat.permission.getPrimaryGroup(player) + ".chatformat");
			String format;
			String friendformat;

			// Grab the format to use depending on the player's group.
			if (!(groupformat == null)) {
				format = formatter(groupformat, event);
				friendformat = Friendformatter(format, event);
			} else {
				format = formatter(plugin.getConfig().getString("chat.globalformat"), event);
				friendformat = Friendformatter(plugin.getConfig().getString("chat.globalformat"), event);
			}

			// Private messaging!
			if (SkitChat.pming.contains(name)) {
				player.performCommand("chat pm " + ((SkitChat) plugin).getPlayerdataConfig().getString("playerdata." + name + ".pmtarget") + " " + event.getMessage());
				return;
			}

			// Twitter style PM's!
			if (event.getMessage().startsWith("@")) {
				String target = event.getMessage().replace("@", "").substring(0, event.getMessage().indexOf(" ") - 1);
				String message = event.getMessage().replace("@", "").substring(event.getMessage().indexOf(" "));
				player.performCommand("chat pm " + target + " " + message);
				return;
			}

			// Loop through each online player.
			for (Player players : online) {
				if (!player.hasPermission("skitchat.override")) {
					if (!((SkitChat) plugin).getIgnoresConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(player.getName().toLowerCase())) { // Check to see if the online player is ignoring the sender..
						if (getFriendsOf(players).contains(name.toLowerCase())) { // If the online player has the sender as a friend..
							if (players.getName().equalsIgnoreCase(name)) {
								player.sendMessage(format);
							} else {
								players.sendMessage(friendformat);
							}
						} else { // If the online player does not have the sender as a friend..
							if (players.getName().equalsIgnoreCase(name)) { // If the player in question is the sender:
								players.sendMessage(format); // Send them the message.
							} else { // If the player in question isn't the sender:
								if (!toggledOn(players)) { // If the player in question has their chat toggle OFF:
									players.sendMessage(format); // Send them the message.
								}
							}
						}
					}
				} else {
					players.sendMessage(format);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		final String format;
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}

		String groupnode = plugin.getConfig().getString("groups." + group + ".join");
		String playernode = plugin.getConfig().getString("players." + player.getName() + ".join");
		if (playernode == null) {
			if (groupnode != null) {
				format = Joinformatter(groupnode, event);
			} else {
				format = Joinformatter(plugin.getConfig().getString("chat.global-join"), event);
			}
		} else {
			format = Joinformatter(plugin.getConfig().getString("chat.global-join"), event);
		}

		if (!format.equalsIgnoreCase("none")) {
			event.setJoinMessage(format);
		} else {
			event.setJoinMessage(null);
		}
		
		String groupnameformat;
		// Tab List
		if (plugin.getConfig().getString("groups." + group + ".tabcolor") != null) {
			groupnameformat = colorize(plugin.getConfig().getString("groups." + group + ".tabcolor").replace("%n", player.getName()).replace("%dn", player.getDisplayName())).replace("%s", getSuffix(player));
		} else {
			groupnameformat = colorize(plugin.getConfig().getString("chat.global-tabcolor").replace("%n", player.getName()).replace("%dn", player.getDisplayName()).replace("%s", getSuffix(player)));
		}
		String name = groupnameformat;
		if (name.length() > 16) {
			name = name.substring(0, 12) + "..";
			player.setPlayerListName(name);
		} else {
			player.setPlayerListName(name);
		}
	}
	
	public void setJoin(String string, PlayerJoinEvent event) {
		event.setJoinMessage(string);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String format;
		String groupnode = plugin.getConfig().getString("groups." + group + ".leave");
		String playernode = plugin.getConfig().getString("players." + player.getName() + ".leave");
		if (!(groupnode == null)) {
			if (playernode == null) {
				format = Quitformatter(groupnode, event);
			} else {
				format = Quitformatter(playernode, event);
			}
		} else {
			format = Quitformatter(plugin.getConfig().getString("chat.global-leave"), event);
		}

		if (!format.equalsIgnoreCase("none")) {
			event.setQuitMessage(format);
		} else {
			event.setQuitMessage(null);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinEvent(PlayerJoinEvent e)
	{
		data.put(e.getPlayer().getName(), new PlayerDataHolder());
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void ChatHandler(PlayerCommandPreprocessEvent e) {
		if (plugin.getServer().getOnlinePlayers().length > plugin.getConfig().getInt("settings.min-playercount")) {
			try {
				PlayerDataHolder pdata = (PlayerDataHolder)data.get(e.getPlayer().getName());
				if ((!e.getPlayer().hasPermission("skitchat.spamoverride"))) {
					if ((!((PlayerDataHolder)data.get(e.getPlayer().getName())).moved) && (plugin.getConfig().getBoolean("settings.forcemove"))) {
						e.getPlayer().sendMessage("§cPlease move before chatting!");
						e.setCancelled(true);
						return;
					}
					Long current = Long.valueOf(new Date().getTime());
					current = Long.valueOf(current.longValue() / 1000L);
					boolean blocked = false;
					if (current.longValue() - pdata.lastMessageTime < plugin.getConfig().getLong("settings.chatdelay")) {
						e.getPlayer().sendMessage("§cYou must wait " + plugin.getConfig().getLong("settings.chatdelay") + " seconds between messages! (" + (plugin.getConfig().getLong("settings.chatdelay") - (current.longValue() - pdata.lastMessageTime)) + " remaining)");
						e.setCancelled(true);
						blocked = true;
					}
					else {
						pdata.lastMessageTime = current.longValue();
					}
					if ((pdata.lastMessage.equals(e.getMessage())) && (plugin.getConfig().getBoolean("settings.nodupemessages"))) {
						e.getPlayer().sendMessage("§cYou cannot send duplicate messages!");
						e.setCancelled(true);
					}
					if (!blocked)
						pdata.lastMessage = e.getMessage();
				}
			} catch (NullPointerException ex) { 
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHandler(PlayerLoginEvent e) {
		if (plugin.getServer().getOnlinePlayers().length > plugin.getConfig().getInt("settings.min-playercount")) {
			try {
				Player player = e.getPlayer();
				PlayerDataHolder pdata = (PlayerDataHolder)data.get(player.getName());
				if (pdata == null) return;
				Long current = Long.valueOf(new Date().getTime());
				current = Long.valueOf(current.longValue() / 1000L);
				if (!player.hasPermission("skitchat.spamoverride")) {
					if (current.longValue() - pdata.lastLeave < plugin.getConfig().getInt("settings.rejointime")) {
						e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Please wait " + (plugin.getConfig().getInt("settings.rejointime") - (current.longValue() - pdata.lastLeave)) + " seconds before joining!");
					}
				}
				data.put(player.getName(), pdata);
			} catch (NullPointerException ex) { 
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHandler(PlayerQuitEvent e) {
		try {
			PlayerDataHolder pdata = (PlayerDataHolder)data.get(e.getPlayer().getName());
			pdata.lastLeave = (new Date().getTime() / 1000L);
			data.put(e.getPlayer().getName(), pdata);
		} catch (NullPointerException ex) { 
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void MoveHandler(PlayerMoveEvent e) {
		try {
			((PlayerDataHolder)data.get(e.getPlayer().getName())).moved = true;
		} catch (NullPointerException ex) { 
		}
	}

	class PlayerDataHolder {
		boolean moved = false;
		public long lastMessageTime = 0L;
		public String lastMessage = "";
		public long lastLeave = 0L;

		PlayerDataHolder() {
		}
	}

	public boolean hasFriends(Player player) {
		if (((SkitChat) plugin).getFriendsConfig().getStringList("friends." + player.getName()) == null) {
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
		List<String> friends = ((SkitChat) plugin).getFriendsConfig().getStringList(
				"friends." + player.getName().toLowerCase());
		return friends;
	}

	public boolean isIgnoring(Player player, Player target) {
		if (((SkitChat) plugin).getIgnoresConfig().getStringList("ignores." + player.getName().toLowerCase())
				.contains(target)) {
			return true;
		}
		return false;
	}

	public boolean toggledOn(Player player) {
		if (SkitChat.toggled.contains(player.getName())) {
			return true;
		}
		return false;
	}

	// Colors!
	public String colorize(String s){
		if(s == null) return null;
		return s.replaceAll("&([l-o0-9a-f])", "\u00A7$1");
	}

	// Format configuration stuffs.
	public String formatter(String string, PlayerChatEvent event) {
		Player player = event.getPlayer();
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String message;
		if (player.hasPermission("skitchat.colors")) {
			message = colorize(event.getMessage());
		} else {
			message = event.getMessage();
		}
		String newstring = colorize(string.replace("%n", player.getName()).replace("%p", getPrefix(player)).replace("%s", getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
		String finalstr = newstring.replace("%m", message);
		return finalstr;
	}

	public String Friendformatter(String string, PlayerChatEvent event) {
		Player player = event.getPlayer();
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String message;
		if (player.hasPermission("skitchat.colors")) {
			message = colorize(event.getMessage());
		} else {
			message = event.getMessage();
		}
		String newstring = colorize(string.replace("%n", player.getName()).replace("%p", getPrefix(player)).replace("%s", getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
		String finalstr = newstring.replace("%m", colorize(plugin.getConfig().getString("chat.friendcolor")) + message);
		return finalstr;
	}

	public String Joinformatter(String string, PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String newstring = colorize(string.replace("%n", player.getName()).replace("%p", getPrefix(player)).replace("%s", getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
		return newstring;
	}

	public String Quitformatter(String string, PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String newstring = colorize(string.replace("%n", player.getName()).replace("%p", getPrefix(player)).replace("%s", getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
		return newstring;
	}

	// Vault Support
	public static Chat chat = null;
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	// Prefixes and Suffixes
	public String getPrefix(Player player) {
		if (setupChat()) {
			String group;
			if (SkitChat.permission.getPrimaryGroup(player) != null) {
				group = SkitChat.permission.getPrimaryGroup(player);
			} else {
				group = "default";
			}
			String prefix = SkitChat.chat.getGroupPrefix(player.getWorld(), group);
			return prefix;
		}
		if (SkitChat.permission.getPrimaryGroup(player) != null && plugin.getConfig().getString("groups." + SkitChat.permission.getPrimaryGroup(player) + ".prefix") != null) {
			String group = SkitChat.permission.getPrimaryGroup(player).toLowerCase();
			String prefix = colorize(plugin.getConfig().getString("groups." + group + ".prefix"));
			return prefix;
		} else {
			return plugin.getConfig().getString("chat.defaultprefix");
		}
	}

	public String getSuffix(Player player) {
		if (setupChat()) {
			String group = SkitChat.chat.getPrimaryGroup(player);
			String prefix = SkitChat.chat.getGroupSuffix(player.getWorld(), group);
			return prefix;
		}
		if (SkitChat.permission.getPrimaryGroup(player) != null && plugin.getConfig().getString("groups." + SkitChat.permission.getPrimaryGroup(player) + ".suffix") != null) {
			String group = SkitChat.permission.getPrimaryGroup(player).toLowerCase();
			String prefix = colorize(plugin.getConfig().getString("groups." + group + ".suffix"));
			return prefix;
		} else {
			return plugin.getConfig().getString("chat.defaultsuffix");
		}
	}

}
