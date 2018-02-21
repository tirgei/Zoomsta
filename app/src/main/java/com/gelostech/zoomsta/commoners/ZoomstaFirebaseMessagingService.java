package com.gelostech.zoomsta.commoners;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by tirgei on 11/8/17.
 */

public class ZoomstaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage);
        }

    }

    private void sendNotification(RemoteMessage messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(messageBody.getNotification().getTitle())
                        .setContentText(messageBody.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
