package personalhealthmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import personalhealthmonitor.receiver.MyReceiverPostpone;

public class Postpone extends AppCompatActivity {

    private TimePicker timePicker;
    private Button buttonSave;
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.postpone);

        context = Postpone.this;

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        timePicker = findViewById(R.id.timePickerPostpone);
        timePicker.setIs24HourView(true);
        timePicker.setHour(sharedPreferences.getInt("notificationHour", 12));
        timePicker.setMinute(sharedPreferences.getInt("notificationMinute", 0));

        buttonSave = findViewById(R.id.buttonPostpone);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                                Intent notifyIntent = new Intent(v.getContext(), MyReceiverPostpone.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 1);

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.RTC_WAKEUP, pendingIntent);

                finish();
            }
        });

    }



}
