package personalhealthmonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.DB.EntityTemperature;

public class TemperatureSection extends AppCompatActivity {

    private BarChart barChart;
    private Button save;
    private EditText editTemperature1;
    private EditText editTemperature2;
    private EditText editTemperature3;
    private TextView hours1;
    private TextView hours2;
    private TextView hours3;
    public DB DBinstance;
    public Context context;
    private String date;
    private ImageView chartButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.temperature_section);

        context = getApplicationContext();

        DBinstance = DB.getInstance(context);

        date = getIntent().getStringExtra("date");


        save = findViewById(R.id.saveButtonTemperatures);
        //buttonEnabled(false);

        editTemperature1 = findViewById(R.id.editTemperature1);
        editTemperature2 = findViewById(R.id.editTemperature2);
        editTemperature3 = findViewById(R.id.editTemperature3);

        hours1 = findViewById(R.id.hours1);
        hours2 = findViewById(R.id.hours2);
        hours3 = findViewById(R.id.hours3);

        setLayoutData();

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (!ControlGoodData()) {
                    Utils.generateToast("Inserisci dei dati corretti, separa i decimali con il .", context);
                    Utils.runAnimationView(editTemperature1);
                    Utils.runAnimationView(editTemperature2);
                    Utils.runAnimationView(editTemperature3);
                    return;
                } else {
                    saveData();
                }
            }
        });

        barChart = findViewById(R.id.temperatureChart);
        barChart.setNoDataText("");
        barChart.setVisibility(View.VISIBLE);
        barChart.setBackgroundColor(getResources().getColor(R.color.ghostwhite));

        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TemperatureSection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });


        chartButton = findViewById(R.id.temperatureChartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(barChart);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showChart(BarChart barChart){
        //barChart.setVisibility(View.VISIBLE);
        Utils.setBarChartLayout(barChart);

        setChartData(barChart);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChartData(BarChart barChart) {
        List<EntityTemperature> temperaturesElementChart = DBinstance.entityTemperatureDao().getAllEntityTemperature();
        int size = temperaturesElementChart.size();
        ArrayList<String> dateString = new ArrayList<>();

        if (size == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }else {
            dateString = sortTemperatureByDate(temperaturesElementChart);
        }

        temperaturesElementChart.clear();
        int index = dateString.size() - 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate todayDate = LocalDate.parse(date, formatter);
        while (index > -1 && dateString.size() > 0 && temperaturesElementChart.size() < 4){
            String dateS = dateString.get(index);
            LocalDate localDate = LocalDate.parse(dateS, formatter);
            if(todayDate.isAfter(localDate) || todayDate.equals(localDate)) temperaturesElementChart.add(DBinstance.entityTemperatureDao().findEntityTemperatureByDate(dateString.get(index)));
            index--;
        }

        ArrayList<BarEntry> barEntries0 = new ArrayList<>();
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        ArrayList<BarEntry> barEntries3 = new ArrayList<>();

        size = temperaturesElementChart.size();
        int i = size - 1;
        for (int j = 0; j < 4; j++) {
            for (int z = 0; z < 3; z++) {
                if (i >= 0 && i == size - 1) {
                    if (temperaturesElementChart.get(i).temperature1 != null && z == 0)
                        barEntries0.add(new BarEntry(z, temperaturesElementChart.get(i).temperature1));
                    if (temperaturesElementChart.get(i).temperature2 != null && z == 1)
                        barEntries0.add(new BarEntry(z, temperaturesElementChart.get(i).temperature2));
                    if (temperaturesElementChart.get(i).temperature3 != null && z == 2)
                        barEntries0.add(new BarEntry(z, temperaturesElementChart.get(i).temperature3));
                } else if (i >= 0 && i == size - 2) {
                    if (temperaturesElementChart.get(i).temperature1 != null && z == 0)
                        barEntries1.add(new BarEntry(z + 3, temperaturesElementChart.get(i).temperature1));
                    if (temperaturesElementChart.get(i).temperature2 != null && z == 1)
                        barEntries1.add(new BarEntry(z + 3, temperaturesElementChart.get(i).temperature2));
                    if (temperaturesElementChart.get(i).temperature3 != null && z == 2)
                        barEntries1.add(new BarEntry(z + 3, temperaturesElementChart.get(i).temperature3));
                } else if (i >= 0 && i == size - 3) {
                    if (temperaturesElementChart.get(i).temperature1 != null && z == 0)
                        barEntries2.add(new BarEntry(z + 3*2, temperaturesElementChart.get(i).temperature1));
                    if (temperaturesElementChart.get(i).temperature2 != null && z == 1)
                        barEntries2.add(new BarEntry(z + 3*2, temperaturesElementChart.get(i).temperature2));
                    if (temperaturesElementChart.get(i).temperature3 != null && z == 2)
                        barEntries2.add(new BarEntry(z + 3*2, temperaturesElementChart.get(i).temperature3));
                } else if (i >= 0 && i == size - 4) {
                    if (temperaturesElementChart.get(i).temperature1 != null && z == 0)
                        barEntries3.add(new BarEntry(z + 3*3, temperaturesElementChart.get(i).temperature1));
                    if (temperaturesElementChart.get(i).temperature2 != null && z == 1)
                        barEntries3.add(new BarEntry(z + 3*3, temperaturesElementChart.get(i).temperature2));
                    if (temperaturesElementChart.get(i).temperature3 != null && z == 2)
                        barEntries3.add(new BarEntry(z + 3*3, temperaturesElementChart.get(i).temperature3));
                }
            }
            i--;
        }

        BarDataSet set0 = new BarDataSet(barEntries0, "bar0");
        set0.setColors(getResources().getColor(R.color.seaGreenRipple));
        set0.setValueTextSize(11f);

        BarDataSet set1 = new BarDataSet(barEntries1, "bar1");
        set1.setColors(getResources().getColor(R.color.orangeYellow));
        set1.setValueTextSize(11f);

        BarDataSet set2 = new BarDataSet(barEntries2, "bar2");
        set2.setColors(ColorTemplate.getHoloBlue());
        set2.setValueTextSize(11f);

        BarDataSet set3 = new BarDataSet(barEntries3, "bar3");
        set3.setColors(getResources().getColor(R.color.redChart));
        set3.setValueTextSize(11f);

        ArrayList<IBarDataSet> barDataSet = new ArrayList<IBarDataSet>();

        barDataSet.add(set0);
        barDataSet.add(set1);
        barDataSet.add(set2);
        barDataSet.add(set3);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);

    }

    @SuppressLint("SetTextI18n")
    private void setLayoutData(){
        EntityTemperature entityTemperature = DBinstance.entityTemperatureDao().findEntityTemperatureByDate(date);
        if (entityTemperature != null) {
            if (entityTemperature.temperature1 != null) {
                editTemperature1.setText(entityTemperature.temperature1.toString());
                hours1.setText(entityTemperature.hours1);
            }
            if (entityTemperature.temperature2 != null) {
                editTemperature2.setText(entityTemperature.temperature2.toString());
                hours2.setText(entityTemperature.hours2);
            }
            if (entityTemperature.temperature3 != null) {
                editTemperature3.setText(entityTemperature.temperature3.toString());
                hours3.setText(entityTemperature.hours3);
            }
        }
    }


    private void buttonEnabled(boolean bool){
        save.setEnabled(bool);
    }

    //mi restituisce una stringa con composizione ora e minuti correnti
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime(){
        String hours = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).getHour() + ":" + LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).getMinute() + "";
        return hours;
    }


    //controlla che l'inserimento dei dati sia corretto
    private boolean ControlGoodData(){
        boolean response = true;
        try {
            if (!editTemperature1.getText().toString().equals("")) Float.parseFloat(editTemperature1.getText().toString());
            if (!editTemperature2.getText().toString().equals("")) Float.parseFloat(editTemperature2.getText().toString());
            if (!editTemperature3.getText().toString().equals("")) Float.parseFloat(editTemperature3.getText().toString());
        }
        catch(NumberFormatException e){
            response = false;
        }
        return response;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData(){
        boolean sendNotification = false;
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        float alertTemperature = Float.parseFloat(sharedPreferences.getString("temperatureMonitoringValue", "37.5"));

        EntityTemperature entityTemperature = DBinstance.entityTemperatureDao().findEntityTemperatureByDate(date);
        //caso1: non esiste la tabella, la setto e la inserisco
        if (entityTemperature == null) {
            entityTemperature = new EntityTemperature();
            if (!TextUtils.isEmpty(editTemperature1.getText())) {
                entityTemperature.temperature1 = Float.parseFloat(editTemperature1.getText().toString());
                entityTemperature.hours1 = getCurrentTime();
                if (entityTemperature.temperature1 > alertTemperature) sendNotification = true;
            }
            if (!TextUtils.isEmpty(editTemperature2.getText())) {
                entityTemperature.temperature2 = Float.parseFloat(editTemperature2.getText().toString());
                entityTemperature.hours2 = getCurrentTime();
                if (entityTemperature.temperature2 > alertTemperature) sendNotification = true;
            }
            if (!TextUtils.isEmpty(editTemperature3.getText())) {
                entityTemperature.temperature3 = Float.parseFloat(editTemperature3.getText().toString());
                entityTemperature.hours3 = getCurrentTime();
                if (entityTemperature.temperature3 > alertTemperature) sendNotification = true;
            }
            entityTemperature.date = date;
            DBinstance.entityTemperatureDao().insert(entityTemperature);
        } else {
            checkNullInsert(entityTemperature);
            if (entityTemperature.temperature1 != null) {
                if (Float.parseFloat(editTemperature1.getText().toString()) != (entityTemperature.temperature1)) {
                    entityTemperature.temperature1 = Float.parseFloat(editTemperature1.getText().toString());
                    entityTemperature.hours1 = getCurrentTime();
                    if (entityTemperature.temperature1 > alertTemperature) sendNotification = true;
                }
            } else if (!TextUtils.isEmpty(editTemperature1.getText())) {
                entityTemperature.temperature1 = Float.parseFloat(editTemperature1.getText().toString());
                entityTemperature.hours1 = getCurrentTime();
                if (entityTemperature.temperature1 > alertTemperature) sendNotification = true;
            }
            if (entityTemperature.temperature2 != null) {
                if (Float.parseFloat(editTemperature2.getText().toString()) != (entityTemperature.temperature2)) {
                    entityTemperature.temperature2 = Float.parseFloat(editTemperature2.getText().toString());
                    entityTemperature.hours2 = getCurrentTime();
                    if (entityTemperature.temperature2 > alertTemperature) sendNotification = true;
                }
            } else if (!TextUtils.isEmpty(editTemperature2.getText())) {
                entityTemperature.temperature2 = Float.parseFloat(editTemperature2.getText().toString());
                entityTemperature.hours2 = getCurrentTime();
                if (entityTemperature.temperature2 > alertTemperature) sendNotification = true;
            }
            if (entityTemperature.temperature3 != null) {
                if (Float.parseFloat(editTemperature3.getText().toString()) != (entityTemperature.temperature3)) {
                    entityTemperature.temperature3 = Float.parseFloat(editTemperature3.getText().toString());
                    entityTemperature.hours3 = getCurrentTime();
                    if (entityTemperature.temperature3 > alertTemperature) sendNotification = true;
                }
            } else if (!TextUtils.isEmpty(editTemperature3.getText())) {
                entityTemperature.temperature3 = Float.parseFloat(editTemperature3.getText().toString());
                entityTemperature.hours3 = getCurrentTime();
                if (entityTemperature.temperature3 > alertTemperature) sendNotification = true;
            }
            DBinstance.entityTemperatureDao().update(entityTemperature);
        }
        if (sendNotification && sharedPreferences.getBoolean("temperatureMonitoring", false)) Utils.addNotificationNote("Chiama il medico!", "Ho visto che hai una febbre alta in data " + date + ". Puoi chiamare il medico direttamente dall'app.", "doctor", context);
        Utils.generateToast("DATI SALVATI", context);
        showChart(barChart);
    }

    private void checkNullInsert(EntityTemperature entityTemperature){
        if (editTemperature1.getText().toString().equals("")) entityTemperature.temperature1 = null;
        if (editTemperature2.getText().toString().equals("")) entityTemperature.temperature2 = null;
        if (editTemperature3.getText().toString().equals("")) entityTemperature.temperature3 = null;

        DBinstance.entityTemperatureDao().update(entityTemperature);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<String> sortTemperatureByDate(List<EntityTemperature> list){
        int size = list.size();
        ArrayList<String> dateString = new ArrayList<String>();
        ArrayList<LocalDate> localDateList = new ArrayList<LocalDate>();
        for (int i = 0; i < size; i++){
            String string = list.get(i).date;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(string, formatter);
            localDateList.add(date);
        }

        Collections.sort(localDateList);

        for (int i = 0; i < size; i++){
            DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateString.add(localDateList.get(i).format(formattedDate));
        }

        return dateString;
    }


    private void showChartDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TemperatureSection.this);
        alertDialog.setTitle("Salvare il grafico?");

        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        barChart.saveToGallery("Temperature" + System.currentTimeMillis()/1000, 50);
                        Utils.generateToast("Grafico salvato", TemperatureSection.this);
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialogCreate = alertDialog.create();
        alertDialogCreate.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialogCreate.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.redChart));
                alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.seaGreenPrimary));
            }
        });

        alertDialogCreate.show();
    }

}
