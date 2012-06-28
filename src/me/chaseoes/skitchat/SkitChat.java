package me.chaseoes.skitchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SkitChat extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");
	public final static Set<String> pming = new HashSet<String>();
	public final static Set<String> toggled = new HashSet<String>();
	public final static Set<String> pmtoggled = new HashSet<String>();
	public static Permission permission = null;
	public String finalfriends;
	public String finalignored;
	private FileConfiguration friendsConfig = null;
	private File friendsConfigFile = null;
	private FileConfiguration ignoresConfig = null;
	private File ignoresConfigFile = null;
	private FileConfiguration playerdataConfig = null;
	private File playerdataConfigFile = null;
	private FileConfiguration channelsConfig = null;
	private File channelsConfigFile = null;
	public static Chat chat = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<String> blocked = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static List<String> exceptions = new ArrayList();
	String msg = "";
	String pmmsg = "";
	String value = "";
	String memessage = "";

	public void onEnable() {
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "[" + getDescription().getName() + "] Could not load configuration!", ex);
		}

		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		setupChat();
		setupPermissions();
		blockDomains();
		exceptions = getConfig().getStringList("settings.urls.exceptions");
		log.info("[" + getDescription().getName() + "] Version " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " sucessfully enabled.");
	}

	public void onDisable() {
		reloadConfig();
		saveFriendsConfig();
		saveIgnoresConfig();
		savePlayerdataConfig();
		saveConfig();
		log.info("[" + getDescription().getName() + "] Version " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " sucessfully disabled.");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string,
			String[] strings) {
		if (cmnd.getName().equalsIgnoreCase("pm")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player target = getServer().getPlayer(strings[0]);
					if (!(target == null)) {
						if (!target.hasPermission("skitchat.nopm")) {
							if (!pmtoggled.contains(target.getName())) {
								pming.add(cs.getName());
								getPlayerdataConfig().set("playerdata." + cs.getName() + ".pmtarget", target.getName());
								savePlayerdataConfig();
								cs.sendMessage("§aSuccessfully started a private chat with " + target.getDisplayName() + "§a!");
								cs.sendMessage("§aType /" + cmnd.getName() + " again to stop.");
							} else {
								cs.sendMessage("§cYou are not allowed to PM " + target.getDisplayName() + "§c.");
							}
						} else {
							cs.sendMessage("§cYou are not allowed to start a chat session with " + target.getDisplayName() + "§c.");
						}
					} else {
						cs.sendMessage("§cThat player isn't online!");
					}
				} else {
					if (pming.contains(cs.getName())) {
						pming.remove(cs.getName());
						getPlayerdataConfig().set("playerdata." + cs.getName() + ".pmtarget", null);
						savePlayerdataConfig();
						cs.sendMessage("§aSuccessfully stopped the private conversation.");
					} else {
						cs.sendMessage("§cYou aren't in a conversation with anyone!");
						cs.sendMessage("§cTo join a conversation: /" + cmnd.getName() + " <player>");
					}
				}
			} else {
				cs.sendMessage("§cUsage: /" + cmnd.getName() + " <player>");
				needsHelp(cs);
			}
		}
		
