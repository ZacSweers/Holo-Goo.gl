package com.pandanomic.hologoogl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class URLShortenerService  extends Service {

    private Intent originalIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int stardId) {
        originalIntent = intent;
        String sharedURL = intent.getStringExtra("URL");

        URLShortener shortener = new URLShortener();
        String shortenedURL = shortener.generate(sharedURL);

        if (shortenedURL != null) {
            handleNotification(shortenedURL, intent);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void handleNotification(String shortenedURL, Intent intent) {
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Shortening URL...")
                        .setProgress(0, 0, true);

        final int mNotificationId = 001;
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        final String finalResultURL = shortenedURL;
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
                stopService(originalIntent);
            }
        };

        IntentFilter intentFilter = new IntentFilter("com.pandanomic.ACTION_COPY");
        getBaseContext().registerReceiver(brCopy, intentFilter);

        Intent copy = new Intent("com.pandanomic.ACTION_COPY");
        PendingIntent piCopy = PendingIntent.getBroadcast(getBaseContext(), 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.addAction(R.drawable.ic_menu_copy, "Copy", piCopy);

        mBuilder.setProgress(0, 0, false);
        mBuilder.setContentTitle("Done");
        mBuilder.setContentText(finalResultURL);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
