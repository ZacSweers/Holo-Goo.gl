package com.pandanomic.hologoogl;

/**
 * Created by pandanomic on 9/3/13.
 */
public class URLMetrics {

    private String shortURL;
    private String longURL;
    private int clicks;
    private String dateCreated;

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public URLMetrics(String shortURL) {
        this.shortURL = shortURL;
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
