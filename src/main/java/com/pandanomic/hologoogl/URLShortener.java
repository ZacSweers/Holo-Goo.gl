package com.pandanomic.hologoogl;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class URLShortener {

    Activity parent;

    public URLShortener(Activity activity) {
        parent = activity;
    }

    public String generate(String input) {
        JSONObject result;
        String resultURL = null;
        try {
            result = new PostTask(parent).execute(input).get(5000, TimeUnit.MILLISECONDS);

            if (result == null) {
                Toast.makeText(parent, "Error retrieving data", Toast.LENGTH_LONG).show();
                return null;
            }

            resultURL = result.getString("id");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(parent, "Timeout", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(parent, "Error Parsing Result", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (resultURL == null) {
            Toast.makeText(parent, "Failed", Toast.LENGTH_LONG).show();
            return null;
        }

        return resultURL;
    }
}
