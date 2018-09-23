package me.nikl.wikipedia.language;

import me.nikl.wikipedia.Utility.FileUtility;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the file configurations.
 *
 * Provides methods to load messages from the files.
 *
 * @author Niklas Eicker
 */
public class Language {
    private static final String FOLDER = "language";

    protected Plugin plugin;
    protected FileConfiguration config;
    private String fileName;
    private File languageFile;
    private FileConfiguration defaultLanguage;
    private FileConfiguration language;

    protected Map<String, String> messages = new HashMap<>();
    protected Map<String, List<String>> lists = new HashMap<>();

    public Language(Plugin plugin){
        this.plugin = plugin;
        reload();
    }

    /**
     * Reload all messages
     */
    public void reload(){
        messages.clear();
        lists.clear();
        FileUtility.copyDefaultLanguageFiles();
        this.config = plugin.getConfig();
        getLangFile();
    }

    /**
     * Try loading the language file specified in the
     * configuration file of the plugin.
     *
     * The required path is 'langFile'.
     */
    protected void getLangFile() {
        // load default language
        loadDefaultLanguage();
        if(!checkFileName()) return;
        // create language file
        languageFile = new File(plugin.getDataFolder().toString() + File.separatorChar + FOLDER + File.separatorChar + fileName);
        if(!checkFile()) return;
        // File exists
        loadLanguage();

        return;
    }

    private boolean checkFile() {
        if(!languageFile.exists()){
            plugin.getLogger().warning("The language-file '" + fileName + "' does not exist!");
            plugin.getLogger().warning("Falling back to the default file...");
            language = defaultLanguage;
            return false;
        }
        return true;
    }

    private void loadLanguage() {
        try {
            language = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(languageFile), "UTF-8"));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
            language = defaultLanguage;
        }
    }

    private boolean checkFileName() {
        fileName = config.getString("languageFile");
        if(fileName != null && (fileName.equalsIgnoreCase("default") || fileName.equalsIgnoreCase("default.yml"))) {
            language = defaultLanguage;
            return false;
        }
        if(fileName == null){
            String path = "'config.yml'";
            plugin.getLogger().warning("Language file is not specified or not valid.");
            plugin.getLogger().warning("Should be set in " + path + " as value of 'languageFile'");
            plugin.getLogger().warning("Falling back to the default file...");
            language = defaultLanguage;
            return false;
        }
        if(!fileName.endsWith(".yml")){
            plugin.getLogger().warning("Missing '.yml' ending for your configured language file.");
            plugin.getLogger().warning("Did you forget it? Trying with modified filename...");
            fileName = fileName + ".yml";
        }
        return true;
    }

    protected void loadDefaultLanguage() {
        try {
            String defaultLangName = "language/lang_en.yml";
            defaultLanguage = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(defaultLangName), "UTF-8"));
        } catch (UnsupportedEncodingException e2) {
            plugin.getLogger().warning("Failed to load default language file!");
            e2.printStackTrace();
        }
    }


    /**
     * Find all string messages that are missing in the language file.
     *
     * This method compares all message keys that hold a String in the default english
     * file with all set keys in the used language file. All missing keys are
     * collected and returned.
     *
     * @return list of all missing keys (can be empty list)
     */
    public List<String> findMissingStringMessages(){
        List<String> toReturn = new ArrayList<>();
        if(defaultLanguage.equals(language)) return toReturn;
        for(String key : defaultLanguage.getKeys(true)){
            if(defaultLanguage.isString(key)){
                if(!language.isString(key)){
                    // there is a message missing
                    toReturn.add(key);
                }
            }
        }
        return toReturn;
    }

    /**
     * Find all string messages that are missing in the language file.
     *
     * This method compares all message keys that hold a list in the default english
     * file with all set keys in the used language file. All missing keys are
     * collected and returned.
     *
     * @return list of all missing keys (can be empty list)
     */
    public List<String> findMissingListMessages(){
        List<String> toReturn = new ArrayList<>();
        if(defaultLanguage.equals(language)) return toReturn;
        for(String key : defaultLanguage.getKeys(true)){
            if (defaultLanguage.isList(key)){
                if(!language.isList(key)){
                    // there is a list missing
                    toReturn.add(key);
                }
            }
        }
        return toReturn;
    }


    /**
     * Load list messages from the language file
     *
     * If the requested path is not valid for the chosen
     * language file the corresponding list from the default
     * file is returned.
     * ChatColor can be translated here.
     * @param path path to the message
     * @return message
     */
    public List<String> getStringList(String path) {
        List<String> toReturn = lists.get(path);
        if(toReturn != null) return new ArrayList<>(toReturn);
        // load from default file if path is not valid
        if(!language.isList(path)){
            toReturn = defaultLanguage.getStringList(path);
            if(toReturn != null){
                for(int i = 0; i<toReturn.size(); i++){
                    toReturn.set(i, ChatColor.translateAlternateColorCodes('&',toReturn.get(i)));
                }
            }
            lists.put(path, toReturn);
            return new ArrayList<>(toReturn);
        }

        // load from language file
        toReturn = language.getStringList(path);
        if(toReturn != null) {
            for (int i = 0; i < toReturn.size(); i++) {
                toReturn.set(i, ChatColor.translateAlternateColorCodes('&', toReturn.get(i)));
            }
        }
        lists.put(path, toReturn);
        return new ArrayList<>(toReturn);
    }

    /**
     * Get a message from the language file
     *
     * If the requested path is not valid for the
     * configured language file the corresponding
     * message from the default file is returned.
     * ChatColor is translated when reading the message.
     * @param path path to the message
     * @return message
     */
    public String getString(String path) {
        String toReturn = messages.get(path);
        if(toReturn != null) return toReturn;
        if(!language.isString(path)){
            toReturn = ChatColor.translateAlternateColorCodes('&', defaultLanguage.getString(path));
            messages.put(path, toReturn);
            return toReturn;
        }
        toReturn = ChatColor.translateAlternateColorCodes('&', language.getString(path));
        messages.put(path, toReturn);
        return toReturn;
    }
}