//		if (cmnd.getName().equalsIgnoreCase("list")) {
//			if ((strings.length == 0)) {
//				Player p = (Player) cs;
//				if (p.hasPermission("skitchat.list")) {
//					List<String> format = getConfig().getStringList("settings.onlinelist");
//					for (String line : format) {
//						String newline = null;
//						if (line.contains("%groupname") || line.contains("%groupmember")) {
//							for (String group : permission.getGroups()) {
//								StringBuilder playersingroup = new StringBuilder();
//								if (!getPlayersInGroup(group).contains("none")) {
//									for (String pingroup : getPlayersInGroup(group)) {
//										playersingroup.append(pingroup + "§f, ");
//									}
//									cs.sendMessage(colorize(line.replace("%groupname", group).replace("%groupmember", playersingroup.toString())) + "\n");
//								}
//							}
//						} else {
//							cs.sendMessage(colorize(line));
//						}
//					}
//				} else {
//					cs.sendMessage("§cSorry, you don't have permission for that.");
//				}
//			} else {
//				needsHelp(cs);
//			}
//		}
		
		if (cmnd.getName().equalsIgnoreCase("chatlist")) {
			if (strings.length == 0) {
				Map<String, List<Player>> sort = new HashMap<String, List<Player>>();
				for (Player OnlinePlayer : getServer().getOnlinePlayers()) {
					Player player = OnlinePlayer;
					final String group = permission.getPrimaryGroup(player);
					List<Player> list = sort.get(group);
					if (list == null)
					{
						list = new ArrayList<Player>();
						sort.put(group, list);
					}
					list.add(player);
				}
				
				final String[] groups = sort.keySet().toArray(new String[0]);
				Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
				for (String group : groups) {
					final StringBuilder groupString = new StringBuilder();
					groupString.append("§8" + group + ": §7");
					final List<Player> users = sort.get(group);
					boolean first = true;
					for (Player user : users)
					{
						if (!first)
						{
							groupString.append(", ");
						}
						else
						{
							first = false;
						}
						groupString.append(user.getDisplayName());
						groupString.append("§f");
					}
					cs.sendMessage(groupString.toString());
				}
			}
		}

		if (cmnd.getName().equalsIgnoreCase("friend")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player player = Bukkit.getPlayer(cs.getName());
					player.performCommand("chat add " + strings[0]);
				} else {
					needsHelp(cs);
				}
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("unfriend")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player player = Bukkit.getPlayer(cs.getName());
					player.performCommand("chat remove " + strings[0]);
				} else {
					needsHelp(cs);
				}
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("ignore")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player player = Bukkit.getPlayer(cs.getName());
					player.performCommand("chat ignore " + strings[0]);
				} else {
					needsHelp(cs);
				}
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("reply")) {
			StringBuilder sb = new StringBuilder();
			for (String arg : strings)
				sb.append(arg + " ");
			Player player = Bukkit.getPlayer(cs.getName());
			String target = getPlayerdataConfig().getString(
					"playerdata." + cs.getName() + ".lastpmfrom");
			player.performCommand("chat pm " + target + " " + sb.toString());
		}

		if (cmnd.getName().equalsIgnoreCase("me")) {
			if ((strings.length >= 1)) {
				Player p = (Player) cs;
				if (p.hasPermission("skitchat.me")) {
					StringBuilder sb = new StringBuilder();
					for (String arg : strings)
						sb.append(arg + " ");
					memessage = sb.toString();
					for (Player players : Bukkit.getServer().getOnlinePlayers()) {
						if (!(getIgnoresConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(cs.getName().toLowerCase()))) {
							players.sendMessage(meFormatter(getConfig().getString("chat.meformat"), p));
						}
					}
				} else {
					cs.sendMessage("§cSorry, you don't have permission for that.");
				}
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("colors")) {
			if ((strings.length == 0)) {
				Player p = (Player) cs;
				p.sendMessage("§aa§bb§cc§dd§ee§ff§11§22§33§44§55§66§77§88§99§00");
			} else {
				needsHelp(cs);
			}
		}

		if (cmnd.getName().equalsIgnoreCase("unignore")) {
			if (!(strings.length > 1)) {
				if (strings.length == 1) {
					Player player = Bukkit.getPlayer(cs.getName());
					player.performCommand("chat unignore " + strings[0]);
				} else {
					needsHelp(cs);
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
							Player target = getServer().getPlayer(strings[1]);
							Player player = (Player) cs;
							if (!(target == null)) {
								if (!target.getName().equalsIgnoreCase(cs.getName())) {
									List<String> friends = getFriendsConfig().getStringList("friends."+ cs.getName().toLowerCase());
									List<String> ignores = getIgnoresConfig().getStringList("ignores."+ cs.getName().toLowerCase());
									if (ignores.contains(target.getName().toLowerCase())) { 
										ignores.remove(target.getName().toLowerCase());
										getIgnoresConfig().set("ignores."+ cs.getName().toLowerCase(), ignores);
										saveIgnoresConfig();
									}
									if (!friends.contains(target.getName().toLowerCase())) {
										friends.add(target.getName().toLowerCase());
										getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
										saveFriendsConfig();
										cs.sendMessage("§aSuccessfully added " + target.getName() + " to your friends list!");
										if (!target.hasPermission("skitchat.hidefriendadded")) {
											target.sendMessage("§b" + player.getDisplayName() + " §badded you to their friends list!");
											target.sendMessage("§bIf you would like to add them back, type §a/friend "+ player.getName() + "§b.");
										}
									} else {
										cs.sendMessage("§cThat player is already on your friends list!");
									}
								} else {
									cs.sendMessage("§cYou can't add yourself as a friend!");
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("remove")) {
						Player target = getServer().getPlayer(strings[1]);
						if (strings.length == 2) {
							if (!(target == null)) {
								List<String> friends = getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());
								if (friends.contains(target.getName().toLowerCase())) {
									friends.remove(target.getName().toLowerCase());
									getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
									saveFriendsConfig();
									cs.sendMessage("§aSuccessfully removed " + target.getDisplayName() + " §afrom your friends list!");
								} else {
									cs.sendMessage("§cThat player isn't on your friends list!");
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("list")) {
						if (strings.length == 1) {
							List<String> friends = getFriendsConfig() .getStringList("friends." + cs.getName().toLowerCase());
							if (friends.size() != 0) {
								StringBuilder friendlist = new StringBuilder();
								for (String friend : friends) {
									friendlist.append("§7" + Bukkit.getOfflinePlayer(friend).getName().toLowerCase() + "§f, §7");
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
							Player target = getServer().getPlayer(strings[1]);
							if (target != null) {
								List<String> ignores = getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
								List<String> friends = getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());
								
								if (friends.contains(target.getName().toLowerCase())) { 
									friends.remove(target.getName().toLowerCase());
									getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
									saveFriendsConfig();
								}
								
								if (!target.getName().toString().equalsIgnoreCase(cs.getName())) {
									if (!ignores.contains(target.getName().toLowerCase())) {
										ignores.add(target.getName().toLowerCase());
										getIgnoresConfig().set("ignores."+ cs.getName().toLowerCase(), ignores);
										saveIgnoresConfig();
										cs.sendMessage("§aSuccessfully added " + target.getDisplayName() + " §ato your ignored list!");
									} else {
										cs.sendMessage("§cThat player is already on your ignored list!");
									}
								} else {
									cs.sendMessage("§cYou can't ignore yourself!");
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("rl")) {
						Player player = (Player) cs;
						player.performCommand("chat reload");
					}

					if (strings[0].equalsIgnoreCase("unignore")) {
						Player target = getServer().getPlayer(strings[1]);
						if (strings.length == 2) {
							if (!(target == null)) {
								List<String> ignores = getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
								if (!target.getName().toString()
										.equalsIgnoreCase(cs.getName())) {
									if (ignores.contains(target.getName()
											.toLowerCase())) {
										ignores.remove(target.getName().toLowerCase());
										getIgnoresConfig().set("ignores." + cs.getName().toLowerCase(), ignores);
										saveIgnoresConfig();
										cs.sendMessage("§aSuccessfully removed " + target.getDisplayName() + " §afrom your ignored list!");
									} else {
										cs.sendMessage("§cThat player isn't on your ignored list!");
									}
								} else {
									cs.sendMessage("§cYou can't ignore yourself!");
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("ignoredlist")) {
						if (strings.length == 1) {
							List<String> ignores = getIgnoresConfig().getStringList("ignores."+ cs.getName().toLowerCase());
							if (ignores.size() != 0) {
								StringBuilder ignoredlist = new StringBuilder();
								for (String friend : ignores) {
									ignoredlist.append("§7" + Bukkit.getOfflinePlayer(friend) .getName().toLowerCase() + "§f, §7");
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
						if (strings.length >= 1) {
							if (strings.length > 2) {
								needsHelp(cs);
								return true;
							}

							if (strings.length == 1) {
								cs.sendMessage("§b--------------- §6" + getDescription().getName() + " Commands Help §b---------------");
								cs.sendMessage("§cPage 1 of 2 §b| §c/chat help <page>");
								cs.sendMessage("§5@<player> <message>§7: Send a PM (used as a chat message).");
								cs.sendMessage("§5/chat add <player>§7: Add a player to your friends list.");
								cs.sendMessage("§5/chat remove <player>§7: Remove a player from your friends list.");
								cs.sendMessage("§5/chat toggle§7: Toggle seeing chat only from friends.");
								cs.sendMessage("§5/chat list§7: Display a list of all your friends.");
								cs.sendMessage("§5/chat ignore <player>§7: Ignore another player.");
								cs.sendMessage("§8§oSkitChat v" + getDescription().getVersion() + " by chaseoes.");
							}

							if (strings.length == 2) {
								if (strings[1].equalsIgnoreCase("1")) {
									((Player) cs).performCommand("chat help");
									return true;
								}
								if (strings[1].equalsIgnoreCase("2")) {
									cs.sendMessage("§b--------------- §6" + getDescription().getName() + " Commands Help §b---------------");
									cs.sendMessage("§cPage 2 of 2 §b| §c/chat help <page>");
									cs.sendMessage("§5/chat unignore <player>§7: Unignore another player.");
									cs.sendMessage("§5/chat ignoredlist§7: List all players you're currently ignoring.");
									cs.sendMessage("§5/chat pm <player> <message>§7: Send another player a message.");
									cs.sendMessage("§5/chat help aliases§7: SkitChat command aliases.");
									if (cs.hasPermission("skitchat.set")) {
										cs.sendMessage("§4/chat help set§7: Help page for the /chat set command..");
									}
									if (cs.hasPermission("skitchat.spy")) {
										cs.sendMessage("§4/chat spy§7: Spy on all private messages sent.");
									}
									if (cs.hasPermission("skitchat.set")) {
										cs.sendMessage("§4/chat set <option> <value>§7: Set a configuration option.");
									}
									if (cs.isOp()) {
										cs.sendMessage("§4/chat reload§7: Reload the SkitChat configuration files.");
									}
									return true;
								}
								if (strings[1].equalsIgnoreCase("aliases")) {
									cs.sendMessage("§b--------------- §6" + "Aliases" + " Commands Help §b---------------");
									cs.sendMessage("§5/friend <player>§7: Alias for /chat add.");
									cs.sendMessage("§5/unfriend remove <player>§7: Alias for /chat remove.");
									cs.sendMessage("§5/ignore <player>§7: Alias for /chat ignore.");
									cs.sendMessage("§5/unignore <player>§7: Alias for /chat unignore.");
									return true;
								}
								if (strings[1].equalsIgnoreCase("set")) {
									cs.sendMessage("§b--------------- §6" + getDescription().getName() + " Set Command Help §b---------------");
									cs.sendMessage("§2/chat set <option> <value> §8- Possible values for <option> are:");
									cs.sendMessage("§5settings.§7 logchatmessages§f, §7logpms§f, §7forcemove§f, §7nodupemessages§f, §7min-playercount§f, §7chatdelay§f, §7rejointime");
									cs.sendMessage("§5chat.§7 globalformat§f, §7global-join§f, §7global-leave§f, §7global-tabcolor§f, §7pm-from§f, §7pm-to§f, §7spyformat§f, §7meformat§f, §7friendcolor");
									cs.sendMessage("§5groups.groupname.§7 chatformat§f, §7join§f, §7leave§f, §7tabcolor");
									cs.sendMessage("§5players.playername.§7 join§f, §7leave§f, §7tabcolor");
									cs.sendMessage("§8Example: §2/chat set §asettings.logpms §2true");
									cs.sendMessage("§8Example: §2/chat set §aplayers.chaseoes.join §2Jesus has joined.");
									// cs.sendMessage("§7§oSkitChat v" + getDescription().getVersion() + " by chaseoes.");
									return true;
								} else {
									needsHelp(cs);
								}
							}

						} else {
							needsHelp(cs);
						}
					}

//					if (strings[0].equalsIgnoreCase("debug")) {
//						Player player = (Player) cs;
//						if (!cs.hasPermission("skitchat.override")) {
//							cs.sendMessage("§8You don't have the §askitchat.override §8permission!");
//						} else {
//							cs.sendMessage("§8You have the §askitchat.override §8permission!");
//						}
//						cs.sendMessage("§8This server is using SkitChat version §a"
//								+ getDescription().getVersion() + " §8.");
//						if (player.isOp()) {
//							cs.sendMessage("§8You are an OP!");
//						} else {
//							cs.sendMessage("§8You aren't an OP!");
//						}
//						cs.sendMessage("§8Global join message: §a"
//								+ getConfig().getString("chat.global-join")
//								+ "§8.");
//						if (getConfig().getString(
//								"players." + player.getName() + ".join") != null) {
//							cs.sendMessage("§8Your join message: §a"
//									+ getConfig().getString(
//											"players." + player.getName()
//													+ ".join") + "§8.");
//						}
//						cs.sendMessage("§8Global leave message: §a"
//								+ getConfig().getString("chat.global-leave")
//								+ "§8.");
//						if (getConfig().getString(
//								"players." + player.getName() + ".leae") != null) {
//							cs.sendMessage("§8Your leave message: §a"
//									+ getConfig().getString(
//											"players." + player.getName()
//													+ ".leave") + "§8.");
//						}
//						cs.sendMessage("§8Your display name: §a"
//								+ player.getDisplayName() + "§8.");
//						cs.sendMessage("§8Your group name: §a"
//								+ permission.getPrimaryGroup(player) + "§8.");
//					}

					if (strings[0].equalsIgnoreCase("spy")) {
						if (cs.hasPermission("skitchat.spy")) {
							if (strings.length == 1) {
								if (getPlayerdataConfig().getBoolean("playerdata." + cs.getName() + ".spying")) {
									getPlayerdataConfig().set("playerdata." + cs.getName() + ".spying", false);
									saveConfig();
									cs.sendMessage("§aYou are no longer spying on private messages.");
								} else {
									getPlayerdataConfig().set("playerdata." + cs.getName() + ".spying", true);
									savePlayerdataConfig();
									cs.sendMessage("§aSuccessfully started spying on private messages!");
								}
							} else {
								needsHelp(cs);
							}
						} else {
							cs.sendMessage("§cSorry, you don't have permission to do that.");
						}
					}

					if (strings[0].equalsIgnoreCase("toggle")) {
						if (strings.length == 1) {
							if (toggled.contains(cs.getName())) {
								toggled.remove(cs.getName());
								cs.sendMessage("§aSuccessfully toggled! You will now see the global chat.");
							} else {
								toggled.add(cs.getName());
								cs.sendMessage("§aSuccessfully toggled! Type §5/chat toggle §aagain to toggle off.\n§aYou'll no longer see the global chat - only messages sent by friends.");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("pmtoggle")) {
						if (strings.length == 1) {
							if (cs.hasPermission("skitchat.pmtoggle")) {
								if (pmtoggled.contains(cs.getName())) {
									pmtoggled.remove(cs.getName());
									cs.sendMessage("§aSuccessfully toggled! You will now be able to receive PM's.");
								} else {
									pmtoggled.add(cs.getName());
									cs.sendMessage("§aSuccessfully toggled! Type §5/chat pmtoggle §aagain to toggle off.\n§aYou'll no longer be able to receive PM's.");
								}
							} else {
								cs.sendMessage("§cSorry, you don't have permission to do that.");
							}
						} else {
							needsHelp(cs);
						}
					}

					if (strings[0].equalsIgnoreCase("reload")) {
						if (cs.isOp()) {
							if (strings.length == 1) {
								reloadConfig();
								reloadFriendsConfig();
								reloadIgnoresConfig();
								reloadPlayerdataConfig();
								saveFriendsConfig();
								saveIgnoresConfig();
								savePlayerdataConfig();
								saveConfig();
								cs.sendMessage("§aSuccessfully reloaded all SkitChat configuration files!");
							} else {
								needsHelp(cs);
							}
						} else {
							cs.sendMessage("§cSorry, you don't have permission to do that.");
						}
					}

					if (strings[0].equalsIgnoreCase("set")) {
						if (cs.hasPermission("skitchat.set")) {
							if (strings.length > 2) {
								String option = strings[1];
								for (int i = 2; i < strings.length - 1; i++) {
									value += strings[i] + ' ';
								}
								value += strings[strings.length - 1];
								getConfig().set(option, value);
								saveConfig();
								reloadConfig();
								cs.sendMessage("§aSuccessfully set that configuration value.");
							} else {
								needsHelp(cs);
							}
						} else {
							cs.sendMessage("§cSorry, you don't have permission to do that.");
						}
					}

					if (strings[0].equalsIgnoreCase("pm")) {
						Player target = getServer().getPlayer(strings[1]);
						Player[] online = Bukkit.getOnlinePlayers();
						if (strings.length > 2) {
							if (!(target == null)) { 
								if (!pmtoggled.contains(target.getName())) {
									String pmmsg = "";
									for (int i = 2; i < strings.length - 1; i++) {
										pmmsg += strings[i] + ' ';
									}
									pmmsg += strings[strings.length - 1];
									getPlayerdataConfig().set("playerdata." + target.getName() + ".lastpmfrom", cs.getName());
									cs.sendMessage(pmFormatter(getConfig().getString("chat.pm-to"), pmmsg, target));
									if (!(getIgnoresConfig().getStringList("ignores." + target.getName().toLowerCase()).contains(cs.getName().toLowerCase()))) {
									target.sendMessage(pmFormatter(getConfig().getString("chat.pm-from"), pmmsg, (Player) cs));
									}
									if (getConfig().getBoolean("settings.logpms")) {
										log.info("[" + getDescription().getName() + "] [PM] " + cs.getName() + " -> " + target.getName() + ": " + pmmsg);
									}
									for (Player players : online) {
										if (players.hasPermission("skitchat.spy") && (!players.getName().equalsIgnoreCase(target.getName()))) {
											if (getPlayerdataConfig().getBoolean("playerdata." + players.getName() + ".spying")) {
												players.sendMessage(spyFormatter(getConfig().getString("chat.spyformat"),pmmsg, (Player) cs, target));
											}
										}
									}
								} else {
									cs.sendMessage("§cYou are not allowed to PM " + target.getDisplayName() + "§c.");
								}
							} else {
								cs.sendMessage("§cThat player isn't online!");
							}
						} else {
							needsHelp(cs);
						}
					}

				} else {
					cs.sendMessage("§b--------------- §6§l " + getDescription().getName() + " §r§6v" + getDescription().getVersion() + " §b---------------");
					cs.sendMessage("§7Plugin developed by §9chaseoes§7.");
					cs.sendMessage("§7Type §b/chat help §7for help.");
					cs.sendMessage("§7Type §b/chat help aliases §7for command aliases.");
					cs.sendMessage("§7Download Here: §8http://dev.bukkit.org/server-mods/skitchat/");
					
				}
			} else {
				cs.sendMessage("[" + getDescription().getName()
						+ "] Sorry, that command can only be used in-game.");
			}
		}
		return true;
	}

	public void needsHelp(CommandSender cs) {
		cs.sendMessage("§cIncorrect command usage! Type §b/chat help §cfor help.");
	}

	public String pmFormatter(String string, String message, Player cs) {
		Player player = cs;
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String newstring = colorize(
				string.replace("%n", player.getName())
						.replace("%p", getPrefix(player))
						.replace("%s", getSuffix(player))
						.replace("%dn", player.getDisplayName())
						.replace("%g", group)).replace("%m", message);
		return newstring;
	}

	public String spyFormatter(String string, String message, Player cs,
			Player target) {
		Player player = cs;
		String newstring = colorize(
				string.replace("%to", target.getDisplayName()).replace("%from", player.getDisplayName())).replace("%m", message);
		return newstring;
	}

	public String meFormatter(String string, Player cs) {
		Player player = cs;
		String group;
		if (SkitChat.permission.getPrimaryGroup(player) != null) {
			group = SkitChat.permission.getPrimaryGroup(player);
		} else {
			group = "default";
		}
		String newstring = colorize(
				string.replace("%n", player.getName())
						.replace("%p", getPrefix(player))
						.replace("%s", getSuffix(player))
						.replace("%dn", player.getDisplayName())
						.replace("%g", group))
						.replace("%m", memessage)
				.replace("sg", "");
		return newstring;
	}

	// Vault Support
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	// Friends Configuration
	public void reloadFriendsConfig() {
		if (friendsConfigFile == null) {
			friendsConfigFile = new File(getDataFolder(), "friends.yml");
		}
		friendsConfig = YamlConfiguration.loadConfiguration(friendsConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("friends.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			friendsConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getFriendsConfig() {
		if (friendsConfig == null) {
			reloadFriendsConfig();
		}
		return friendsConfig;
	}

	public void saveFriendsConfig() {
		if (friendsConfig == null || friendsConfigFile == null) {
			return;
		}
		try {
			friendsConfig.save(friendsConfigFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"[SkitChat] Could not save config to " + friendsConfigFile,
					ex);
		}
	}

	// Ignores Configuration
	public void reloadIgnoresConfig() {
		if (ignoresConfigFile == null) {
			ignoresConfigFile = new File(getDataFolder(), "ignores.yml");
		}
		ignoresConfig = YamlConfiguration.loadConfiguration(ignoresConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("ignores.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			ignoresConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getIgnoresConfig() {
		if (ignoresConfig == null) {
			reloadIgnoresConfig();
		}
		return ignoresConfig;
	}

	public void saveIgnoresConfig() {
		if (ignoresConfig == null || ignoresConfigFile == null) {
			return;
		}
		try {
			ignoresConfig.save(ignoresConfigFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"[SkitChat] Could not save config to " + ignoresConfigFile,
					ex);
		}
	}

	// Playerdata Configuration
	public void reloadPlayerdataConfig() {
		if (playerdataConfigFile == null) {
			playerdataConfigFile = new File(getDataFolder(), "playerdata.yml");
		}
		playerdataConfig = YamlConfiguration
				.loadConfiguration(playerdataConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("playerdata.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			playerdataConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getPlayerdataConfig() {
		if (playerdataConfig == null) {
			reloadPlayerdataConfig();
		}
		return playerdataConfig;
	}

	public void savePlayerdataConfig() {
		if (playerdataConfig == null || playerdataConfigFile == null) {
			return;
		}
		try {
			playerdataConfig.save(playerdataConfigFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(
					Level.SEVERE,
					"[SkitChat] Could not save config to "
							+ playerdataConfigFile, ex);
		}
	}
	
	// Channels Configuration
	public void reloadChannelsConfig() {
		if (channelsConfigFile == null) {
			channelsConfigFile = new File(getDataFolder(), "channels.yml");
		}
		playerdataConfig = YamlConfiguration
				.loadConfiguration(channelsConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("channels.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			channelsConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getChannelsConfig() {
		if (channelsConfig == null) {
			reloadChannelsConfig();
		}
		return channelsConfig;
	}

	public void saveChannelsConfig() {
		if (channelsConfig == null || channelsConfigFile == null) {
			return;
		}
		try {
			playerdataConfig.save(channelsConfigFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(
					Level.SEVERE,
					"[SkitChat] Could not save config to "
							+ channelsConfigFile, ex);
		}
	}

	public String colorize(String s) {
		if (s == null)
			return null;
		return s.replaceAll("&([l-o0-9a-f])", "\u00A7$1");
	}

	public String getPrefix(Player player) {
		if (setupChat()) {
			String group;
			if (SkitChat.permission.getPrimaryGroup(player) != null) {
				group = SkitChat.permission.getPrimaryGroup(player);
			} else {
				group = "default";
			}
			String prefix = SkitChat.chat.getGroupPrefix(player.getWorld(),
					group);
			return prefix;
		}
		if (SkitChat.permission.getPrimaryGroup(player) != null
				&& getConfig().getString(
						"groups." + SkitChat.permission.getPrimaryGroup(player)
								+ ".prefix") != null) {
			String group = SkitChat.permission.getPrimaryGroup(player)
					.toLowerCase();
			String prefix = colorize(getConfig().getString(
					"groups." + group + ".prefix"));
			return prefix;
		} else {
			return getConfig().getString("chat.defaultprefix");
		}
	}

	public String getSuffix(Player player) {
		if (setupChat()) {
			String group = SkitChat.chat.getPrimaryGroup(player);
			String prefix = SkitChat.chat.getGroupSuffix(player.getWorld(),
					group);
			return prefix;
		}
		if (SkitChat.permission.getPrimaryGroup(player) != null
				&& getConfig().getString(
						"groups." + SkitChat.permission.getPrimaryGroup(player)
								+ ".suffix") != null) {
			String group = SkitChat.permission.getPrimaryGroup(player)
					.toLowerCase();
			String prefix = colorize(getConfig().getString(
					"groups." + group + ".suffix"));
			return prefix;
		} else {
			return getConfig().getString("chat.defaultsuffix");
		}
	}
	
	public static void blockDomains() {
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
