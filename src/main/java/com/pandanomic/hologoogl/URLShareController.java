package com.pandanomic.hologoogl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Method;

public class URLShareController extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
            Intent i = new Intent(this, URLShortenerService.class);
            i.putExtra("URL", sharedURL);
            startService(i);
            Toast.makeText(this, "Generating URL, check notification bar", Toast.LENGTH_LONG).show();
            // TODO: Expand notifications panel

            finish();
		}
    }
}