package me.chaseoes.skitchat.utilities;

import java.util.ArrayList;
import java.util.List;

import me.chaseoes.skitchat.SkitChat;

import org.bukkit.entity.Player;

public class Utilities {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<String> blocked = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> exceptions = new ArrayList();
	public SkitChat plugin;
	static Utilities instance = new Utilities();
    private Utilities(){

    }
    public static Utilities getInstance(){
        return instance;
    }

    public void setup(SkitChat p){
    	plugin = p;
   }
	
	// Taste the rainbow.
	public String colorize(String s) {
		if (s == null)
			return null;
		return s.replaceAll("&([l-ok0-8k9a-f])", "\u00A7$1");
	}
	
	// Get the primary group.
	public String getGroup(Player player) {
		if (plugin
				.setupPermissions() && 
				(Utilities.getInstance().plugin.permission.getPrimaryGroup(player) != null)) {
			return Utilities.getInstance().plugin.permission.getPrimaryGroup(player);
		} else {
			return Utilities.getInstance().plugin.getConfig().getString("info.defaultgroup");
		}
	}
	
	// Prefixes
	public String getPrefix(Player player) {
		String group = getGroup(player);
		if (Utilities.getInstance().plugin.setupChat() &&
				!Utilities.getInstance().plugin.getConfig().getBoolean("info.enable")) {
			if (Utilities.getInstance().plugin.chat.getPlayerPrefix(player) != null) {
				return Utilities.getInstance().plugin.chat.getGroupPrefix(player.getWorld(), group);
			} else {
				return Utilities.getInstance().plugin.chat.getPlayerPrefix(player.getWorld(), group);
			}
		}
		
		String playerprefix = Utilities.getInstance().plugin.getConfig().getString("players." + player.getName() + ".prefix");
		String groupprefix = Utilities.getInstance().plugin.getConfig().getString("groups." + group + ".prefix");
		if (groupprefix != null || playerprefix != null && Utilities.getInstance().plugin.getConfig().getBoolean("info.enable")) {
			if (playerprefix == null) {
				return colorize(groupprefix);
			} else {
				return colorize(playerprefix);
			}
		} else {
			return colorize(Utilities.getInstance().plugin.getConfig().getString("info.defaultprefix"));
		}
	}

	// Suffixes
	public String getSuffix(Player player) {
		String group = getGroup(player);
		String playersuffix = Utilities.getInstance().plugin.getConfig().getString("players." + player.getName() + ".suffix");
		String groupsuffix = Utilities.getInstance().plugin.getConfig().getString("groups." + group + ".suffix");
		
		if (Utilities.getInstance().plugin.setupChat() && !Utilities.getInstance().plugin.getConfig().getBoolean("info.enable")) {
			if (Utilities.getInstance().plugin.chat.getPlayerSuffix(player) != null) {
				return Utilities.getInstance().plugin.chat.getGroupSuffix(player.getWorld(), group);
			} else {
				return Utilities.getInstance().plugin.chat.getPlayerSuffix(player.getWorld(), group);
			}
		}
		
		if (groupsuffix != null || playersuffix != null && Utilities.getInstance().plugin.getConfig().getBoolean("info.enable")) {
			if (playersuffix == null) {
				return colorize(groupsuffix);
			} else {
				return colorize(playersuffix);
			}
		} else {
			return colorize(Utilities.getInstance().plugin.getConfig().getString("info.defaultprefix"));
		}
	}
	
	public boolean toggledOn(Player player) {
		if (SkitChat.toggled.contains(player.getName())) {
			return true;
		}
		return false;
	}
	
	// Grammar is good!
	public String capitalizeFirstLetterOfEachSentence(String str){
	    char[] arr = str.toCharArray();
	    boolean cap = true;
	    boolean space_found = true;
	    for (int i = 0; i<arr.length; i++){
	        if (cap) {
	            // white space includes \n, space
	            if (Character.isWhitespace(arr[i])) 
	                space_found = true;
	            else {
	                if (space_found && !Character.isUpperCase(arr[i]))
	                    arr[i] = Character.toUpperCase(arr[i]);
	 
	                cap = false;
	                space_found = false;
	            }
	        } else {
	            if (arr[i] == '.' || arr[i] == '?' || arr[i] == '!') {
	                cap = true;
	            }
	        }
	    }
	    return new String(arr);
	}
	
//	public boolean needsUpdate() {
//		String pageurl = UpdateChecker.fetch("http://emeraldsmc.com/skitchat/");
//		if (pageurl.equalsIgnoreCase(Utilities.getInstance().plugin.getDescription().getVersion())) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	public void blockDomains() {
		blocked.add(".com");
		blocked.add(".org");
		blocked.add(".net");
		blocked.add(".us");
		blocked.add(".ca");
		blocked.add(".biz");
		blocked.add(".info");
		blocked.add(".xxx");
	}
}
