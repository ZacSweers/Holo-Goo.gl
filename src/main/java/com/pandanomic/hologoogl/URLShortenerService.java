package com.pandanomic.hologoogl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class URLShortenerService  extends Service {

    private Intent originalIntent;
    private NotificationManager mNotifyMgr;
    private NotificationCompat.Builder mBuilder;
    private final int mNotificationId = 001;

    @Override
    public int onStartCommand(Intent intent, int flags, int stardId) {
        originalIntent = intent;
        String sharedURL = intent.getStringExtra("URL");

        if (!Patterns.WEB_URL.matcher(sharedURL).matches()) {
            // Validate URL pattern
            Toast.makeText(getBaseContext(), "Please enter a valid URL!", Toast.LENGTH_LONG).show();
            stopService(originalIntent);
        }

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Shortening URL...")
                        .setProgress(0, 0, true);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        ShortenURLTask shortenURLTask;

        AuthPreferences authPreferences = new AuthPreferences(this);

        if (authPreferences.loggedIn()) {
            shortenURLTask = new ShortenURLTask(authPreferences.getToken());
        } else {
            shortenURLTask = new ShortenURLTask();
        }

        shortenURLTask.execute(sharedURL);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleNotification(String shortenedURL) {
        final String finalResultURL = shortenedURL;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("com.pandanomic.ACTION_COPY")) {
                    copyURL(finalResultURL);
                }
                if (action.equals("com.pandanomic.ACTION_SHARE")) {
                    shareURL(finalResultURL);
                }
                mNotifyMgr.cancel(mNotificationId);
                unregisterReceiver(this);
            }
        };

        IntentFilter intentFilterCopy = new IntentFilter("com.pandanomic.ACTION_COPY");
        getBaseContext().registerReceiver(broadcastReceiver, intentFilterCopy);
        Intent copy = new Intent("com.pandanomic.ACTION_COPY");
        PendingIntent piCopy = PendingIntent.getBroadcast(getBaseContext(), 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(R.drawable.ic_menu_copy, "Copy", piCopy);

        IntentFilter intentFilterShare = new IntentFilter("com.pandanomic.ACTION_SHARE");
        getBaseContext().registerReceiver(broadcastReceiver, intentFilterShare);
        Intent share = new Intent("com.pandanomic.ACTION_SHARE");
        PendingIntent piShare = PendingIntent.getBroadcast(getBaseContext(), 0, share, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(R.drawable.ic_social_share, "Share", piShare);

        mBuilder.setProgress(0, 0, false);
        mBuilder.setContentTitle("URL Shortened!");
        mBuilder.setContentText(finalResultURL);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void copyURL(String url) {
        ClipboardManager clipboard = (ClipboardManager)
                getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Shortened URL", url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
        stopService(originalIntent);
    }

    private void shareURL(String url) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Shared from Holo Goo.gl");
        getBaseContext().startActivity(intent);
        stopService(originalIntent);
    }

    private class ShortenURLTask extends AsyncTask<String, Void, JSONObject> {

        private String token;

        public ShortenURLTask() {

        }

        public ShortenURLTask(String authToken) {
            this.token = authToken;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String sharedURL = params[0];

            JSONObject results;

            Log.d("googl", "Fetching data");
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                DefaultHttpClient client = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost("https://www.googleapis.com/urlshortener/v1/url");
                post.setEntity(new StringEntity("{\"longUrl\": \"" + sharedURL + "\"}"));
                post.setHeader("Content-Type", "application/json");
                if (token != null) {
                    post.setHeader("Authorization", "Bearer " + token);
                }

                HttpResponse response = client.execute(post);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }

                results = new JSONObject(new JSONTokener(builder.toString()));
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

                Log.e("ShortenURLTask", errorMessage);
                return null;
            }
            return results;
        }

        /**
         * Post-execution stuff
         * @param result JSONObject result received from Goo.gl
         */
        protected void onPostExecute(JSONObject result) {
            String shortenedURL = "none";

            try {
                shortenedURL = result.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handleNotification(shortenedURL);
        }
    }
}
