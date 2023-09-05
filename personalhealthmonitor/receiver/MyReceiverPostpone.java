package personalhealthmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import personalhealthmonitor.notification.NotificationHelper;

public class MyReceiverPostpone extends BroadcastReceiver {

//    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

    public MyReceiverPostpone() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendNotification("Secondo avviso..." , "Non farmi preoccupare!", "Entra subito e dimmi come stai!");


    }

}