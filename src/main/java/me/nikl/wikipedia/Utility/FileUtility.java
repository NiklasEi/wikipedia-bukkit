package me.nikl.wikipedia.Utility;

import me.nikl.wikipedia.Wikipedia;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Niklas Eicker
 *
 * Utility class for language related stuff
 */
public class FileUtility {
    /**
     * Copy all default language files to the plugin folder
     *
     * This method checks for every .yml in the language folder
     * whether it is already present in the plugins language folder.
     * If not it is copied.
     */
    public static void copyDefaultLanguageFiles(){
        URL main = Wikipedia.class.getResource("Wikipedia.class");
        try {
            JarURLConnection connection = (JarURLConnection) main.openConnection();
            JarFile jar = new JarFile(connection.getJarFileURL().getFile());
            // ToDo: need better way to get the plugin (not using the name)
            Plugin core = Bukkit.getPluginManager().getPlugin("Wikipedia");
            for (Enumeration list = jar.entries(); list.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) list.nextElement();
                if(entry.getName().split("/")[0].equals("language")) {
                    String[] pathParts = entry.getName().split("/");
                    if (pathParts.length < 2 || !entry.getName().endsWith(".yml")){
                        continue;
                    }
                    File file = new File(core.getDataFolder().toString() + File.separatorChar + entry.getName());
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        core.saveResource(entry.getName(), false);
                    }
                }
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
