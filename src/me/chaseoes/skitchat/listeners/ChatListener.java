package me.chaseoes.skitchat.listeners;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import me.chaseoes.skitchat.SkitChat;
import me.chaseoes.skitchat.utilities.Configuration;
import me.chaseoes.skitchat.utilities.Formatter;
import me.chaseoes.skitchat.utilities.PlayerData;
import me.chaseoes.skitchat.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public final Logger log = Logger.getLogger("Minecraft");
    public String message;
    private SkitChat plugin;

    public ChatListener(SkitChat instance) {
        this.plugin = instance;
    }

    /*
     * I realize that canceling the chat event altogether is a very bad way of
     * doing this. Unfortunately, due to what this plugin does, it's the only
     * realistic way of doing things. As a side effect, this will break support
     * for almost every other plugin that modifies the chat. In that sense, this
     * plugin should be able to do anything plus more of any plugin that does
     * modify the chat (this is a chat plugin, after all). Canceling the event
     * also gives much greater control over it (being able to not log chat to
     * the console, etc.).
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public synchronized void onPlayerChat(AsyncPlayerChatEvent event) {

        // Tons of variables!
        event.setCancelled(true);
        Player player = event.getPlayer();
        String name = player.getName();
        String groupformat = Configuration.getInstance().plugin.getConfig().getString("groups." + Utilities.getInstance().getGroup(player) + ".chatformat");
        String playerformat = Configuration.getInstance().plugin.getConfig().getString("players." + player.getName() + ".chatformat");
        String format;
        String friendformat;
        message = event.getMessage();
        Boolean link = false;

        // Link Blocking
        if (!(Configuration.getInstance().plugin.getConfig().getInt("settings.urls.level") == 0)) {

            String[] words = message.split(" ");
            String blockeddomain = null;
            String alloweddomain = null;
            for (String domain : Utilities.getInstance().blocked) {
                for (String allowdomain : Utilities.getInstance().exceptions) {
                    blockeddomain = domain;
                    alloweddomain = allowdomain;
                    for (String word : words) {
                        if (word.contains(blockeddomain) && (!word.contains(alloweddomain))) {
                            link = true;
                            if (!event.getPlayer().hasPermission("skitchat.url")) {
                                if (Configuration.getInstance().plugin.getConfig().getInt("settings.urls.level") == 1) {
                                    message = message.replace(".", " ");
                                } else if (Configuration.getInstance().plugin.getConfig().getInt("settings.urls.level") == 2) {
                                    String blank = word.replace(word, "").trim();
                                    message = message.replace(word, blank);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.grammar.forcepunctuation") && !link) {
            if (!(message.contains(".") || message.contains("?") || message.contains("!"))) {
                if (message.contains("why") || message.contains("who") || message.contains("where") || message.contains("did") || message.contains("does") || message.contains("is") || message.contains("can") || message.contains("how")) {
                    message = message + "?";
                } else if (message.contains("wow") || message.contains("omg") || message.contains("amazing") || message.contains("unbelievable") || message.contains("yay") || message.contains("omfg")) {
                    message = message + "!";
                } else {
                    message = message + ".";
                }
            }
        }

        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.grammar.forcecapitalI") && !link) {
            if (message.startsWith("i ") || message.startsWith("I ")) {
                message = message.replace("i ", " I ").replace("i'm ", " I'm ").replace("im ", " I'm ").replace("Im ", "I'm ");
            }
            message = message.replace(" i ", " I ").replace(" i'm ", " I'm ").replace(" im ", " I'm ").replace(" Im ", " I'm ");
        }

        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.grammar.forcecapitalization") && !link) {
            message = Utilities.getInstance().capitalizeFirstLetterOfEachSentence(message);
        }

        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.spam.noextraspaces")) {
            String regex = "\\s{2,}";
            message = message.replaceAll(regex, " ");
        }

        // CAPS Blocking
        double capsCountt = 0.0D;
        double msglength = message.length();
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capsCountt += 1.0D;
            }
            if (!Character.isLetterOrDigit(c)) {
                msglength -= 1.0D;
            }
        }

        if (!event.getPlayer().hasPermission("skitchat.capsoverride")) {
            int percent = Configuration.getInstance().plugin.getConfig().getInt("settings.spam.capspercent");
            double calc = capsCountt / msglength * 100.0D;
            if (calc >= percent) {
                if (message.length() > Configuration.getInstance().plugin.getConfig().getInt("settings.caps-minchars")) {
                    if (!(message.equalsIgnoreCase(":D") || message.equalsIgnoreCase(":P") || message.equalsIgnoreCase(":(") || message.equalsIgnoreCase(":)") || message.equalsIgnoreCase("D:"))) {
                        message = message.toLowerCase();
                    }
                }
            }
        }

        // Log!
        if (Configuration.getInstance().plugin.getConfig().getBoolean("settings.logchatmessages")) {
            log.info("<" + event.getPlayer().getName() + "> " + message);
        }

        // Grab the format to use depending on the player's group.
        if (playerformat != null) {
            format = Formatter.getInstance().chat(playerformat, message, event);
            friendformat = Formatter.getInstance().friend(playerformat, message, event);
        } else if (groupformat != null) {
            format = Formatter.getInstance().chat(groupformat, message, event);
            friendformat = Formatter.getInstance().friend(groupformat, message, event);
        } else {
            format = Formatter.getInstance().chat(Configuration.getInstance().plugin.getConfig().getString("chat.globalformat"), message, event);
            friendformat = Formatter.getInstance().friend(Configuration.getInstance().plugin.getConfig().getString("chat.globalformat"), message, event);
        }

        // Private messaging!
        if (SkitChat.pming.contains(name)) {
            player.performCommand("chat pm " + PlayerData.getInstance().getPMTarget(player.getName()) + " " + message);
            return;
        }

        // Twitter style PM's!
        if (event.getMessage().startsWith("@")) {
            String target = event.getMessage().replace("@", "").substring(0, event.getMessage().indexOf(" ") - 1);
            String pmmessage = event.getMessage().replace("@", "").substring(event.getMessage().indexOf(" "));
            player.performCommand("chat pm " + target + " " + pmmessage);
            return;
        }

        // Loop through each player in the recipients list that other plugins can modify the players who recieve the message
        
        for (Player players : new HashSet<Player>(event.getRecipients())) {
            if (!player.hasPermission("skitchat.override")) {
                if (!isIgnoring(players, player)) {
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
                            if (!Utilities.getInstance().toggledOn(players)) { // If the player in question has their chat toggle OFF:
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

    public static boolean hasFriends(Player player) {
        if (Configuration.getInstance().getFriendsConfig().getStringList("friends." + player.getName()) == null) {
            return false; // Aww, no friends.
        }
        return true;
    }

    public static boolean hasFriend(Player player, Player tocheck) {
        if (!getFriendsOf(player).contains(tocheck.getName().toLowerCase())) {
            return false;
        }
        return true;
    }

    public static List<String> getFriendsOf(Player player) {
        List<String> friends = Configuration.getInstance().getFriendsConfig().getStringList("friends." + player.getName().toLowerCase());
        return friends;
    }

    public static boolean isIgnoring(Player player, Player target) {
        if (Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + player.getName().toLowerCase()).contains(target.getName().toLowerCase())) {
            return true;
        }
        return false;
    }
}
