package personalhealthmonitor.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import personalhealthmonitor.MainActivity;
import personalhealthmonitor.Postpone;
import personalhealthmonitor.R;
import personalhealthmonitor.Utils;


public class NotificationHelper extends ContextWrapper {

    private String channelName = "NotificationChannelName";
    private String channelId = "personalhealthmonitor" + channelName;
    private static int notificationCode = 267;

    public NotificationHelper(Context context){
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("channelDescription");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String title, String text, String longText) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_icon_notification)
                .build();
        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Utils.addNotificationNote(title + " " + dateFormat.format(calendar.getTime()), text, "sendNotification", getApplicationContext());
    }

    public void sendDailyNotification(String title, String text) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent postponeIntent = new Intent(this, Postpone.class);
        postponeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent postponePendingIntent = PendingIntent.getActivity(this, 1200, postponeIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Entra subito! Voglio sapere come stai..."))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_icon_notification)
                .addAction(R.drawable.alarm_clock, "posticipa", postponePendingIntent)
                .build();
        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Utils.addNotificationNote("DailyReminder " + dateFormat.format(calendar.getTime()) , text, "sendNotification", getApplicationContext());
    }

}