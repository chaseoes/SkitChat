package me.chaseoes.skitchat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkitChat extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");
	public final static Set<String> pming = new HashSet<String>();
	public String finalfriends;
	public String finalignored;

	//TODO: Everything in the official SkitChat Google document.

	public void onEnable() {
		log.info("[" + getDescription().getName() + "] Version "
				+ getDescription().getVersion() + " by "
				+ getDescription().getAuthors() + " sucessfully enabled.");
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Could not load configuration!", ex);
		}

		// Register our Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ChatListener(this), this);
	}

	public void onDisable() {
		log.info("[" + getDescription().getName() + "] Version "
				+ getDescription().getVersion() + " by "
				+ getDescription().getAuthors() + " sucessfully disabled.");
	}

	// Commands!
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (cmnd.getName().equalsIgnoreCase("pm")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player target = getServer().getPlayer(strings[0]);
					if (!(target == null)) {
						pming.add(cs.getName());
						getConfig().set("playerdata." + cs.getName() + ".pmtarget", target.getName());
						saveConfig();
						cs.sendMessage("§aSuccessfully started a private chat with " + target.getDisplayName() + "§a!");
						cs.sendMessage("§aType /" + cmnd.getName() + " again to stop.");
					} else {
						cs.sendMessage("§cThat player isn't online!");
					}
				} else {
					if (pming.contains(cs.getName())) {
						pming.remove(cs.getName());
						getConfig().set("playerdata." + cs.getName() + ".pmtarget", null);
						saveConfig();
						cs.sendMessage("§aSuccessfully stopped the private conversation.");
					} else {
						cs.sendMessage("§cYou aren't in a conversation with anyone!");
					}
				}
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("chat")) {
			if (cs instanceof Player) {
				if (strings.length > 0) {
					if (strings[0].equalsIgnoreCase("add")) {
						if (strings.length == 2) {
							List<String> friends = getConfig().getStringList(
									"friends." + cs.getName().toLowerCase());
							if (!strings[1].toString().equalsIgnoreCase(
									cs.getName())) {
								if (!friends.contains(strings[1].toLowerCase())) {
									friends.add(strings[1].toLowerCase());
									getConfig().set("friends." + cs.getName().toLowerCase(),
											friends);
									saveConfig();
									cs.sendMessage("§aSuccessfully added "
											+ strings[1]
													+ " to your friends list!");
								} else {
									cs.sendMessage("§cThat player is already on your friends list!");
								}
							} else {
								cs.sendMessage("§cYou can't add yourself as a friend!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("remove")) {
						if (strings.length == 2) {
							List<String> friends = getConfig().getStringList(
									"friends." + cs.getName().toLowerCase());
							if (friends.contains(strings[1].toLowerCase())) {
								friends.remove(strings[1].toLowerCase());
								getConfig().set("friends." + cs.getName().toLowerCase(),
										friends);
								saveConfig();
								cs.sendMessage("§aSuccessfully removed "
										+ strings[1]
												+ " from your friends list!");
							} else {
								cs.sendMessage("§cThat player isn't on your friends list!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("list")) {
						if (strings.length == 1) {
							List<String> friends = getConfig().getStringList(
									"friends." + cs.getName().toLowerCase());
							if (friends.size() != 0) {
								StringBuilder friendlist = new StringBuilder();
								for (String friend : friends) {
									friendlist.append("§7"
											+ Bukkit.getOfflinePlayer(friend)
											.getName().toLowerCase() + "§f, §7");
								}
								finalfriends = friendlist.toString();
								cs.sendMessage("§aYour chat friends are:");
								cs.sendMessage(finalfriends);
							} else {
								cs.sendMessage("§cYou have no friends! :(");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("ignore")) {
						if (strings.length == 2) {
							List<String> ignores = getConfig().getStringList(
									"ignores." + cs.getName());
							List<String> friends = getConfig().getStringList(
									"friends." + cs.getName());
							if (friends.contains(strings[1].toLowerCase())) {
								friends.remove(strings[1].toLowerCase());
								getConfig().set("friends." + cs.getName().toLowerCase(),
										ignores);
								saveConfig();
							}
							if (!strings[1].toString().equalsIgnoreCase(
									cs.getName())) {
								if (!ignores.contains(strings[1].toLowerCase())) {
									ignores.add(strings[1].toLowerCase());
									getConfig().set("ignores." + cs.getName().toLowerCase(),
											ignores);
									saveConfig();
									cs.sendMessage("§aSuccessfully added "
											+ strings[1]
													+ " to your ignored list!");
								} else {
									cs.sendMessage("§cThat player is already on your ignored list!");
								}
							} else {
								cs.sendMessage("§cYou can't ignore yourself!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("unignore")) {
						if (strings.length == 2) {
							List<String> ignores = getConfig().getStringList(
									"ignores." + cs.getName());
							List<String> friends = getConfig().getStringList(
									"friends." + cs.getName());
							if (friends.contains(strings[1].toLowerCase())) {
								friends.remove(strings[1].toLowerCase());
								getConfig().set("friends." + cs.getName().toLowerCase(),
										ignores);
								saveConfig();
							}
							if (ignores.contains(strings[1].toLowerCase())) {
								ignores.remove(strings[1].toLowerCase());
								getConfig().set("ignores." + cs.getName().toLowerCase(),
										ignores);
								saveConfig();
								cs.sendMessage("§aSuccessfully removed "
										+ strings[1]
												+ " from your ignored list!");
							} else {
								cs.sendMessage("§cThat player isn't on your ignored list!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("ignoredlist")) {
						if (strings.length == 1) {
							List<String> ignores = getConfig().getStringList(
									"ignores." + cs.getName().toLowerCase());
							if (ignores.size() != 0) {
								StringBuilder ignoredlist = new StringBuilder();
								for (String friend : ignores) {
									ignoredlist.append("§7"
											+ Bukkit.getOfflinePlayer(friend)
											.getName().toLowerCase() + "§f, §7");
								}
								finalignored = ignoredlist.toString();
								cs.sendMessage("§aYou have these people ignored:");
								cs.sendMessage(finalignored);
							} else {
								cs.sendMessage("§cYou have nobody ignored!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("help")) {
						if (strings.length == 1) {
							cs.sendMessage("§b--------------- §6" + getDescription().getName() +" Commands Help §b---------------");
							cs.sendMessage("§5/chat add <player>§7: Add a player to your friends list.");
							cs.sendMessage("§5/chat remove <player>§7: Remove a player from your friends list.");
							cs.sendMessage("§5/chat list§7: Display a list of all your friends.");
							cs.sendMessage("§5/chat ignore <player>§7: Ignore another player.");
							cs.sendMessage("§5/chat unignore <player>§7: Unignore another player.");
							cs.sendMessage("§5/chat ignoredlist§7: List all players you're currently ignoring.");
							cs.sendMessage("§5/chat pm <player> <message>§7: Send another player a message.");
							if (cs.isOp()) {
								cs.sendMessage("§4/chat reload§7: Reload the SkitChat configuration files.");
							}
							if (cs.hasPermission("skitchat.spy")) {
								cs.sendMessage("§4/chat spy§7: Spy on all private messages sent.");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("spy")) {
						if (cs.hasPermission("skitchat.spy")) {
							if (strings.length == 1) {
								if (getConfig().getBoolean("playerdata." + cs.getName() + ".spying")) {
									getConfig().set("playerdata." + cs.getName() + ".spying", false);
									saveConfig();
									cs.sendMessage("§aYou are no longer spying on private messages.");
								} else {
									getConfig().set("playerdata." + cs.getName() + ".spying", true);
									saveConfig();
									cs.sendMessage("§aSuccessfully started spying on private messages!");
								}
							} else {
								needsHelp(cs);
							}
						} else {
							cs.sendMessage("§cSorry, you don't have permission to do that.");
						}
					}

					if (strings[0].equalsIgnoreCase("reload")) {
						if (cs.isOp()) {
							if (strings.length == 1) {
								reloadConfig();
								saveConfig();
								cs.sendMessage("§aSuccessfully reloaded the SkitChat configuration!");
							} else {
								needsHelp(cs);
							}
						} else {
							cs.sendMessage("§cSorry, you don't have permission to do that.");
						}
					}

					if (strings[0].equalsIgnoreCase("pm")) {
						Player target = getServer().getPlayer(strings[1]);
						if (strings.length > 2) {
							if (!(target == null)) { // Make sure it's a real player!
								String msg = "";
								for (int i = 2; i < strings.length - 1; i++) {
									msg += strings[i] + ' ';
								}
								msg += strings[strings.length - 1];
								cs.sendMessage(getConfig().getString("settings.pmformat").replace("%message%", msg).replace("%fromplayer%", ((Player) cs).getDisplayName()).replace("%toplayer%", target.getDisplayName()).replace("&", "§"));
								target.sendMessage(getConfig().getString("settings.pmformat").replace("%message%", msg).replace("%fromplayer%", ((Player) cs).getDisplayName()).replace("%toplayer%", target.getDisplayName()).replace("&", "§"));
								Player[] online = Bukkit.getOnlinePlayers();
								if (getConfig().getBoolean("settings.logpms")) {
									log.info("[" + getDescription().getName() +"] [PM] " + cs.getName() + " -> " + target.getName() + ": " + msg);
								}
								for (Player players : online) { // Send the PM to any players spying on PM's.
									if (players.hasPermission("skitchat.spy")) {
										if (getConfig().getBoolean("playerdata." + players.getName() + ".spying")) {
											players.sendMessage(getConfig().getString("settings.spyformat").replace("%message%", msg).replace("%fromplayer%", ((Player) cs).getDisplayName()).replace("%toplayer%", target.getDisplayName()).replace("&", "§"));
										}
									}
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

				} else {
					needsHelp(cs);
				}
			} else {
				cs.sendMessage("[" + getDescription().getName() +"] Sorry, that command can only be used in-game.");
			}
		}
		return true;
	}

	public void needsHelp(CommandSender cs) {
		cs.sendMessage("§cIncorrect command usage! Type §b/chat help §cfor help.");
	}
}
