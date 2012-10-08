package me.chaseoes.skitchat.utilities;

import java.util.HashSet;
import java.util.Set;
import me.chaseoes.skitchat.SkitChat;
import org.bukkit.entity.Player;

public class PlayerData {

    private SkitChat plugin;
    static PlayerData instance = new PlayerData();
    public final static Set<String> moved = new HashSet<String>();

    private PlayerData() {
    }

    public static PlayerData getInstance() {
        return instance;
    }

    public void setup(SkitChat p) {
        plugin = p;
    }

    public void setPMTarget(String player, String target) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player + ".pmtarget", target);
        Configuration.getInstance().savePlayerdataConfig();
    }

    public String getPMTarget(String player) {
        return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player + ".pmtarget");
    }

    public String getLastPMFrom(String player) {
        return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player + ".lastpmfrom");
    }

    public void setLastPMFrom(String player, String target) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + target + ".lastpmfrom", player);
        Configuration.getInstance().savePlayerdataConfig();
    }

    public Boolean isSpying(String player) {
        if (Configuration.getInstance().getPlayerdataConfig().getBoolean("playerdata." + player + ".spying")) {
            return true;
        }
        return false;
    }

    public void setSpying(String player, Boolean b) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player + ".spying", b);
        Configuration.getInstance().savePlayerdataConfig();
    }
    
    public Long getLastMessageTime(String player) {
        return Configuration.getInstance().getPlayerdataConfig().getLong("playerdata." + player + ".lastmsgtime");
    }

    public void setLastMessageTime(String player, Long l) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player + ".lastmsgtime", l);
        Configuration.getInstance().savePlayerdataConfig();
    }

    public String getLastMessage(String player) {
        return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player + ".lastmsg");
    }

    public void setLastMessage(String player, String message) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player + ".lastmsg", message);
        Configuration.getInstance().savePlayerdataConfig();
    }

    public Long getLastLeave(String player) {
        return Configuration.getInstance().getPlayerdataConfig().getLong("playerdata." + player + ".lastleave");
    }

    public void setLastLeave(String player, Long l) {
        Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player + ".lastleave", l);
        Configuration.getInstance().savePlayerdataConfig();
    }
}
