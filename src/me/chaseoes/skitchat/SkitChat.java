package me.chaseoes.skitchat;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.chaseoes.skitchat.listeners.ChatListener;
import me.chaseoes.skitchat.listeners.PlayerListener;
import me.chaseoes.skitchat.listeners.SpamListener;
import me.chaseoes.skitchat.utilities.Formatter;
import me.chaseoes.skitchat.utilities.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SkitChat extends JavaPlugin {

    public final Logger log = Logger.getLogger("Minecraft");
    public final static Set<String> pming = new HashSet<String>();
    public final static Set<String> toggled = new HashSet<String>();
    public final static Set<String> pmtoggled = new HashSet<String>();
    public String finalfriends;
    public String finalignored;
    String msg = "";
    String pmmsg = "";
    String value = "";
    String memessage = "";
    Helper help = new Helper(this);
    public Permission permission = null;
    public Chat chat = null;
    Boolean enablemove;

    // Vault Chat Support
    public boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    // Vault Permission Support
    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    @Override
    public void onEnable() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "[" + getDescription().getName() + "] Could not load configuration!", ex);
        }

        Utilities.getInstance().setup(this);
        Formatter.getInstance().setup(this);
        Configuration.getInstance().setup(this);
        PlayerData.getInstance().setup(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new SpamListener(this), this);
        setupChat();
        setupPermissions();
        Utilities.getInstance().blockDomains();
        Utilities.getInstance().exceptions = getConfig().getStringList("settings.urls.exceptions");
        
        // Auto-Updater by h31ix
        if(Utilities.getInstance().plugin.getConfig().getBoolean("settings.updatecheck")) {
            Updater updater = new Updater(this, "skitchat", this.getFile(), Updater.UpdateType.DEFAULT, false);
        }
        
    }

    @Override
    public void onDisable() {
        Configuration.getInstance().saveFriendsConfig();
        Configuration.getInstance().saveIgnoresConfig();
        Configuration.getInstance().savePlayerdataConfig();
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("That command can only be used in-game.");
            return true;
        }
        if (cmnd.getName().equalsIgnoreCase("pm")) {
            if (cs.hasPermission("skitchat.pm")) {
                if (!(strings.length > 1)) {
                    if (strings.length == 1) {
                        Player target = getServer().getPlayer(strings[0]);
                        if (!(target == null)) {
                            if (!target.hasPermission("skitchat.nopm")) {
                                if (!pmtoggled.contains(target.getName())) {
                                    pming.add(cs.getName());
                                    PlayerData.getInstance().setPMTarget(cs.getName(), target.getName());
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
                            PlayerData.getInstance().setPMTarget(cs.getName(), "");
                            cs.sendMessage("§aSuccessfully stopped the private conversation.");
                        } else {
                            cs.sendMessage("§cYou aren't in a conversation with anyone!");
                            cs.sendMessage("§cTo join a conversation: /" + cmnd.getName() + " <player>");
                        }
                    }
                } else {
                    String pm = "";
                    for (int i = 1; i < strings.length - 1; i++) {
                        pm += strings[i] + ' ';
                    }
                    pm += strings[strings.length - 1];
                    Player player = (Player) cs;
                    player.performCommand("chat pm " + strings[0] + " " + pm);
                }
            } else {
                help.noPermission(cs);
            }
        }



        if (cmnd.getName().equalsIgnoreCase("chatlist")) {
            if (cs.hasPermission("skitchat.list")) {
                if (strings.length == 0) {
                    Map<String, List<Player>> sort = new HashMap<String, List<Player>>();
                    for (Player OnlinePlayer : getServer().getOnlinePlayers()) {
                        Player player = OnlinePlayer;
                        String group = Utilities.getInstance().getGroup(player);
                        List<Player> list = sort.get(group);
                        if (list == null) {
                            list = new ArrayList<Player>();
                            sort.put(group, list);
                        }
                        list.add(player);
                    }

                    final String[] groups = sort.keySet().toArray(new String[0]);
                    Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
                    for (String group : groups) {
                        final StringBuilder groupString = new StringBuilder();
                        groupString.append("§8").append(group).append(": §7");
                        final List<Player> users = sort.get(group);
                        boolean first = true;
                        for (Player user : users) {
                            if (!first) {
                                groupString.append(", ");
                            } else {
                                first = false;
                            }
                            groupString.append(user.getDisplayName());
                            groupString.append("§f");
                        }
                        cs.sendMessage(groupString.toString());
                    }
                }
            } else {
                help.noPermission(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("friend")) {
            if (!(strings.length > 1)) {
                if (strings.length == 1) {
                    Player player = Bukkit.getPlayer(cs.getName());
                    player.performCommand("chat add " + strings[0]);
                } else {
                    help.needsHelp(cs);
                }
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("unfriend")) {
            if (!(strings.length > 1)) {
                if (strings.length == 1) {
                    Player player = Bukkit.getPlayer(cs.getName());
                    player.performCommand("chat remove " + strings[0]);
                } else {
                    help.needsHelp(cs);
                }
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("ignore")) {
            if (!(strings.length > 1)) {
                if (strings.length == 1) {
                    Player player = Bukkit.getPlayer(cs.getName());
                    player.performCommand("chat ignore " + strings[0]);
                } else {
                    help.needsHelp(cs);
                }
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("reply")) {
            StringBuilder sb = new StringBuilder();
            for (String arg : strings) {
                sb.append(arg).append(" ");
            }
            Player player = Bukkit.getPlayer(cs.getName());
            String target = PlayerData.getInstance().getLastPMFrom(player.getName());
            player.performCommand("chat pm " + target + " " + sb.toString());
        }

        if (cmnd.getName().equalsIgnoreCase("me")) {
            if ((strings.length >= 1)) {
                Player p = (Player) cs;
                if (p.hasPermission("skitchat.me")) {
                    StringBuilder sb = new StringBuilder();
                    for (String arg : strings) {
                        sb.append(arg).append(" ");
                    }
                    memessage = sb.toString();
                    for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                        if (!(Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + players.getName().toLowerCase()).contains(cs.getName().toLowerCase()))) {
                            players.sendMessage(Formatter.getInstance().me(getConfig().getString("chat.meformat"), memessage, p));
                        }
                    }
                } else {
                    cs.sendMessage("§cSorry, you don't have permission for that.");
                }
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("colors")) {
            if ((strings.length == 0)) {
                Player p = (Player) cs;
                p.sendMessage("§aa§bb§cc§dd§ee§ff§11§22§33§44§55§66§77§88§99§00");
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("unignore")) {
            if (!(strings.length > 1)) {
                if (strings.length == 1) {
                    Player player = Bukkit.getPlayer(cs.getName());
                    player.performCommand("chat unignore " + strings[0]);
                } else {
                    help.needsHelp(cs);
                }
            } else {
                help.needsHelp(cs);
            }
        }

        if (cmnd.getName().equalsIgnoreCase("chat")) {
            if (strings.length > 0) {
                if (strings[0].equalsIgnoreCase("add")) {
                    if (cs.hasPermission("skitchat.friends")) {
                        if (strings.length == 2) {
                            Player target = getServer().getPlayer(strings[1]);
                            Player player = (Player) cs;
                            if (!(target == null)) {
                                if (!target.getName().equalsIgnoreCase(cs.getName())) {
                                    List<String> friends = Configuration.getInstance().getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());
                                    List<String> ignores = Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
                                    if (ignores.contains(target.getName().toLowerCase())) {
                                        ignores.remove(target.getName().toLowerCase());
                                        Configuration.getInstance().getIgnoresConfig().set("ignores." + cs.getName().toLowerCase(), ignores);
                                        Configuration.getInstance().saveIgnoresConfig();
                                    }
                                    if (!friends.contains(target.getName().toLowerCase())) {
                                        friends.add(target.getName().toLowerCase());
                                        Configuration.getInstance().getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
                                        Configuration.getInstance().saveFriendsConfig();
                                        cs.sendMessage("§aSuccessfully added " + target.getName() + " to your friends list!");
                                        if (!target.hasPermission("skitchat.hidefriendadded")) {
                                            target.sendMessage("§b" + player.getDisplayName() + " §badded you to their friends list!");
                                            target.sendMessage("§bIf you would like to add them back, type §a/friend " + player.getName() + "§b.");
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
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("remove")) {
                    if (cs.hasPermission("skitchat.friends")) {
                        Player target = getServer().getPlayer(strings[1]);
                        if (strings.length == 2) {
                            if (!(target == null)) {
                                List<String> friends = Configuration.getInstance().getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());
                                if (friends.contains(target.getName().toLowerCase())) {
                                    friends.remove(target.getName().toLowerCase());
                                    Configuration.getInstance().getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
                                    Configuration.getInstance().saveFriendsConfig();
                                    cs.sendMessage("§aSuccessfully removed " + target.getDisplayName() + " §afrom your friends list!");
                                } else {
                                    cs.sendMessage("§cThat player isn't on your friends list!");
                                }
                            } else {
                                cs.sendMessage("§cThat player isn't online!");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("list")) {
                    if (cs.hasPermission("skitchat.friends")) {
                        if (strings.length == 1) {
                            List<String> friends = Configuration.getInstance().getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());
                            if (!friends.isEmpty()) {
                                StringBuilder friendlist = new StringBuilder();
                                for (String friend : friends) {
                                    friendlist.append("§7").append(Bukkit.getOfflinePlayer(friend).getName().toLowerCase()).append("§f, §7");
                                }
                                finalfriends = friendlist.toString();
                                cs.sendMessage("§aYour chat friends are:");
                                cs.sendMessage(finalfriends);
                            } else {
                                cs.sendMessage("§cYou have no friends! :(");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("ignore")) {
                    if (cs.hasPermission("skitchat.ignores")) {
                        if (strings.length == 2) {
                            Player target = getServer().getPlayer(strings[1]);
                            if (target != null) {
                                List<String> ignores = Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
                                List<String> friends = Configuration.getInstance().getFriendsConfig().getStringList("friends." + cs.getName().toLowerCase());

                                if (friends.contains(target.getName().toLowerCase())) {
                                    friends.remove(target.getName().toLowerCase());
                                    Configuration.getInstance().getFriendsConfig().set("friends." + cs.getName().toLowerCase(), friends);
                                    Configuration.getInstance().saveFriendsConfig();
                                }

                                if (!target.getName().toString().equalsIgnoreCase(cs.getName())) {
                                    if (!ignores.contains(target.getName().toLowerCase())) {
                                        ignores.add(target.getName().toLowerCase());
                                        Configuration.getInstance().getIgnoresConfig().set("ignores." + cs.getName().toLowerCase(), ignores);
                                        Configuration.getInstance().saveIgnoresConfig();
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
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("rl")) {
                    Player player = (Player) cs;
                    player.performCommand("chat reload");
                }

                if (strings[0].equalsIgnoreCase("unignore")) {
                    if (cs.hasPermission("skitchat.ignores")) {
                        Player target = getServer().getPlayer(strings[1]);
                        if (strings.length == 2) {
                            if (!(target == null)) {
                                List<String> ignores = Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
                                if (!target.getName().toString().equalsIgnoreCase(cs.getName())) {
                                    if (ignores.contains(target.getName().toLowerCase())) {
                                        ignores.remove(target.getName().toLowerCase());
                                        Configuration.getInstance().getIgnoresConfig().set("ignores." + cs.getName().toLowerCase(), ignores);
                                        Configuration.getInstance().saveIgnoresConfig();
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
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("ignoredlist")) {
                    if (cs.hasPermission("skitchat.ignores")) {
                        if (strings.length == 1) {
                            List<String> ignores = Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + cs.getName().toLowerCase());
                            if (!ignores.isEmpty()) {
                                StringBuilder ignoredlist = new StringBuilder();
                                for (String friend : ignores) {
                                    ignoredlist.append("§7").append(Bukkit.getOfflinePlayer(friend).getName().toLowerCase()).append("§f, §7");
                                }
                                finalignored = ignoredlist.toString();
                                cs.sendMessage("§aYou have these people ignored:");
                                cs.sendMessage(finalignored);
                            } else {
                                cs.sendMessage("§cYou have nobody ignored!");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("help")) {
                    if (strings.length >= 1) {
                        if (strings.length > 2) {
                            help.needsHelp(cs);
                            return true;
                        }

                        if (strings.length == 1) {
                            cs.sendMessage("§b--------------- §6" + getDescription().getName() + " Commands Help §b---------------");
                            cs.sendMessage("§cPage 1 of 2 §b| §c/chat help <page>");
                            if (cs.hasPermission("skitchat.pm")) {
                                cs.sendMessage("§5@<player> <message>§7: Send a PM (used as a chat message).");
                            }
                            if (cs.hasPermission("skitchat.friends")) {
                                cs.sendMessage("§5/chat add <player>§7: Add a player to your friends list.");
                                cs.sendMessage("§5/chat remove <player>§7: Remove a player from your friends list.");
                                cs.sendMessage("§5/chat list§7: Display a list of all your friends.");
                                cs.sendMessage("§5/chat toggle§7: Toggle seeing chat only from friends.");
                            }
                            if (cs.hasPermission("skitchat.ignores")) {
                                cs.sendMessage("§5/chat ignore <player>§7: Ignore another player.");
                            }
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
                                if (cs.hasPermission("skitchat.ignores")) {
                                    cs.sendMessage("§5/chat unignore <player>§7: Unignore another player.");
                                    cs.sendMessage("§5/chat ignoredlist§7: List all players you're currently ignoring.");
                                }
                                if (cs.hasPermission("skitchat.pm")) {
                                    cs.sendMessage("§5/chat pm <player> <message>§7: Send another player a message.");
                                }
                                if (cs.hasPermission("skitchat.pmtoggle")) {
                                    cs.sendMessage("§5/chat pmtoggle§7: Toggle being able to receive PM's.");
                                }
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
                                if (cs.hasPermission("skitchat.reload")) {
                                    cs.sendMessage("§4/chat reload§7: Reload the SkitChat configuration files.");
                                }
                                return true;
                            }
                            if (strings[1].equalsIgnoreCase("aliases")) {
                                cs.sendMessage("§b--------------- §6" + "Aliases" + " Commands Help §b---------------");
                                if (cs.hasPermission("skitchat.friends")) {
                                    cs.sendMessage("§5/friend <player>§7: Alias for /chat add.");
                                    cs.sendMessage("§5/unfriend remove <player>§7: Alias for /chat remove.");
                                }
                                if (cs.hasPermission("skitchat.ignores")) {
                                    cs.sendMessage("§5/ignore <player>§7: Alias for /chat ignore.");
                                    cs.sendMessage("§5/unignore <player>§7: Alias for /chat unignore.");
                                }
                                return true;
                            }
                            if (strings[1].equalsIgnoreCase("set")) {
                                if (cs.hasPermission("skitchat.set")) {
                                    cs.sendMessage("§b--------------- §6" + getDescription().getName() + " Set Command Help §b---------------");
                                    cs.sendMessage("§2/chat set <option> <value> §8- Possible values for <option> are:");
                                    cs.sendMessage("§5settings.§7 logchatmessages§f, §7logpms§f, §7forcemove§f, §7nodupemessages§f, §7min-playercount§f, §7chatdelay§f, §7rejointime");
                                    cs.sendMessage("§5chat.§7 globalformat§f, §7global-join§f, §7global-leave§f, §7global-tabcolor§f, §7pm-from§f, §7pm-to§f, §7spyformat§f, §7meformat§f, §7friendcolor");
                                    cs.sendMessage("§5groups.groupname.§7 chatformat§f, §7join§f, §7leave§f, §7tabcolor");
                                    cs.sendMessage("§5players.playername.§7 join§f, §7leave§f, §7tabcolor");
                                    cs.sendMessage("§8Example: §2/chat set §asettings.logpms §2true");
                                    cs.sendMessage("§8Example: §2/chat set §aplayers.chaseoes.join §2Jesus has joined.");
                                } else {
                                    help.noPermission(cs);
                                }
                                return true;
                            } else {
                                help.needsHelp(cs);
                            }
                        }

                    } else {
                        help.needsHelp(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("spy")) {
                    if (cs.hasPermission("skitchat.spy")) {
                        if (strings.length == 1) {
                            if (PlayerData.getInstance().isSpying(cs.getName())) {
                                PlayerData.getInstance().setSpying(cs.getName(), false);
                                cs.sendMessage("§aYou are no longer spying on private messages.");
                            } else {
                                PlayerData.getInstance().setSpying(cs.getName(), true);
                                cs.sendMessage("§aSuccessfully started spying on private messages!");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("toggle")) {
                    if (cs.hasPermission("skitchat.toggle")) {
                        if (strings.length == 1) {
                            if (toggled.contains(cs.getName())) {
                                toggled.remove(cs.getName());
                                cs.sendMessage("§aSuccessfully toggled! You will now see the global chat.");
                            } else {
                                toggled.add(cs.getName());
                                cs.sendMessage("§aSuccessfully toggled! Type §5/chat toggle §aagain to toggle off.\n§aYou'll no longer see the global chat - only messages sent by friends.");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("pmtoggle")) {
                    if (cs.hasPermission("skitchat.pmtoggle")) {
                        if (strings.length == 1) {
                            if (pmtoggled.contains(cs.getName())) {
                                pmtoggled.remove(cs.getName());
                                cs.sendMessage("§aSuccessfully toggled! You will now be able to receive PM's.");
                            } else {
                                pmtoggled.add(cs.getName());
                                cs.sendMessage("§aSuccessfully toggled! Type §5/chat pmtoggle §aagain to toggle off.\n§aYou'll no longer be able to receive PM's.");
                            }
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("reload")) {
                    if (cs.hasPermission("skitchat.reload")) {
                        if (strings.length == 1) {
                            Configuration.getInstance().reloadAll();
                            cs.sendMessage("§aSuccessfully reloaded all SkitChat configuration files!");
                        } else {
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
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
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

                if (strings[0].equalsIgnoreCase("pm")) {
                    if (cs.hasPermission("skitchat.pm")) {
                        Player target = getServer().getPlayer(strings[1]);
                        Player[] online = Bukkit.getOnlinePlayers();
                        if (strings.length > 2) {
                            if (!(target == null)) {
                                if (!pmtoggled.contains(target.getName())) {
                                    pmmsg = "";
                                    for (int i = 2; i < strings.length - 1; i++) {
                                        pmmsg += strings[i] + ' ';
                                    }
                                    pmmsg += strings[strings.length - 1];
                                    PlayerData.getInstance().setLastPMFrom(cs.getName(), target.getName());
                                    cs.sendMessage(Formatter.getInstance().pm(getConfig().getString("chat.pm-to"), pmmsg, target));
                                    if (!(Configuration.getInstance().getIgnoresConfig().getStringList("ignores." + target.getName().toLowerCase()).contains(cs.getName().toLowerCase()))) {
                                        target.sendMessage(Formatter.getInstance().pm(getConfig().getString("chat.pm-from"), pmmsg, (Player) cs));
                                    }
                                    if (getConfig().getBoolean("settings.logpms")) {
                                        log.info("[" + getDescription().getName() + "] [PM] " + cs.getName() + " -> " + target.getName() + ": " + pmmsg);
                                    }
                                    for (Player players : online) {
                                        if (players.hasPermission("skitchat.spy") && (!players.getName().equalsIgnoreCase(target.getName())) && (!target.getName().equalsIgnoreCase(players.getName()))) {
                                            if (PlayerData.getInstance().isSpying(players.getName())) {
                                                players.sendMessage(Formatter.getInstance().spy(getConfig().getString("chat.spyformat"), pmmsg, (Player) cs, target));
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
                            help.needsHelp(cs);
                        }
                    } else {
                        help.noPermission(cs);
                    }
                }

            } else {
                cs.sendMessage("§b--------------- §6§l " + getDescription().getName() + " §r§6v" + getDescription().getVersion() + " §b---------------");
                cs.sendMessage("§7Plugin developed by §9chaseoes§7.");
                cs.sendMessage("§7Type §b/chat help §7for help.");
                cs.sendMessage("§7Type §b/chat help aliases §7for command aliases.");
                cs.sendMessage("§7Download Here: §8http://dev.bukkit.org/server-mods/skitchat/");
            }
        }
        return true;
    }
}
