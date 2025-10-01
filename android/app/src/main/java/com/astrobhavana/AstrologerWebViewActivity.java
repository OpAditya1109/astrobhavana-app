package com.yourapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class AstrologerWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "consultation_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astrologer_webview);

        createNotificationChannel();

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load your website
        webView.loadUrl("https://www.astrobhavana.com");

        // Ensure links open inside WebView
        webView.setWebViewClient(new WebViewClient());

        // Add JavaScript interface
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidApp");
    }

    // JavaScript Interface
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void postMessage(String message) {
            try {
                // Parse message JSON
                org.json.JSONObject json = new org.json.JSONObject(message);
                String type = json.getString("type");

                if ("NEW_CONSULTATION".equals(type)) {
                    String mode = json.getString("mode");
                    String userName = json.getString("userName");

                    // Play ringtone
                    playRingtone();

                    // Show notification
                    showNotification(mode, userName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void playRingtone() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
    }

    private void stopRingtone() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showNotification(String mode, String userName) {
        Intent intent = new Intent(this, AstrologerWebViewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // replace with your app icon
                .setContentTitle("New Consultation")
                .setContentText(mode + " consultation by " + userName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Consultation Alerts";
            String description = "Notifications for new consultations";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setSound(android.net.Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ringtone), null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        stopRingtone();
        super.onDestroy();
    }
}
