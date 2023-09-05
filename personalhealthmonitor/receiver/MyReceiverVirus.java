package personalhealthmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import personalhealthmonitor.notification.NotificationHelper;

public class MyReceiverVirus extends BroadcastReceiver {

//    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

    public MyReceiverVirus() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendNotification("Promemoria" , "Sempre in tuo soccorso!", "Hai delle notifiche da controllare");


    }

}