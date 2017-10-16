package com.sagib.food2youadmin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void handleIntent(Intent intent) {
        String orderTime = intent.getExtras().getString("time");
        String fullName = intent.getExtras().getString("fullName");
        String orderNumber = intent.getExtras().getString("orderNumber");
        String futureDate = intent.getExtras().getString("futureDate");
        String futureHour = intent.getExtras().getString("futureHour");
        String address = intent.getExtras().getString("address");
        String houseNumber = intent.getExtras().getString("houseNumber");
        String city = intent.getExtras().getString("city");
        String notes = intent.getExtras().getString("notes");
        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent pi =
                PendingIntent.getActivity(this, 1, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("ישנה הזמנה חדשה!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format("הזמנה חדשה מספר %s עבור %s לכתובת %s %s, %s.\nמוזמן לשעה %s בתאריך %s\nתאריך הזמנה: %s.\nהערות: %s", orderNumber, fullName, address, houseNumber, city, futureHour, futureDate, orderTime, notes)))
                .setSmallIcon(R.drawable.alogo)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chime))
                .setContentIntent(pi);
        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(1, builder.build());
     }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
