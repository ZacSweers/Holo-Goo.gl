package com.pandanomic.hologoogl.URLContent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ShortenedURLContent {

    /**
     * An array of sample (URLContent) items.
     */
    public static List<ShortenedURLItem> ITEMS = new ArrayList<ShortenedURLItem>();

    /**
     * A map of sample (URLContent) items, by ID.
     */
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

    /**
     * A URLContent item representing a piece of content.
     */
    public static class ShortenedURLItem {
        public String id;
        public String content;
		public String metrics = "Here's where you'd see URL metrics like clicks";
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

		public String getMetrics() {
			return this.metrics;
		}
    }
}
