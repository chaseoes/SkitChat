package me.chaseoes.skitchat.utilities;

import java.util.HashSet;
import java.util.Set;

import me.chaseoes.skitchat.SkitChat;

import org.bukkit.entity.Player;

public class PlayerData {
	@SuppressWarnings("unused")
	private SkitChat plugin;
	static PlayerData instance = new PlayerData();
	public final static Set<String> moved = new HashSet<String>();
    private PlayerData(){

    }
    public static PlayerData getInstance(){
        return instance;
    }

    public void setup(SkitChat p){
    	plugin = p;
   }
	public void setPMTarget(Player player, Player target) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player.getName() + ".pmtarget", target.getName());
		Configuration.getInstance().savePlayerdataConfig();
	}
	
	public String getPMTarget(Player player) {
		return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player.getName() + ".pmtarget");
	}
	
	public String getLastPMFrom(Player player) {
		return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player.getName() + ".lastpmfrom");
	}
	
	public void setLastPMFrom(Player player, Player target) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + target.getName() + ".lastpmfrom", player.getName());
		Configuration.getInstance().savePlayerdataConfig();
	}
	
	public Boolean isSpying(Player player) {
		if (Configuration.getInstance().getPlayerdataConfig().getBoolean("playerdata." + player.getName() + ".spying")) {
			return true;
		}
		return false;
	}
	
	public void setSpying(Player player, Boolean b) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player.getName() + ".spying", b);
		Configuration.getInstance().savePlayerdataConfig();
	}

	public Boolean hasMoved(Player player) {
		if (moved.contains(player.getName())) {
			return true;
		}
		return false;
	}

	public void setMoved(Player player, Boolean b) {
		if (b) {
			moved.add(player.getName());
		} else {
			moved.remove(player.getName());
		}
	}
	
	public Long getLastMessageTime(Player player) {
		return Configuration.getInstance().getPlayerdataConfig().getLong("playerdata." + player.getName() + ".lastmsgtime");
	}
	
	public void setLastMessageTime(Player player, Long l) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player.getName() + ".lastmsgtime", l);
		Configuration.getInstance().savePlayerdataConfig();
	}
	
	public String getLastMessage(Player player) {
		return Configuration.getInstance().getPlayerdataConfig().getString("playerdata." + player.getName() + ".lastmsg");
	}
	
	public void setLastMessage(Player player, String message) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player.getName() + ".lastmsg", message);
		Configuration.getInstance().savePlayerdataConfig();
	}
	
	public Long getLastLeave(Player player) {
		return Configuration.getInstance().getPlayerdataConfig().getLong("playerdata." + player.getName() + ".lastleave");
	}
	
	public void setLastLeave(Player player, Long l) {
		Configuration.getInstance().getPlayerdataConfig().set("playerdata." + player.getName() + ".lastleave", l);
		Configuration.getInstance().savePlayerdataConfig();
	}
}
