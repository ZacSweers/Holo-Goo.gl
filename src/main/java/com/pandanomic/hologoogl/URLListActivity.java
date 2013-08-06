package com.pandanomic.hologoogl;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * An activity representing a list of URLs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link URLDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link URLListFragment} and the item details
 * (if present) is a {@link URLDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link URLListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class URLListActivity extends FragmentActivity
        implements URLListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_list);

        if (findViewById(R.id.url_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((URLListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.url_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
			handleSendText(intent);
		}
    }

    /**
     * Callback method from {@link URLListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(URLDetailFragment.ARG_ITEM_ID, id);
            URLDetailFragment fragment = new URLDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.url_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, URLDetailActivity.class);
            detailIntent.putExtra(URLDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

	void handleSendText(Intent intent) {
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

			// TODO: get shortened URL

//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				public void run() {
//					mBuilder.setProgress(0, 0, false);
//					mBuilder.setContentTitle("Done").setContentText(null);
//					mNotifyMgr.notify(mNotificationId, mBuilder.build());
//				}
//			}, 5000);

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
}
