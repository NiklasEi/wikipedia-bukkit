package me.nikl.wikipedia.language;

/**
 * @author Niklas Eicker
 */
public enum MessageKey {
    PREFIX("prefix"),
    NAME("name"),
    NO_PERMISSION("noPermission"),
    NO_QUERY_GIVEN("noQueryGiven"),
    NO_ARTICLE_FOUND("noArticleFound"),
    BLOCKED_WORLD("blockedWorld"),
    RESPONSE("response", true);


    private String path;
    private boolean list;
    MessageKey(String path, boolean list){
        this.path = path;
        this.list = list;
    }

    MessageKey(String path){
        this(path, false);
    }

    public String getPath(){
        return this.path;
    }

    public boolean isList(){
        return this.list;
    }
}
