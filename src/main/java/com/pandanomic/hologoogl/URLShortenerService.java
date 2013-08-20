package com.pandanomic.hologoogl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by pandanomic on 8/20/13.
 */
public class URLShortenerService  extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int stardId) {
        String sharedURL = intent.getStringExtra("URL");

        URLShortener shortener = new URLShortener(getApplicationContext());
        String shortenedURL = shortener.generate(sharedURL);


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
