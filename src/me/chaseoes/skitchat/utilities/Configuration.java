package me.chaseoes.skitchat.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.chaseoes.skitchat.SkitChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {

    private FileConfiguration friendsConfig = null;
    private File friendsConfigFile = null;
    private FileConfiguration ignoresConfig = null;
    private File ignoresConfigFile = null;
    private FileConfiguration playerdataConfig = null;
    private File playerdataConfigFile = null;
    private FileConfiguration channelsConfig = null;
    private File channelsConfigFile = null;
    public SkitChat plugin;
    static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    public void setup(SkitChat p) {
        this.plugin = p;
    }

    public void reloadAll() {
        getInstance().plugin.reloadConfig();
        reloadFriendsConfig();
        reloadIgnoresConfig();
        reloadPlayerdataConfig();
        saveFriendsConfig();
        saveIgnoresConfig();
        savePlayerdataConfig();
        getInstance().plugin.saveConfig();
    }

    public void reloadFriendsConfig() {
        if (this.friendsConfigFile == null) {
            this.friendsConfigFile = new File(getInstance().plugin.getDataFolder(), "friends.yml");
        }
        this.friendsConfig = YamlConfiguration.loadConfiguration(this.friendsConfigFile);
        InputStream defConfigStream = getInstance().plugin.getResource("friends.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.friendsConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getFriendsConfig() {
        if (this.friendsConfig == null) {
            reloadFriendsConfig();
        }
        return this.friendsConfig;
    }

    public void saveFriendsConfig() {
        if ((this.friendsConfig == null) || (this.friendsConfigFile == null)) {
            return;
        }
        try {
            this.friendsConfig.save(this.friendsConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[SkitChat] Could not save config to " + this.friendsConfigFile, ex);
        }
    }

    public void reloadIgnoresConfig() {
        if (this.ignoresConfigFile == null) {
            this.ignoresConfigFile = new File(getInstance().plugin.getDataFolder(), "ignores.yml");
        }
        this.ignoresConfig = YamlConfiguration.loadConfiguration(this.ignoresConfigFile);
        InputStream defConfigStream = getInstance().plugin.getResource("ignores.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.ignoresConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getIgnoresConfig() {
        if (this.ignoresConfig == null) {
            reloadIgnoresConfig();
        }
        return this.ignoresConfig;
    }

    public void saveIgnoresConfig() {
        if ((this.ignoresConfig == null) || (this.ignoresConfigFile == null)) {
            return;
        }
        try {
            this.ignoresConfig.save(this.ignoresConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[SkitChat] Could not save config to " + this.ignoresConfigFile, ex);
        }
    }

    public void reloadPlayerdataConfig() {
        if (this.playerdataConfigFile == null) {
            this.playerdataConfigFile = new File(getInstance().plugin.getDataFolder(), "playerdata.yml");
        }
        this.playerdataConfig = YamlConfiguration.loadConfiguration(this.playerdataConfigFile);
        InputStream defConfigStream = getInstance().plugin.getResource("playerdata.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.playerdataConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getPlayerdataConfig() {
        if (this.playerdataConfig == null) {
            reloadPlayerdataConfig();
        }
        return this.playerdataConfig;
    }

    public void savePlayerdataConfig() {
        if ((this.playerdataConfig == null) || (this.playerdataConfigFile == null)) {
            return;
        }
        try {
            this.playerdataConfig.save(this.playerdataConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[SkitChat] Could not save config to " + this.playerdataConfigFile, ex);
        }
    }

    public void reloadChannelsConfig() {
        if (this.channelsConfigFile == null) {
            this.channelsConfigFile = new File(getInstance().plugin.getDataFolder(), "channels.yml");
        }
        this.playerdataConfig = YamlConfiguration.loadConfiguration(this.channelsConfigFile);
        InputStream defConfigStream = getInstance().plugin.getResource("channels.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.channelsConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getChannelsConfig() {
        if (this.channelsConfig == null) {
            reloadChannelsConfig();
        }
        return this.channelsConfig;
    }

    public void saveChannelsConfig() {
        if ((this.channelsConfig == null) || (this.channelsConfigFile == null)) {
            return;
        }
        try {
            this.playerdataConfig.save(this.channelsConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[SkitChat] Could not save config to " + this.channelsConfigFile, ex);
        }
    }
}