package me.nikl.wikipedia;

import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;

/**
 * @author Niklas Eicker
 */
public abstract class WikipediaRequest {
    private CommandSender player;
    private String search;

    public WikipediaRequest(CommandSender player, String search) {
        this.player = player;
        this.search = search;
    }

    public CommandSender getPlayer() {
        return player;
    }

    public String getSearch() {
        return search;
    }

    public abstract void onSuccess(JSONObject response);
    public abstract void onError();
}
