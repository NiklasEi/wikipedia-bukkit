package me.nikl.wikipedia.Utility;

import me.nikl.wikipedia.WikipediaRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Niklas Eicker
 */
public class WikipediaProvider {
    private static String currentWikipediaRestV1 = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static String wikipediaRestV1 = "https://%lang%.wikipedia.org/api/rest_v1/page/summary/";
    private static final String CHARSET = StandardCharsets.UTF_8.name();

    public static void setLanguage(String language) {
        currentWikipediaRestV1 = wikipediaRestV1.replace("%lang%", language);
    }

    public static void getRequest(WikipediaRequest wikipediaRequest) {
        JSONObject requestJson;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(currentWikipediaRestV1 + URLEncoder.encode(wikipediaRequest.getSearch()
                    , CHARSET).replace("+", "%20")).openConnection();
            connection.setRequestProperty("Accept-Charset", CHARSET);
            InputStream response = connection.getInputStream();
            JSONParser jsonParser = new JSONParser();
            requestJson = (JSONObject) jsonParser.parse(new InputStreamReader(response, CHARSET));
            wikipediaRequest.onSuccess(requestJson);
        } catch (IOException | ParseException e) {
            wikipediaRequest.onError();
        }
    }
}
