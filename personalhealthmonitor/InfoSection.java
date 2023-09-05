package personalhealthmonitor;

import android.Manifest;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityInfo;
import personalhealthmonitor.DB.EntityTemperature;
import personalhealthmonitor.R;
import personalhealthmonitor.Utils;

public class InfoSection extends AppCompatActivity {

    private EditText age, weight, height;
    private Button saveInfo;
    public Context context;
    public DB DBinstance;
    private String date;
    private LineChart lineChart;
    private ImageView chartButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.info_section);

        context = getApplicationContext();

        DBinstance = DB.getInstance(context);

        date = getIntent().getStringExtra("date");

        chartButton = findViewById(R.id.infoChartButton);
        lineChart = findViewById(R.id.infoChart);

        chartButton = findViewById(R.id.infoChartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(lineChart);
            }
        });

        lineChart.setNoDataText("");
        lineChart.setBackgroundColor(getResources().getColor(R.color.ghostwhite));
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InfoSection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });

        age = findViewById(R.id.years);
        weight = findViewById(R.id.weightInfo);
        height = findViewById(R.id.heightInfo);

        setLayoutData();

        if (age.getText().toString().equals("")) setAge();


        saveInfo = findViewById(R.id.saveInfoSection);
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (!ControlGoodData()) {
                    Utils.generateToast("Inserisci dati corretti", context);
                    Utils.runAnimationView(age);
                    Utils.runAnimationView(weight);
                    Utils.runAnimationView(height);
                    return;
                } else {
                    saveData();
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showChart(LineChart lineChart){
        Utils.setLineChartLayout(lineChart);
        setChartData(lineChart);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChartData(LineChart lineChart){

        List<EntityInfo> infoElementChart = DBinstance.entityInfoDao().getAllEntityInfo();
        int size = infoElementChart.size();
        ArrayList<String> dateString = new ArrayList<>();

        if (size == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }else {
            dateString = sortInfoByDate(infoElementChart);
        }

        infoElementChart.clear();

        int index = dateString.size() - 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate todayDate = LocalDate.parse(date, formatter);
        while (index > -1 && dateString.size() > 0 && infoElementChart.size() < 15){
            String dateS = dateString.get(index);
            LocalDate localDate = LocalDate.parse(dateS, formatter);
            if(todayDate.isAfter(localDate) || todayDate.equals(localDate)) infoElementChart.add(0, DBinstance.entityInfoDao().findEntityInfoByDate(dateString.get(index)));
            index--;
        }

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();


        for (int i = 0; i < infoElementChart.size(); i++) {
            final EntityInfo item = infoElementChart.get(i);
            final String label = item.date;
            labels.add(label);
            if (item.weight != null) if(!item.weight.equals("")) entries.add(new Entry(i, Float.parseFloat(item.weight)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Peso Corporeo");
        dataSet.setValueTextSize(11f);
        dataSet.setLineWidth(3);
        dataSet.setCircleSize(3);
        dataSet.setCircleColor(getColor(R.color.rippelColor));
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<String> sortInfoByDate(List<EntityInfo> list){
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
        EntityInfo entityInfo = DBinstance.entityInfoDao().findEntityInfoByDate(date);
        if (entityInfo != null) {
            if (entityInfo.age != null) {
                age.setText(entityInfo.age);
            }
            if (entityInfo.weight != null) {
                weight.setText(entityInfo.weight);
            }
            if (entityInfo.height != null) {
                height.setText(entityInfo.height);
            }
        }

    }

    private boolean ControlGoodData(){
        boolean response = true;
        try {
            if (!age.getText().toString().equals("")) Float.parseFloat(age.getText().toString());
            if (!weight.getText().toString().equals("")) Float.parseFloat(weight.getText().toString());
            if (!height.getText().toString().equals("")) Float.parseFloat(height.getText().toString());
        }
        catch(NumberFormatException e){
            response = false;
        }
        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData(){
        EntityInfo entityInfo = DBinstance.entityInfoDao().findEntityInfoByDate(date);
        //caso1: non esiste la tabella, la setto e la inserisco
        if (entityInfo == null) {
            entityInfo = new EntityInfo();
            if (!TextUtils.isEmpty(age.getText())) {
                entityInfo.age = (age.getText().toString());
            }
            if (!TextUtils.isEmpty(weight.getText())) {
                entityInfo.weight = (weight.getText().toString());
            }
            if (!TextUtils.isEmpty(height.getText())) {
                entityInfo.height = (height.getText().toString());
            }
            entityInfo.date = date;
            DBinstance.entityInfoDao().insert(entityInfo);
        } else {
            if (entityInfo.age != null) {
                if ((age.getText().toString()) != (entityInfo.age)) {
                    entityInfo.age = (age.getText().toString());
                }
            } else if (!TextUtils.isEmpty(age.getText())) {
                entityInfo.age = (age.getText().toString());
            }
            if (entityInfo.weight != null) {
                if ((weight.getText().toString()) != (entityInfo.weight)) {
                    entityInfo.weight = (weight.getText().toString());
                }
            } else if (!TextUtils.isEmpty(weight.getText())) {
                entityInfo.weight = (weight.getText().toString());
            }
            if (entityInfo.height != null) {
                if ((height.getText().toString()) != (entityInfo.height)) {
                    entityInfo.height = (height.getText().toString());
                }
            } else if (!TextUtils.isEmpty(height.getText())) {
                entityInfo.height = (height.getText().toString());
            }
            DBinstance.entityInfoDao().update(entityInfo);
        }
        Utils.generateToast("DATI SALVATI", context);
        showChart(lineChart);
    }

    private void setAge(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        age.setText(sharedPreferences.getString("age", ""));
    }

    private void showChartDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InfoSection.this);
        alertDialog.setTitle("Salvare il grafico?");

        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        lineChart.saveToGallery("Informazioni generali" + System.currentTimeMillis()/1000, 50);
                        Utils.generateToast("Grafico salvato", InfoSection.this);
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
