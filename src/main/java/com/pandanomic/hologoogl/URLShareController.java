package com.pandanomic.hologoogl;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class URLShareController extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
			handleSendText(intent);
		}
    }

	void handleSendText(Intent intent) {

		if (!networkAvailable()) {
			Toast.makeText(this, "Not connected to the internet", Toast.LENGTH_LONG).show();
			return;
		}

		String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedURL != null) {
			final NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle("Shortening URL...")
							.setProgress(0, 0, true);

			final int mNotificationId = 001;
			final NotificationManager mNotifyMgr =
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			mNotifyMgr.notify(mNotificationId, mBuilder.build());

			RetrieveShortenedURLTask getURLTask = new RetrieveShortenedURLTask(sharedURL);

			JSONObject result = null;

			try {
				result = getURLTask.execute().get();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			String resultURL = "Done";
			if (result != null) {
				try {
					resultURL = result.getString("id");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


			final String finalResultURL = resultURL;
			BroadcastReceiver brCopy = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					ClipboardManager clipboard = (ClipboardManager)
							getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("Shortened URL", finalResultURL);
					clipboard.setPrimaryClip(clip);
					mNotifyMgr.cancel(mNotificationId);
					Toast.makeText(getBaseContext(), "Copied!", Toast.LENGTH_SHORT).show();
					unregisterReceiver(this);
					finish();
				}
			};

			IntentFilter intentFilter = new IntentFilter("com.pandanomic.ACTION_COPY");
			getBaseContext().registerReceiver(brCopy, intentFilter);

			Intent copy = new Intent("com.pandanomic.ACTION_COPY");
			PendingIntent piCopy = PendingIntent.getBroadcast(getBaseContext(), 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);

			mBuilder.addAction(R.drawable.ic_menu_copy, "Copy", piCopy);

			mBuilder.setProgress(0, 0, false);
			mBuilder.setContentTitle(resultURL);
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
		}
	}

	public class RetrieveShortenedURLTask extends AsyncTask<String, Void, JSONObject> {
		private Exception exception;
		private String sharedURL;
		private JSONObject result;

		public RetrieveShortenedURLTask(String url) {
			sharedURL = url;
		}

		protected JSONObject doInBackground(String... params) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost("https://www.googleapis.com/urlshortener/v1/url");
				post.setEntity(new StringEntity("{\"longUrl\": \"" + sharedURL + "\"}"));
				post.setHeader("Content-Type", "application/json");

				HttpResponse response = client.execute(post);

				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				JSONTokener tokener = new JSONTokener(builder.toString());

				return new JSONObject(tokener);
			}
			catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(JSONObject obj) {
			// do stuff
			Log.d("googl", obj.toString());
			result = obj;
		}
	}

	public boolean networkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
