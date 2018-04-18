package android.example.com.squawker.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider.SquawkMessages;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 1;
    private static String CHANNEL_ID = "messages";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        ContentValues messageValues = new ContentValues();
        messageValues.put(SquawkContract.COLUMN_AUTHOR, data.get(SquawkContract.COLUMN_AUTHOR));
        messageValues.put(SquawkContract.COLUMN_MESSAGE, data.get(SquawkContract.COLUMN_MESSAGE));
        messageValues.put(SquawkContract.COLUMN_DATE, data.get(SquawkContract.COLUMN_DATE));
        messageValues
                .put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(SquawkContract.COLUMN_AUTHOR_KEY));

        getContentResolver().insert(SquawkMessages.CONTENT_URI, messageValues);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Messages",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(data.get(SquawkContract.COLUMN_AUTHOR))
                .setSmallIcon(R.drawable.ic_duck)
                .setContentText(data.get(SquawkContract.COLUMN_MESSAGE).substring(0, 30) + "...");
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
