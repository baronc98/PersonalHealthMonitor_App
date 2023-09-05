package personalhealthmonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import personalhealthmonitor.DB.EntityBlood;
import personalhealthmonitor.DB.EntityInfo;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.DB.EntityTemperature;

public class BloodSection extends AppCompatActivity {

    private LineChart lineChart;
    private LineChart lineChart2;
    private Button save;
    private EditText editPressure;
    private EditText editGlycemicIndex;
    private EditText editDrug;
    public DB DBinstance;
    public Context context;
    private String date;
    private ImageView chartButton;
    private ImageView chartButton2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blood_section);

        context = getApplicationContext();

        DBinstance = DB.getInstance(context);

        date = getIntent().getStringExtra("date");


        save = findViewById(R.id.saveButtonBlood);

        editPressure = findViewById(R.id.pressure);
        editGlycemicIndex = findViewById(R.id.glycemicIndex);
        editDrug = findViewById(R.id.drug);

        setLayoutData();

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (!ControlGoodData()) {
                    Utils.generateToast("Inserisci dei dati corretti, separa i decimali con il .", context);
                    Utils.runAnimationView(editDrug);
                    Utils.runAnimationView(editGlycemicIndex);
                    Utils.runAnimationView(editPressure);
                    return;
                } else {
                    saveData();
                }
            }
        });

        lineChart = findViewById(R.id.bloodChart1);
        lineChart.setNoDataText("");
        lineChart.setBackgroundColor(getResources().getColor(R.color.ghostwhite));
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BloodSection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });

        chartButton = findViewById(R.id.bloodChartButton1);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(lineChart, 1);
            }
        });


        lineChart2 = findViewById(R.id.bloodChart2);
        lineChart2.setNoDataText("");
        lineChart2.setBackgroundColor(getResources().getColor(R.color.ghostwhite));
        lineChart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BloodSection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });

        chartButton2 = findViewById(R.id.bloodChartButton2);
        chartButton2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(lineChart2, 2);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showChart(LineChart lineChart, int i){
        Utils.setLineChartLayout(lineChart);
        setChartData(lineChart, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChartData(LineChart lineChart, int intero){

        List<EntityBlood> bloodElementChart = DBinstance.entityBloodDao().getAllEntityBlood();
        int size = bloodElementChart.size();
        ArrayList<String> dateString = new ArrayList<>();

        if (size == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }else {
            dateString = sortInfoByDate(bloodElementChart);
        }

        bloodElementChart.clear();

        int index = dateString.size() - 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate todayDate = LocalDate.parse(date, formatter);
        while (index > -1 && dateString.size() > 0 && bloodElementChart.size() < 10){
            String dateS = dateString.get(index);
            LocalDate localDate = LocalDate.parse(dateS, formatter);
            if(todayDate.isAfter(localDate) || todayDate.equals(localDate)) bloodElementChart.add(0, DBinstance.entityBloodDao().findEntityBloodByDate(dateString.get(index)));
            index--;
        }

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();


        for (int i = 0; i < bloodElementChart.size(); i++) {
            final EntityBlood item = bloodElementChart.get(i);
            final String label = item.date;
            labels.add(label);
            if (intero == 1) {
                if (item.pressure != null) if(!item.pressure.equals("")) entries.add(new Entry(i, Float.parseFloat(item.pressure)));
            }else {
                if (item.glycemicIndex != null) if(!item.glycemicIndex.equals("")) entries.add(new Entry(i, Float.parseFloat(item.glycemicIndex)));
            }

        }

        if (entries.size() == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Valori del sangue");
        dataSet.setValueTextSize(11f);
        dataSet.setLineWidth(3);
        dataSet.setCircleSize(3);
        dataSet.setCircleColor(getColor(R.color.rippelColor));
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<String> sortInfoByDate(List<EntityBlood> list){
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

    private void setLayoutData(){
        EntityBlood entityBlood = DBinstance.entityBloodDao().findEntityBloodByDate(date);
        if (entityBlood != null) {
            if (entityBlood.pressure != null) {
                editPressure.setText(entityBlood.pressure);
            }
            if (entityBlood.glycemicIndex != null) {
                editGlycemicIndex.setText(entityBlood.glycemicIndex);
            }
            if (entityBlood.drug != null) {
                editDrug.setText(entityBlood.drug);
            }
        }

    }

    private boolean ControlGoodData(){
        boolean response = true;
        try {
            if (!editPressure.getText().toString().equals("")) Float.parseFloat(editPressure.getText().toString());
            if (!editGlycemicIndex.getText().toString().equals("")) Float.parseFloat(editGlycemicIndex.getText().toString());
        }
        catch(NumberFormatException e){
            response = false;
        }
        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        float alertPressure = Float.parseFloat(sharedPreferences.getString("pressureMonitoringValue", "150"));

        EntityBlood entityBlood = DBinstance.entityBloodDao().findEntityBloodByDate(date);
        //caso1: non esiste la tabella, la setto e la inserisco
        if (entityBlood == null) {
            entityBlood = new EntityBlood();
            if (!TextUtils.isEmpty(editPressure.getText())) {
                entityBlood.pressure = (editPressure.getText().toString());
            }
            if (!TextUtils.isEmpty(editGlycemicIndex.getText())) {
                entityBlood.glycemicIndex = (editGlycemicIndex.getText().toString());
            }
            if (!TextUtils.isEmpty(editDrug.getText())) {
                entityBlood.drug = (editDrug.getText().toString());
            }
            entityBlood.date = date;
            if (Float.parseFloat(entityBlood.pressure) > alertPressure && sharedPreferences.getBoolean("pressureMonitoring", false)) Utils.addNotificationNote("Chiama il medico!", "Ho visto che hai un valore della pressione estremamente alto in data: " + date + ". Puoi chiamare il medico direttamente dall'app.", "doctor", context);
            DBinstance.entityBloodDao().insert(entityBlood);
        } else {
            if (entityBlood.pressure != null) {
                if (!editPressure.getText().toString().equals(entityBlood.pressure)) {
                    entityBlood.pressure = (editPressure.getText().toString());
                    if (!entityBlood.pressure.equals("")) {
                        if (Float.parseFloat(entityBlood.pressure) > alertPressure && sharedPreferences.getBoolean("pressureMonitoring", false))
                            Utils.addNotificationNote("Chiama il medico!", "Ho visto che hai un valore della pressione estremamente alto in data: " + date + ". Puoi chiamare il medico direttamente dall'app.", "doctor", context);
                    }
                }
            } else if (!TextUtils.isEmpty(editPressure.getText())) {
                entityBlood.pressure = (editPressure.getText().toString());
            }
            if (entityBlood.glycemicIndex != null) {
                if (!editGlycemicIndex.getText().toString().equals(entityBlood.glycemicIndex)) {
                    entityBlood.glycemicIndex = (editGlycemicIndex.getText().toString());
                }
            } else if (!TextUtils.isEmpty(editGlycemicIndex.getText())) {
                entityBlood.glycemicIndex = (editGlycemicIndex.getText().toString());
            }
            if (entityBlood.drug != null) {
                if (!editDrug.getText().toString().equals(entityBlood.drug)) {
                    entityBlood.drug = (editDrug.getText().toString());
                }
            } else if (!TextUtils.isEmpty(editDrug.getText())) {
                entityBlood.drug = (editDrug.getText().toString());
            }
            DBinstance.entityBloodDao().update(entityBlood);
        }
        Utils.generateToast("DATI SALVATI", context);
        showChart(lineChart, 1);
        showChart(lineChart2, 2);
    }

    private void showChartDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BloodSection.this);
        alertDialog.setTitle("Salvare il grafico?");

        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        lineChart.saveToGallery("Valori del sangue" + System.currentTimeMillis()/1000, 50);
                        Utils.generateToast("Grafico salvato", BloodSection.this);
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
