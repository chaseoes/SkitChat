package me.chaseoes.skitchat.utilities;

import me.chaseoes.skitchat.SkitChat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Formatter {

    private SkitChat plugin;
    static Formatter instance = new Formatter();

    private Formatter() {
    }

    public static Formatter getInstance() {
        return instance;
    }

    public void setup(SkitChat p) {
        plugin = p;
    }

    public String pm(String string, String message, Player cs) {
        Player player = cs;
        String formatted = Utilities.getInstance().colorize(string.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", Utilities.getInstance().getGroup(player))).replace("%m", message);
        return formatted;
    }

    public String spy(String string, String message, Player cs, Player target) {
        Player player = cs;
        String formatted = Utilities.getInstance().colorize(string.replace("%to", target.getDisplayName()).replace("%from", player.getDisplayName())).replace("%m", message);
        return formatted;
    }

    public String me(String string, String message, Player cs) {
        Player player = cs;
        String formatted = Utilities.getInstance().colorize(string.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", Utilities.getInstance().getGroup(player))).replace("%m", message).replace("sg", "");
        return formatted;
    }

    public String chat(String format, String message, AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String group = Utilities.getInstance().getGroup(player);
        String msg;
        if (player.hasPermission("skitchat.colors")) {
            msg = Utilities.getInstance().colorize(message);
        } else {
            msg = message;
        }
        String newstring = Utilities.getInstance().colorize(format.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
        String finalstr = newstring.replace("%m", msg);
        return finalstr;
    }

    public String friend(String format, String message, AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String group = Utilities.getInstance().getGroup(player);
        String msg;
        if (player.hasPermission("skitchat.colors")) {
            msg = Utilities.getInstance().colorize(message);
        } else {
            msg = message;
        }
        String newstring = Utilities.getInstance().colorize(format.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
        String finalstr = newstring.replace("%m", Utilities.getInstance().colorize(Formatter.getInstance().plugin.getConfig().getString("chat.friendcolor")) + msg);
        return finalstr;
    }

    public String join(String string, PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String group = Utilities.getInstance().getGroup(player);
        String newstring = Utilities.getInstance().colorize(string.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%g", group));
        return newstring;
    }

    public String quit(String string, PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String group = Utilities.getInstance().getGroup(player);
        String newstring = Utilities.getInstance().colorize(string.replace("%n", player.getName()).replace("%p", Utilities.getInstance().getPrefix(player)).replace("%s", Utilities.getInstance().getSuffix(player)).replace("%dn", player.getDisplayName()).replace("%g", group));
        return newstring;
    }
}
