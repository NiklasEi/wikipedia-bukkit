package me.nikl.wikipedia;

import me.nikl.wikipedia.Utility.WikipediaProvider;
import me.nikl.wikipedia.language.Language;
import me.nikl.wikipedia.language.Messenger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author Niklas Eicker
 */
public class Wikipedia extends JavaPlugin {
    private Language language;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        if(!reloadConfiguration()){
            getLogger().severe(" Failed to load config file! Disabling Plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (config.isString("wikipediaLanguage")) WikipediaProvider.setLanguage(config.getString("wikipediaLanguage"));
        getCommand("wikipedia").setExecutor(new WikipediaCommand(this));
        if (language == null) {
            language = new Language(this);
        } else {
            language.reload();
        }
        Messenger.reload(language);

        if(config.getBoolean("bStats", true)) {
            Metrics metrics = new Metrics(this);
        }
    }

    public boolean reloadConfiguration(){
        // save the default configuration file if the file does not exist
        File con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
        if(!con.exists()){
            this.saveResource("config.yml", false);
        }

        // reload config
        try {
            this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(con), "UTF-8"));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }
}
