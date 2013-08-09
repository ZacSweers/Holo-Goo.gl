package com.pandanomic.hologoogl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import java.net.SocketTimeoutException;

public class ShortenURLResult extends Activity {

	private String finalShortenedURL = null;
	protected ProgressBar progressBar;
	protected TextView shortenedURLView;
    protected TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortenurlresult);

        progressBar = (ProgressBar) findViewById(R.id.URL_shortening_progressBar);
        shortenedURLView = (TextView) findViewById(R.id.shortenedURL);
        statusText = (TextView) findViewById(R.id.URL_shortening_status);

		progressBar.setVisibility(View.VISIBLE);
		shortenedURLView.setVisibility(View.GONE);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if ((Intent.ACTION_SEND.equals(action) || "true".equals(intent.getStringExtra("URL")))&& "text/plain".equals(type)) {
			Log.d("googl", "handling intent");
			String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (sharedURL != null) {
				RetrieveURLTask retrieveURLTask = new RetrieveURLTask();
				retrieveURLTask.execute(sharedURL);
			}
		}

        findViewById(R.id.copy_URL_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyURL();
            }
        });

        findViewById(R.id.share_URL_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareURL();
            }
        });

    }

	private class RetrieveURLTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			String sharedURL = params[0];
			String shortenedURL;

            /**
             * Check network connection
             * TODO: timeout as well
             */
			if (!networkAvailable()) {
				Toast.makeText(getBaseContext(), "Not connected to the internet", Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.GONE);
				return "Error";
			}

			Log.d("googl", "Fetching data");
			try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                DefaultHttpClient client = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost("https://www.googleapis.com/urlshortener/v1/url");
				post.setEntity(new StringEntity("{\"longUrl\": \"" + sharedURL + "\"}"));
				post.setHeader("Content-Type", "application/json");

				HttpResponse response = client.execute(post);

				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}

				JSONObject results = new JSONObject(new JSONTokener(builder.toString()));
                shortenedURL = results.getString("id");
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
//                else if (e instanceof SocketTimeoutException) {
//                    errorMessage += "Connection timeout";
//                }
                else {
                    errorMessage += "JSON parsing exception";
                }

                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
                Log.e("googl:retrieveURLTask", errorMessage);
                statusText.setText(errorMessage);
				return "Error";
			}
            return shortenedURL;
		}

        /**
         * Post-execution stuff updating the UI
         * @param shortenedURL shortenedURL received from Goo.gl
         */
        protected void onPostExecute(String shortenedURL) {
            statusText.setText("Done!");
            shortenedURLView.setText(shortenedURL);
            progressBar.setVisibility(View.GONE);
            shortenedURLView.setVisibility(View.VISIBLE);
            finalShortenedURL = shortenedURL;
        }
	}

	private boolean networkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

    private void copyURL() {
        ClipboardManager clipboard = (ClipboardManager)
                getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Shortened URL", finalShortenedURL);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void shareURL() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, finalShortenedURL);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Shared from Holo Goo.gl");
        startActivity(Intent.createChooser(intent, "Share"));
    }

}
