package me.nikl.wikipedia.language;

import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class Messenger {
    private static Language language;
    private static String prefix; // get once since used in every message...

    public static void reload(Language language) {
        Messenger.language = language;
        Messenger.prefix = language.getString(MessageKey.PREFIX.getPath());
    }

    public static void sendMessage(CommandSender receiver, MessageKey key){
        sendMessage(receiver, key, null);
    }

    public static void sendMessage(CommandSender receiver, MessageKey key, @Nullable Map<String, String> context){
        if(key.isList()) throw new IllegalArgumentException("MessageKey cannot be a list!");
        receiver.sendMessage(prefix + createMessage(key, context));
    }

    public static void sendList(CommandSender receiver, MessageKey key){
        sendList(receiver, key, null);
    }

    public static void sendList(CommandSender receiver, MessageKey key, @Nullable Map<String, String> context){
        if(!key.isList()) throw new IllegalArgumentException("MessageKey has to be a list!");
        List<String> list = language.getStringList(key.getPath());
        sendList(receiver, list, context);
    }

    public static void sendList(CommandSender receiver, List<String> list, @Nullable Map<String, String> context){
        if(context != null) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, handlePlaceHolders(list.get(i), context));
            }
        }
        for (String message : list) {
            receiver.sendMessage(prefix + message);
        }
    }

    public static String createMessage(MessageKey key, @Nullable Map<String, String> context) {
        if (key.isList()) throw new IllegalArgumentException("Expected String key, not List key");
        String message = language.getString(key.getPath());
        if(context != null) message = handlePlaceHolders(message, context);
        return message;
    }

    public static List<String> createList(MessageKey key, @Nullable Map<String, String> context) {
        if (!key.isList()) throw new IllegalArgumentException("Expected List key, not String key");
        List<String> message = language.getStringList(key.getPath());
        if(context != null) {
            for (int i = 0; i < message.size(); i++) {
                message.set(i, handlePlaceHolders(message.get(i), context));
            }
        }
        return message;
    }

    private static String handlePlaceHolders(String message, Map<String, String> context) {
        for(String toReplace : context.keySet()){
            message = message.replace(toReplace, context.get(toReplace));
        }
        // ToDo: PlaceholderAPI support?
        return message;
    }

    public static String getString(MessageKey key) {
        if(key.isList()) throw new IllegalArgumentException("Passed key cannot be a list!");
        return language.getString(key.getPath());
    }
}