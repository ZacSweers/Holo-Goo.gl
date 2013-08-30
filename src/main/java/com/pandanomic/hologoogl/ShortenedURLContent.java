package com.pandanomic.hologoogl;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pandanomic on 8/28/13.
 */
public class ShortenedURLContent {

    public static List<ShortenedURLItem> ITEMS = new ArrayList<ShortenedURLItem>();

    public static Map<String, ShortenedURLItem> ITEM_MAP = new HashMap<String, ShortenedURLItem>();

    static {
        // Add 3 sample items.
        addItem(new ShortenedURLItem("1", "goo.gl/SYFV4"));
        addItem(new ShortenedURLItem("2", "goo.gl/4DR2e"));
        addItem(new ShortenedURLItem("3", "goo.gl/0XsgU"));
    }

    private static void addItem(ShortenedURLItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class ShortenedURLItem {
        public String id;
        public String content;
        public String shortenedURL;
        public int clicks;
        public JSONObject URLJSONObject;

        public ShortenedURLItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public ShortenedURLItem(JSONObject object) {

        }

        @Override
        public String toString() {
            return content;
        }
    }
}
