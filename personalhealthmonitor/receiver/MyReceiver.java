package personalhealthmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import personalhealthmonitor.notification.NotificationHelper;

public class MyReceiver extends BroadcastReceiver {

//    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

    public MyReceiver() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

//        Intent startServiceIntent = new Intent(context, MyNewIntentService.class);
//        context.startForegroundService(startServiceIntent);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendDailyNotification("Hey... " , "Dove sei?");

    }

}