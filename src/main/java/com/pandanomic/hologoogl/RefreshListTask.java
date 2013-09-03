package com.pandanomic.hologoogl;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class RefreshListTask extends AsyncTask<String, Void, JSONObject> {
    private URLListActivityold parentActivity;
    private ProgressDialog progressDialog;
    private String GETURL = "https://www.googleapis.com/urlshortener/v1/url/history";
    private final String LOGTAG = "RefreshHistory";

    public RefreshListTask(URLListActivityold activity) {
        parentActivity = activity;
        progressDialog = new ProgressDialog(parentActivity);
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("Retrieving History");
        this.progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String authToken;

        if (!networkAvailable()) {
            Toast.makeText(parentActivity, "Not connected to the internet", Toast.LENGTH_LONG).show();
            Log.e(LOGTAG, "No connection");
            return null;
        }

        authToken = params[0];

        JSONObject results;

        Log.d(LOGTAG, "Fetching data");
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);

            DefaultHttpClient client = new DefaultHttpClient(httpParams);
            HttpGet get = new HttpGet(GETURL);

            get.setHeader("Authorization", "Bearer " + authToken);

            Log.d(LOGTAG, "Requesting");
            HttpResponse response = client.execute(get);

            if (response.getStatusLine().getStatusCode() == 404) {
                Log.e(LOGTAG, "404 Not found");
                return null;
            }

            Log.d(LOGTAG, "Parsing response");
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }

            results = new JSONObject(new JSONTokener(builder.toString()));
            Log.d(LOGTAG, "Finished parsing");
        }
        catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error: ";
            if (e instanceof UnsupportedEncodingException) {
                errorMessage += "Encoding exception";
            }
            else if (e instanceof ClientProtocolException) {
                errorMessage += "POST exception";
            }
            else if (e instanceof IOException) {
                errorMessage += "IO Exception in parsing response";
            }
            else {
                errorMessage += "JSON parsing exception";
            }

            Log.e("history", errorMessage);
            return null;
        }
        return results;
    }

    /**
     * Post-execution stuff
     * @param result JSONObject result received from Goo.gl
     */
    protected void onPostExecute(JSONObject result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        parentActivity.refreshCallback(result);
    }

    private boolean networkAvailable() {
        Log.d(LOGTAG, "Checking network connection");
        ConnectivityManager connectivityManager = (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
