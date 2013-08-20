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
                stopService(originalIntent);
            }
        };

        IntentFilter intentFilterCopy = new IntentFilter("com.pandanomic.ACTION_COPY");
        getBaseContext().registerReceiver(broadcastReceiver, intentFilterCopy);
        Intent copy = new Intent("com.pandanomic.ACTION_COPY");
        PendingIntent piCopy = PendingIntent.getBroadcast(getBaseContext(), 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(R.drawable.ic_menu_copy, "Copy", piCopy);

//        IntentFilter intentFilterShare = new IntentFilter("com.pandanomic.ACTION_SHARE");
//        getBaseContext().registerReceiver(broadcastReceiver, intentFilterShare);
//        Intent share = new Intent("com.pandanomic.ACTION_SHARE");
//        PendingIntent piShare = PendingIntent.getBroadcast(getBaseContext(), 0, share, PendingIntent.FLAG_CANCEL_CURRENT);
//        mBuilder.addAction(R.drawable.ic_social_share, "Share", piShare);

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
        Toast.makeText(getBaseContext(), "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void shareURL(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Shared from Holo Goo.gl");
        startActivity(Intent.createChooser(intent, "Share"));
    }
}
