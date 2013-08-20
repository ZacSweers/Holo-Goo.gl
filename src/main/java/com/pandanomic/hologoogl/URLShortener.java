package com.pandanomic.hologoogl;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class URLShortener {

    private final String LOGTAG = "URLShortener";

    public String generate(String input) {
        JSONObject result;
        String resultURL = null;
        try {
            result = new PostTask().execute(input).get(5000, TimeUnit.MILLISECONDS);

            if (result == null) {
                Log.e(LOGTAG, "Error retrieving data");
                return null;
            }

            resultURL = result.getString("id");

        } catch (InterruptedException e) {
            Log.e(LOGTAG, "Interrupted exception");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e(LOGTAG, "Execution exception");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.e(LOGTAG, "Timeout exception");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing result");
            e.printStackTrace();
        }

        if (resultURL == null) {
            Log.e(LOGTAG, "Failed");
            return null;
        }

        return resultURL;
    }
}
