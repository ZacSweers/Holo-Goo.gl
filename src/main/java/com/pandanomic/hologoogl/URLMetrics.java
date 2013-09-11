package com.pandanomic.hologoogl;

import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by pandanomic on 9/3/13.
 */
public class URLMetrics {

    private String shortURL;
    private String longURL;
    private int clicks;
    private String dateCreated;
    private JSONObject analytics;
    private String LOGTAG = "URLMetrics";

    public URLMetrics(JSONObject object) {
        Log.i(LOGTAG, object.toString());
        try {
            this.shortURL = object.getString("id");
            this.longURL = object.getString("longUrl");
            String date = object.getString("created").substring(0,10);
            date = date.replaceAll("-", "");
            int dateInt = Integer.parseInt(date);
            int year = dateInt / 10000;
            int month = (dateInt % 10000) / 100;
            int day = dateInt % 100;
            this.dateCreated = month + "/" + day + "/" + year;
            this.analytics = object.getJSONObject("analytics");
            JSONObject allTime = analytics.getJSONObject("allTime");
            this.clicks = allTime.getInt("shortUrlClicks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAnalytics(JSONObject analytics) {
        this.analytics = analytics;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLongURL(String url) {
        this.longURL = url;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public int getClicks() {
        return this.clicks;
    }

    public String getShortURL() {
        return this.shortURL;
    }

    public String getLongURL() {
        return this.longURL;
    }
}
