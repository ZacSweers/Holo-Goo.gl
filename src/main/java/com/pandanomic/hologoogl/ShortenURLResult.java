package com.pandanomic.hologoogl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

//		progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
		shortenedURLView.setVisibility(View.GONE);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if ((Intent.ACTION_SEND.equals(action) || "true".equals(intent.getStringExtra("URL"))) && "text/plain".equals(type)) {
			Log.d("googl", "handling intent");
			String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (sharedURL != null) {
                generateURL(sharedURL);
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

    private void generateURL(String sharedURL) {
        JSONObject result;
        String resultURL = null;
        try {
            result = new PostTask().execute(sharedURL).get(5000, TimeUnit.MILLISECONDS);

            if (result == null) {
                Toast.makeText(getBaseContext(), "Error retrieving data", Toast.LENGTH_LONG).show();
                return;
            }

            resultURL = result.getString("id");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getBaseContext(), "Timeout", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "Error Parsing Result", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (resultURL != null) {
            statusText.setText("Done!");
            shortenedURLView.setText(resultURL);
            shortenedURLView.setVisibility(View.VISIBLE);
            finalShortenedURL = resultURL;
        } else {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG).show();
        }
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
