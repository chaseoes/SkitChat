package me.chaseoes.skitchat.utilities;

import me.chaseoes.skitchat.SkitChat;

import org.bukkit.command.CommandSender;

public class Helper {
	  private SkitChat plugin;
	  public Helper(SkitChat instance)
	  {
	    this.plugin = instance;
	  }
	
	public void needsHelp(CommandSender cs) {
		cs.sendMessage("§cIncorrect command usage! Type §b/chat help §cfor help.");
	}
	
	public void noPermission(CommandSender cs) {
		cs.sendMessage("§cYou do not have permission to preform that action.");
	}
	
	public void pluginInfo(CommandSender cs) {
		cs.sendMessage("§b--------------- §6§l " + plugin.getDescription().getName() + " §r§6v" + plugin.getDescription().getVersion() + " §b---------------");
		cs.sendMessage("§7Plugin developed by §9chaseoes§7.");
		cs.sendMessage("§7Type §b/chat help §7for help.");
		cs.sendMessage("§7Type §b/chat help aliases §7for command aliases.");
		cs.sendMessage("§7Download Here: §8http://dev.bukkit.org/server-mods/skitchat/");
	}
}
