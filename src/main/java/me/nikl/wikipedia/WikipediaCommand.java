package me.nikl.wikipedia;

import me.nikl.wikipedia.utility.WikipediaProvider;
import me.nikl.wikipedia.language.MessageKey;
import me.nikl.wikipedia.language.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class WikipediaCommand implements CommandExecutor {
    private Wikipedia wikipedia;
    private List<String> blockedWorlds = new ArrayList<>();

    public WikipediaCommand(Wikipedia wikipedia) {
        this.wikipedia = wikipedia;
        FileConfiguration config = wikipedia.getConfig();
        if (config.isList("settings.blockedWorlds")) {
            blockedWorlds.addAll(config.getStringList("settings.blockedWorlds"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("wikipedia.use")) {
            Messenger.sendMessage(sender, MessageKey.NO_PERMISSION);
            return true;
        }
        if (sender instanceof Player && blockedWorlds.contains(((Player) sender).getWorld().getName())) {
            Messenger.sendMessage(sender, MessageKey.BLOCKED_WORLD);
            return true;
        }
        if (args == null || args.length == 0) {
            Messenger.sendMessage(sender, MessageKey.NO_QUERY_GIVEN);
            return true;
        }
        String request = String.join(" ", Arrays.asList(args));

        new BukkitRunnable() {
            @Override
            public void run() {
                WikipediaProvider.getRequest(new WikipediaRequest(sender, request) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        handleJsonResponse(sender, request, response);
                    }

                    @Override
                    public void onError() {
                        handleError(sender, request);
                    }
                });
            }
        }.runTaskAsynchronously(wikipedia);
        return true;
    }

    private void handleJsonResponse(CommandSender sender, String query, JSONObject response) {
        if (sender == null) return;
        Object descriptionObj = response.get("description");
        Object displayTitleObj = response.get("title");
        Object extractObj = response.get("extract");
        if (extractObj == null) {
            Messenger.sendMessage(sender, MessageKey.NO_ARTICLE_FOUND, Collections.singletonMap("%query%", query));
            return;
        }
        Map<String, String> context = new HashMap<>();
        // use language messages!
        if (descriptionObj != null && displayTitleObj != null) {
            context.put("%title%", (String) displayTitleObj);
            context.put("%description%", (String) descriptionObj);
        }
        context.put("%extract%", (String) extractObj);
        Messenger.sendList(sender, MessageKey.RESPONSE, context);
    }

    private void handleError(CommandSender sender, String query) {
        if (sender == null) return;
        Messenger.sendMessage(sender, MessageKey.NO_ARTICLE_FOUND, Collections.singletonMap("%query%", query));
    }
}
