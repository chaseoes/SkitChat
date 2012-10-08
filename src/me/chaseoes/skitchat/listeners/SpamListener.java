package me.chaseoes.skitchat.listeners;

import java.util.Date;
import me.chaseoes.skitchat.SkitChat;
import me.chaseoes.skitchat.utilities.PlayerData;
import me.chaseoes.skitchat.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpamListener implements Listener {

    private SkitChat plugin;

    public SpamListener(SkitChat instance) {
        this.plugin = instance;
    }
    // Command Listener

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ChatHandler(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Long current = Long.valueOf(new Date().getTime());
        current = Long.valueOf(current.longValue() / 1000L);
        boolean blocked = false;
        if (!player.hasPermission("skitchat.spamoverride")) {
            if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {

                if (PlayerData.getInstance().getLastMessage(player.getName()) == null) {
                    PlayerData.getInstance().setLastMessage(player.getName(), event.getMessage());
                    return;
                }

                if (current.longValue() - PlayerData.getInstance().getLastMessageTime(player.getName()) < Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay")) {
                    player.sendMessage("§cYou must wait " + Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") + " seconds between commands! (" + (Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") - (current.longValue() - PlayerData.getInstance().getLastMessageTime(player.getName()))) + " remaining)");
                    event.setCancelled(true);
                    blocked = true;
                    return;
                } else {
                    PlayerData.getInstance().setLastMessageTime(player.getName(), current.longValue());
                }

                if ((PlayerData.getInstance().getLastMessage(player.getName()).equals(event.getMessage())) && (Utilities.getInstance().plugin.getConfig().getBoolean("settings.spam.nodupecommands"))) {
                    player.sendMessage("§cYou cannot send duplicate commands!");
                    event.setCancelled(true);
                    return;
                }

                if (!blocked) {
                    PlayerData.getInstance().setLastMessage(player.getName(), event.getMessage());
                }
            }
        }
    }

    // Chat Listener
    @EventHandler(priority = EventPriority.HIGH)
    public void chatSpamCheck(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Long current = Long.valueOf(new Date().getTime());
        current = Long.valueOf(current.longValue() / 1000L);
        boolean blocked = false;
        if (!player.hasPermission("skitchat.spamoverride")) {
            if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {

                if (PlayerData.getInstance().getLastMessage(player.getName()) == null) {
                    PlayerData.getInstance().setLastMessage(player.getName(), event.getMessage());
                    return;
                }

                if (current.longValue() - PlayerData.getInstance().getLastMessageTime(player.getName()) < Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay")) {
                    player.sendMessage("§cYou must wait " + Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") + " seconds between messages! (" + (Utilities.getInstance().plugin.getConfig().getLong("settings.spam.chatdelay") - (current.longValue() - PlayerData.getInstance().getLastMessageTime(player.getName()))) + " remaining)");
                    event.setCancelled(true);
                    blocked = true;
                    return;
                } else {
                    PlayerData.getInstance().setLastMessageTime(player.getName(), current.longValue());
                }

                if ((PlayerData.getInstance().getLastMessage(player.getName()).equals(event.getMessage())) && (Utilities.getInstance().plugin.getConfig().getBoolean("settings.spam.nodupemessages"))) {
                    player.sendMessage("§cYou cannot send duplicate messages!");
                    event.setCancelled(true);
                    blocked = true;
                    return;
                }

                if (!blocked) {
                    PlayerData.getInstance().setLastMessage(player.getName(), event.getMessage());
                }
            }
        }
    }

    // Login Listener
    @EventHandler(priority = EventPriority.HIGHEST)
    public void JoinHandler(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("skitchat.spamoverride")) {
            if (Utilities.getInstance().plugin.getServer().getOnlinePlayers().length >= Utilities.getInstance().plugin.getConfig().getInt("settings.spam.min-playercount")) {
                if (PlayerData.getInstance().getLastLeave(player.getName()) == null) {
                    return;
                }
                Long current = Long.valueOf(new Date().getTime());
                current = Long.valueOf(current.longValue() / 1000L);
                if (!player.hasPermission("skitchat.spamoverride")) {
                    if (current.longValue() - PlayerData.getInstance().getLastLeave(player.getName()) < Utilities.getInstance().plugin.getConfig().getInt("settings.spam.rejointime")) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Please wait " + (Utilities.getInstance().plugin.getConfig().getInt("settings.spam.rejointime") - (current.longValue() - PlayerData.getInstance().getLastLeave(player.getName()))) + " seconds before joining!");
                    }
                }
            }
        }
        if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            event.setKickMessage(Utilities.getInstance().colorize(Utilities.getInstance().plugin.getConfig().getString("settings.spam.whitelistmessage")));
        }
    }

    // Quit Listener
    @EventHandler(priority = EventPriority.HIGHEST)
    public void JoinHandler(PlayerQuitEvent event) {
        PlayerData.getInstance().setLastLeave(event.getPlayer().getName(), new Date().getTime() / 1000L);
    }

    // !!!!!!!!!!!!!!!!!
    // BAD FOR PERFORMANCE! NOCHEATPLUS ALREADY HAS SMTH. SIMILAR!
    // !!!!!!!!!!!!!!!!!
    //    @EventHandler(priority = EventPriority.HIGHEST)
    //    public void MoveHandler(PlayerMoveEvent event) {
    //        if (!PlayerData.getInstance().hasMoved(event.getPlayer()) || PlayerData.getInstance().hasMoved(event.getPlayer()) == null) {
    //            PlayerData.getInstance().setMoved(event.getPlayer(), true);
    //        }
    //    }
}
