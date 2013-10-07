package com.pandanomic.hologoogl;

import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by pandanomic on 9/3/13.
 */
@Table(name = "URLObject")
public class URLObject extends Model {

    @Column(name = "ShortURL")
    private String shortURL;

    @Column(name = "LongURL")
    private String longURL;

    @Column(name = "Clicks")
    private int clicks;

    @Column(name = "DateCreated")
    private String dateCreated;

    @Column(name = "Analytics")
    private JSONObject analytics;

    @Column(name = "LogTag")
    private String LOGTAG = "URLObject";

    @Column(name = "Container")
    public URLObjectsContainer urlObjectsContainer;

    public URLObject(JSONObject object) {
//        Log.i(LOGTAG, object.toString());
        super();
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

        }
    }

    public URLObject(String shortURL) {
        super();
        this.shortURL = shortURL;
    }

    public void setAnalytics(JSONObject object) {
        Log.i(LOGTAG, object.toString());
        try {
            this.shortURL = object.getString("id");
            Log.i(LOGTAG, "Short URL is " + shortURL);
            this.longURL = object.getString("longUrl");
//            String date = object.getString("created").substring(0,10);
//            date = date.replaceAll("-", "");
//            int dateInt = Integer.parseInt(date);
//            int year = dateInt / 10000;
//            int month = (dateInt % 10000) / 100;
//            int day = dateInt % 100;
//            this.dateCreated = month + "/" + day + "/" + year;
            this.analytics = object.getJSONObject("analytics");
            JSONObject allTime = analytics.getJSONObject("allTime");
            this.clicks = allTime.getInt("shortUrlClicks");
            Log.i(LOGTAG, "Clicks - " + clicks);
        } catch (JSONException e) {

        }
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

    public void update() {
        new refreshMetrics().execute();
    }

    private class refreshMetrics extends AsyncTask<String, Void, Boolean> {
        private final String LOGTAG = "RefreshHistory";
        private String GETURL = "https://www.googleapis.com/urlshortener/v1/url";
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            GETURL += "?shortUrl=" + shortURL;
            GETURL += "&projection=FULL";

            JSONObject results;

            Log.d("googl", "Fetching data");
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                DefaultHttpClient client = new DefaultHttpClient(httpParams);
                HttpGet get = new HttpGet(GETURL);

                HttpResponse response = client.execute(get);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }

                results = new JSONObject(new JSONTokener(builder.toString()));

                if (results.has("error")) {
                    Log.e("URLObject", "history refresh failed");
                    return false;
                }

                updateData(results);

                Log.d("hologoogl", results.toString());
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = "Error: ";
                if (e instanceof UnsupportedEncodingException) {
                    errorMessage += "Encoding exception";
                } else if (e instanceof ClientProtocolException) {
                    errorMessage += "POST exception";
                } else if (e instanceof IOException) {
                    errorMessage += "IO Exception in parsing response";
                } else {
                    errorMessage += "JSON parsing exception";
                }

                Log.e("googl:retrieveURLTask", errorMessage);
                return false;
            }

            return true;
        }

        /**
         * Post-execution stuff
         * @param result JSONObject result received from Goo.gl
         */
        protected void onPostExecute(Boolean result) {
        }

        protected void updateData(JSONObject data) {
            try {
                shortURL = data.getString("id");
                Log.i(LOGTAG, "Short URL is " + shortURL);
                longURL = data.getString("longUrl");
//            String date = object.getString("created").substring(0,10);
//            date = date.replaceAll("-", "");
//            int dateInt = Integer.parseInt(date);
//            int year = dateInt / 10000;
//            int month = (dateInt % 10000) / 100;
//            int day = dateInt % 100;
//            this.dateCreated = month + "/" + day + "/" + year;
                analytics = data.getJSONObject("analytics");
                JSONObject allTime = analytics.getJSONObject("allTime");
                clicks = allTime.getInt("shortUrlClicks");
                Log.i(LOGTAG, "Clicks - " + clicks);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
