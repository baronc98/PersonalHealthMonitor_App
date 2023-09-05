package personalhealthmonitor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.sql.Time;
import java.util.Calendar;

import personalhealthmonitor.receiver.MyReceiver;


public class Settings extends AppCompatActivity {

    private Context context;
    private TimePicker timePicker;
    private Button buttonSave;
    private SwitchCompat switchCompat1, switchCompat2, switchCompat3;
    private boolean sw1, sw2, sw3;
    private EditText editTemperature, editPressure;
    private EditText editPains;
    private ImageView saveValueSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings);

        context = getApplicationContext();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(sharedPreferences.getInt("notificationHour", 12));
        timePicker.setMinute(sharedPreferences.getInt("notificationMinute", 0));

        switchCompat1 = findViewById(R.id.settingSwitch1);
        switchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMonitoring(1);
            }
        });
        switchCompat2 = findViewById(R.id.settingSwitch2);
        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMonitoring(2);
            }
        });
        switchCompat3 = findViewById(R.id.settingSwitch3);
        switchCompat3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMonitoring(3);
            }
        });

        saveValueSettings = findViewById(R.id.saveValueSettings);

        sw1 = false;
        sw2 = false;
        sw3 = false;

        editTemperature = findViewById(R.id.editTemperatureMonitoring);
//        editPains = findViewById(R.id.editPainsMonitoring);
        editPressure = findViewById(R.id.editPressureMonitoring);

        setSwitch();

        saveValueSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateMonitoringValues();
            }
        });

        buttonSave = findViewById(R.id.buttonSaveTimePicker);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("notificationHour", timePicker.getHour()).apply();
                editor.putInt("notificationMinute", timePicker.getMinute()).apply();

                Intent notifyIntent = new Intent(v.getContext(), MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 1);

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pendingIntent);

                Utils.generateToast("Notifica giornaliera impostata alle ore " + Utils.buildHour(timePicker.getHour(), timePicker.getMinute()), context);

                finish();

            }
        });

    }

    private void updateMonitoring(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (i == 1 && sw1) {
            editor.putBoolean("temperatureMonitoring", !sharedPreferences.getBoolean("temperatureMonitoring", false)).apply();
            if (editTemperature.getVisibility() == View.VISIBLE) {
                editTemperature.setVisibility(View.GONE);
            } else {
                editTemperature.setVisibility(View.VISIBLE);
            }
        }
        if (i == 2 && sw2) {
            editor.putBoolean("painsMonitoring", !sharedPreferences.getBoolean("painsMonitoring", false)).apply();
//            if (editPains.getVisibility() == View.VISIBLE) {
//                editPains.setVisibility(View.GONE);
//            } else {
//                editPains.setVisibility(View.VISIBLE);
//            }
        }
        if (i == 3 && sw3) {
            editor.putBoolean("pressureMonitoring", !sharedPreferences.getBoolean("pressureMonitoring", false)).apply();
            if (editPressure.getVisibility() == View.VISIBLE) {
                editPressure.setVisibility(View.GONE);
            } else {
                editPressure.setVisibility(View.VISIBLE);
            }
        }
        setEditValue();
    }

    private void setSwitch() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("temperatureMonitoring", false)) {
            switchCompat1.setChecked(true);
            editTemperature.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("painsMonitoring", false)){
            switchCompat2.setChecked(true);
//            editPains.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("pressureMonitoring", false)) {
            switchCompat3.setChecked(true);
            editPressure.setVisibility(View.VISIBLE);
        }
        sw1 = true;
        sw2 = true;
        sw3 = true;
    }

    private void setEditValue() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editTemperature.setText(sharedPreferences.getString("temperatureMonitoringValue", "37.5"));
        editPressure.setText(sharedPreferences.getString("pressureMonitoringValue", "150"));
    }

    private void updateMonitoringValues() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String tempValue = editTemperature.getText().toString();
//        String painsValue = editPains.getText().toString();
        String pressureValue = editPressure.getText().toString();
        if (!tempValue.equals("")) {
            editor.putString("temperatureMonitoringValue", tempValue).apply();
        }
//        if (!painsValue.equals("")) {
//            editor.putString("painsMonitoringValue", painsValue).apply();
//        }
        if (!pressureValue.equals("")) {
            editor.putString("pressureMonitoringValue", pressureValue).apply();
        }
        Utils.generateToast("Valori salvati", context);
    }

}