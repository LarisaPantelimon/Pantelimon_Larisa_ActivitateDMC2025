package com.example.licenta_mobile;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.Entities.AccountDao;
import com.example.Entities.Accounts;
import com.example.Entities.AppDatabase;
import com.example.licenta_mobile.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "auth_requests_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received");

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

            String type = remoteMessage.getData().get("type");
            if ("auth_request".equals(type)) {
                String targetEmail = remoteMessage.getData().get("email");
                String ownerEmail = remoteMessage.getData().get("ownerEmail");

                if (targetEmail != null && ownerEmail != null) {
                    handleAuthRequest(targetEmail, ownerEmail);
                }
            }
        }
    }

    private void handleAuthRequest(String email, String ownerEmail) {
        // Update local database
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AccountDao dao = AppDatabase.getDatabase(this).accountsDao();
            Accounts account = dao.getAccount(ownerEmail, email);

            if (account != null) {
                account.setHasPendingRequest(true);
                dao.update(account);

                // Notify UI if app is in foreground
                if (isAppInForeground()) {
                    sendBroadcastToActivity(email);
                } else {
                    showNotification(email);
                }
            }
        });
    }

    private void sendBroadcastToActivity(String email) {
        Intent intent = new Intent("com.example.AUTH_REQUEST_RECEIVED");
        intent.putExtra("email", email);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showNotification(String email) {
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.red_dot_background)
                .setContentTitle("Authentication Request")
                .setContentText("New login request for " + email)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Authentication Requests",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for authentication requests");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isAppInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();

        if (processes == null) return false;

        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return process.processName.equals(getPackageName());
            }
        }
        return false;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // Implement your server token update logic here
        // You'll need to get the current user's email from SharedPreferences
        // and send both the email and token to your server
    }
}