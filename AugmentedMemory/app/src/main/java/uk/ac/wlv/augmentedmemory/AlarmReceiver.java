package uk.ac.wlv.augmentedmemory;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver{
    private static final String ARG_REMINDER= "reminder";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle1 = intent.getExtras();
        String title = bundle1.getString(ARG_REMINDER);
        int requestCode = bundle1.getInt("reminder1");
        Log.d("www", String.valueOf(title));
        Log.d("www", String.valueOf(requestCode));
        //Intent i = new Intent(context,DestinationActivity.class);
        Intent i = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,requestCode,i,Intent.FILL_IN_DATA);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Augmemory")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Augmented Memory")
                .setContentText(title)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());

    }
}
