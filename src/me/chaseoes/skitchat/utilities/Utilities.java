package me.chaseoes.skitchat.utilities;

import java.util.ArrayList;
import java.util.List;
import me.chaseoes.skitchat.SkitChat;
import org.bukkit.entity.Player;

public class Utilities {

    public ArrayList<String> blocked = new ArrayList();
    public List<String> exceptions = new ArrayList();
    public SkitChat plugin;
    static Utilities instance = new Utilities();

    private Utilities() {
    }

    public static Utilities getInstance() {
        return instance;
    }

    public void setup(SkitChat p) {
        plugin = p;
    }

    // Taste the rainbow.
    public String colorize(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("&([l-ok0-8k9a-f])", "\u00A7$1");
    }

    // Get the primary group.
    public String getGroup(Player player) {
        if (plugin.setupPermissions()
                && (Utilities.getInstance().plugin.permission.getPrimaryGroup(player) != null)) {
            return Utilities.getInstance().plugin.permission.getPrimaryGroup(player);
        } else {
            return Utilities.getInstance().plugin.getConfig().getString("info.defaultgroup");
        }
    }

    // Prefixes
    public String getPrefix(Player player) {
        String group = getGroup(player);
        if (Utilities.getInstance().plugin.setupChat()
                && !Utilities.getInstance().plugin.getConfig().getBoolean("info.enable")) {
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
    public String capitalizeFirstLetterOfEachSentence(String str) {
        char[] arr = str.toCharArray();
        boolean cap = true;
        boolean space_found = true;
        for (int i = 0; i < arr.length; i++) {
            if (cap) {
                // white space includes \n, space
                if (Character.isWhitespace(arr[i])) {
                    space_found = true;
                } else {
                    if (space_found && !Character.isUpperCase(arr[i])) {
                        arr[i] = Character.toUpperCase(arr[i]);
                    }

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

    // http://data.iana.org/TLD/tlds-alpha-by-domain.txt
    public void blockDomains() {
        blocked.add(".ac");
        blocked.add(".ad");
        blocked.add(".ae");
        blocked.add(".aero");
        blocked.add(".af");
        blocked.add(".ag");
        blocked.add(".ai");
        blocked.add(".al");
        blocked.add(".am");
        blocked.add(".an");
        blocked.add(".ao");
        blocked.add(".aq");
        blocked.add(".ar");
        blocked.add(".arpa");
        blocked.add(".as");
        blocked.add(".asia");
        blocked.add(".at");
        blocked.add(".au");
        blocked.add(".aw");
        blocked.add(".ax");
        blocked.add(".az");
        blocked.add(".ba");
        blocked.add(".bb");
        blocked.add(".bd");
        blocked.add(".be");
        blocked.add(".bf");
        blocked.add(".bg");
        blocked.add(".bh");
        blocked.add(".bi");
        blocked.add(".biz");
        blocked.add(".bj");
        blocked.add(".bm");
        blocked.add(".bn");
        blocked.add(".bo");
        blocked.add(".br");
        blocked.add(".bs");
        blocked.add(".bt");
        blocked.add(".bv");
        blocked.add(".bw");
        blocked.add(".by");
        blocked.add(".bz");
        blocked.add(".ca");
        blocked.add(".cat");
        blocked.add(".cc");
        blocked.add(".cd");
        blocked.add(".cf");
        blocked.add(".cg");
        blocked.add(".ch");
        blocked.add(".ci");
        blocked.add(".ck");
        blocked.add(".cl");
        blocked.add(".cm");
        blocked.add(".cn");
        blocked.add(".co");
        blocked.add(".com");
        blocked.add(".coop");
        blocked.add(".cr");
        blocked.add(".cu");
        blocked.add(".cv");
        blocked.add(".cw");
        blocked.add(".cx");
        blocked.add(".cy");
        blocked.add(".cz");
        blocked.add(".de");
        blocked.add(".dj");
        blocked.add(".dk");
        blocked.add(".dm");
        blocked.add(".do");
        blocked.add(".dz");
        blocked.add(".ec");
        blocked.add(".edu");
        blocked.add(".ee");
        blocked.add(".eg");
        blocked.add(".er");
        blocked.add(".es");
        blocked.add(".et");
        blocked.add(".eu");
        blocked.add(".fi");
        blocked.add(".fj");
        blocked.add(".fk");
        blocked.add(".fm");
        blocked.add(".fo");
        blocked.add(".fr");
        blocked.add(".ga");
        blocked.add(".gb");
        blocked.add(".gd");
        blocked.add(".ge");
        blocked.add(".gf");
        blocked.add(".gg");
        blocked.add(".gh");
        blocked.add(".gi");
        blocked.add(".gl");
        blocked.add(".gm");
        blocked.add(".gn");
        blocked.add(".gov");
        blocked.add(".gp");
        blocked.add(".gq");
        blocked.add(".gr");
        blocked.add(".gs");
        blocked.add(".gt");
        blocked.add(".gu");
        blocked.add(".gw");
        blocked.add(".gy");
        blocked.add(".hk");
        blocked.add(".hm");
        blocked.add(".hn");
        blocked.add(".hr");
        blocked.add(".ht");
        blocked.add(".hu");
        blocked.add(".id");
        blocked.add(".ie");
        blocked.add(".il");
        blocked.add(".im");
        blocked.add(".in");
        blocked.add(".info");
        blocked.add(".int");
        blocked.add(".io");
        blocked.add(".iq");
        blocked.add(".ir");
        blocked.add(".is");
        blocked.add(".it");
        blocked.add(".je");
        blocked.add(".jm");
        blocked.add(".jo");
        blocked.add(".jobs");
        blocked.add(".jp");
        blocked.add(".ke");
        blocked.add(".kg");
        blocked.add(".kh");
        blocked.add(".ki");
        blocked.add(".km");
        blocked.add(".kn");
        blocked.add(".kp");
        blocked.add(".kr");
        blocked.add(".kw");
        blocked.add(".ky");
        blocked.add(".kz");
        blocked.add(".la");
        blocked.add(".lb");
        blocked.add(".lc");
        blocked.add(".li");
        blocked.add(".lk");
        blocked.add(".lr");
        blocked.add(".ls");
        blocked.add(".lt");
        blocked.add(".lu");
        blocked.add(".lv");
        blocked.add(".ly");
        blocked.add(".ma");
        blocked.add(".mc");
        blocked.add(".md");
        blocked.add(".me");
        blocked.add(".mg");
        blocked.add(".mh");
        blocked.add(".mil");
        blocked.add(".mk");
        blocked.add(".ml");
        blocked.add(".mm");
        blocked.add(".mn");
        blocked.add(".mo");
        blocked.add(".mobi");
        blocked.add(".mp");
        blocked.add(".mq");
        blocked.add(".mr");
        blocked.add(".ms");
        blocked.add(".mt");
        blocked.add(".mu");
        blocked.add(".museum");
        blocked.add(".mv");
        blocked.add(".mw");
        blocked.add(".mx");
        blocked.add(".my");
        blocked.add(".mz");
        blocked.add(".na");
        blocked.add(".name");
        blocked.add(".nc");
        blocked.add(".ne");
        blocked.add(".net");
        blocked.add(".nf");
        blocked.add(".ng");
        blocked.add(".ni");
        blocked.add(".nl");
        blocked.add(".no");
        blocked.add(".np");
        blocked.add(".nr");
        blocked.add(".nu");
        blocked.add(".nz");
        blocked.add(".om");
        blocked.add(".org");
        blocked.add(".pa");
        blocked.add(".pe");
        blocked.add(".pf");
        blocked.add(".pg");
        blocked.add(".ph");
        blocked.add(".pk");
        blocked.add(".pl");
        blocked.add(".pm");
        blocked.add(".pn");
        blocked.add(".post");
        blocked.add(".pr");
        blocked.add(".pro");
        blocked.add(".ps");
        blocked.add(".pt");
        blocked.add(".pw");
        blocked.add(".py");
        blocked.add(".qa");
        blocked.add(".re");
        blocked.add(".ro");
        blocked.add(".rs");
        blocked.add(".ru");
        blocked.add(".rw");
        blocked.add(".sa");
        blocked.add(".sb");
        blocked.add(".sc");
        blocked.add(".sd");
        blocked.add(".se");
        blocked.add(".sg");
        blocked.add(".sh");
        blocked.add(".si");
        blocked.add(".sj");
        blocked.add(".sk");
        blocked.add(".sl");
        blocked.add(".sm");
        blocked.add(".sn");
        blocked.add(".so");
        blocked.add(".sr");
        blocked.add(".st");
        blocked.add(".su");
        blocked.add(".sv");
        blocked.add(".sx");
        blocked.add(".sy");
        blocked.add(".sz");
        blocked.add(".tc");
        blocked.add(".td");
        blocked.add(".tel");
        blocked.add(".tf");
        blocked.add(".tg");
        blocked.add(".th");
        blocked.add(".tj");
        blocked.add(".tk");
        blocked.add(".tl");
        blocked.add(".tm");
        blocked.add(".tn");
        blocked.add(".to");
        blocked.add(".tp");
        blocked.add(".tr");
        blocked.add(".travel");
        blocked.add(".tt");
        blocked.add(".tv");
        blocked.add(".tw");
        blocked.add(".tz");
        blocked.add(".ua");
        blocked.add(".ug");
        blocked.add(".uk");
        blocked.add(".us");
        blocked.add(".uy");
        blocked.add(".uz");
        blocked.add(".va");
        blocked.add(".vc");
        blocked.add(".ve");
        blocked.add(".vg");
        blocked.add(".vi");
        blocked.add(".vn");
        blocked.add(".vu");
        blocked.add(".wf");
        blocked.add(".ws");
        blocked.add(".xxx");
        blocked.add(".ye");
        blocked.add(".yt");
        blocked.add(".za");
        blocked.add(".zm");
        blocked.add(".zw");
    }
}
