package me.chaseoes.skitchat.listeners;

import me.chaseoes.skitchat.SkitChat;
import me.chaseoes.skitchat.utilities.Configuration;
import me.chaseoes.skitchat.utilities.Formatter;
import me.chaseoes.skitchat.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private SkitChat plugin;

    public PlayerListener(SkitChat instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        final String format;
        String group = Utilities.getInstance().getGroup(player);
        String groupnode = Configuration.getInstance().plugin.getConfig().getString("groups." + group + ".join");
        String playernode = Configuration.getInstance().plugin.getConfig().getString("players." + player.getName() + ".join");
        if (!player.hasPermission("skitchat.hidejoinmessage")) {

            if (playernode != null) {
                format = Formatter.getInstance().join(playernode, event);
            } else {
                if (groupnode != null) {
                    format = Formatter.getInstance().join(groupnode, event);
                } else {
                    format = Formatter.getInstance().join(Configuration.getInstance().plugin.getConfig().getString("chat.global-join"), event);
                }
            }

            if (!format.equalsIgnoreCase("none")) {
                for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                    if (!Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(player.getName().toLowerCase())) {
                        if (!Utilities.getInstance().toggledOn(players)) {
                            players.sendMessage(format.replace("%dn", player.getDisplayName()));
                        } else {
                            if (ChatListener.getFriendsOf(players).contains(player.getName().toLowerCase())) {
                                players.sendMessage(format.replace("%dn", player.getDisplayName()));
                            }
                        }
                    }
                }
            }
        }

        String groupnameformat;
        // Tab List
        if (Configuration.getInstance().plugin.getConfig().getString("groups." + group + ".tabformat") != null) {
            groupnameformat = Utilities.getInstance().colorize(Configuration.getInstance().plugin.getConfig().getString("groups." + group + ".tabformat").replace("%n", player.getName()).replace("%dn", player.getDisplayName())).replace("%s", Utilities.getInstance().colorize(Utilities.getInstance().getSuffix(player)));
        } else {
            groupnameformat = Utilities.getInstance().colorize(Configuration.getInstance().plugin.getConfig().getString("chat.global-tabformat").replace("%n", player.getName()).replace("%dn", player.getDisplayName()).replace("%s", Utilities.getInstance().getSuffix(player)));
        }
        String name = groupnameformat;
        if (name.length() > 16) {
            name = name.substring(0, 14) + "..";
            player.setPlayerListName(name);
        } else {
            player.setPlayerListName(name);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        if (!player.hasPermission("skitchat.hideleavemessage")) {
            String group = Utilities.getInstance().getGroup(player);
            String format;
            String groupnode = Configuration.getInstance().plugin.getConfig().getString("groups." + group + ".leave");
            String playernode = Configuration.getInstance().plugin.getConfig().getString("players." + player.getName() + ".leave");

            if (playernode != null) {
                format = Formatter.getInstance().quit(playernode, event);
            } else {
                if (groupnode != null) {
                    format = Formatter.getInstance().quit(groupnode, event);
                } else {
                    format = Formatter.getInstance().quit(Configuration.getInstance().plugin.getConfig().getString("chat.global-leave"), event);
                }
            }

            if (!format.equalsIgnoreCase("none")) {
                for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                    if (!Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(player.getName().toLowerCase())) {
                        if (!Utilities.getInstance().toggledOn(players)) {
                            players.sendMessage(format.replace("%dn", player.getDisplayName()));
                        } else if (ChatListener.getFriendsOf(players).contains(player.getName().toLowerCase())) {
                            players.sendMessage(format.replace("%dn", player.getDisplayName()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.disabledeathmessages")) {
            event.setDeathMessage(null);
        }
    }

    public static void setJoin(String string, PlayerJoinEvent event) {
        event.setJoinMessage(string);
    }
}
