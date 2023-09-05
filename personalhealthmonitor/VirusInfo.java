package personalhealthmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.sql.Time;
import java.util.Calendar;

import personalhealthmonitor.receiver.MyReceiverVirus;

public class VirusInfo extends AppCompatActivity {

    private EditText virusEdit;
    private Button addNotificaition;
    public Context context;
    private SwitchCompat switchCompat;
    private TimePicker timePicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.virus_info);

        context = getApplicationContext();

        virusEdit = findViewById(R.id.virusEditText);

        timePicker = findViewById(R.id.timePickerVirus);
        timePicker.setIs24HourView(true);

        switchCompat = findViewById(R.id.virusSwitch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    timePicker.setVisibility(View.VISIBLE);
                    timePicker.setHour(new Time(System.currentTimeMillis()).getHours());
                    timePicker.setMinute(new Time(System.currentTimeMillis()).getMinutes());
                }else {
                    timePicker.setVisibility(View.GONE);
                }
            }
        });


        addNotificaition = findViewById(R.id.virusButton);
        addNotificaition.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                String string = virusEdit.getText().toString();
                if (!string.equals("")) {
                    Utils.generateToast("Promemoria aggiunto", context);
                    Utils.addNotificationNote("Promemoria", string, "virus", VirusInfo.this);
                    virusEdit.setText("");
                    if (switchCompat.isChecked()) sendVirusNotification();
                } else {
                    Utils.generateToast("Inserisci un testo", VirusInfo.this);
                    Utils.runAnimationView(virusEdit);
                }
            }
        });

    }

    private void sendVirusNotification(){
        switchCompat.setChecked(false);
        timePicker.setVisibility(View.GONE);

        Intent notifyIntent = new Intent(context, MyReceiverVirus.class);
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

    }

}
