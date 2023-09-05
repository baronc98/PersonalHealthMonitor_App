package personalhealthmonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityPhysicalActivity;
import personalhealthmonitor.R;
import personalhealthmonitor.Utils;

public class PhysicalActivitySection extends AppCompatActivity {

    private PieChart pieChart;
    public Context context;
    public DB DBinstance;
    private String date;
    private Button saveInfo;
    private CheckBox coproLibero, pesi, corsa, camminata, sport, yoga;
    private EditText time;
    private ImageView chartButton;
    private ImageView saveIMG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.physical_activity_section);

        context = getApplicationContext();

        DBinstance = DB.getInstance(context);

        date = getIntent().getStringExtra("date");

        coproLibero = findViewById(R.id.corpoLibero);
        pesi = findViewById(R.id.Pesistica);
        corsa = findViewById(R.id.corsa);
        camminata = findViewById(R.id.camminata);
        sport = findViewById(R.id.sportContatto);
        yoga = findViewById(R.id.yoga);
        time = findViewById(R.id.howMuchTime);

        pieChart = findViewById(R.id.sportChart);
        pieChart.setNoDataText("");
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setBackgroundColor(getResources().getColor(R.color.ghostwhite));

        saveIMG = findViewById(R.id.saveIMGactivity);
        saveIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhysicalActivitySection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });

        chartButton = findViewById(R.id.sportChartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(pieChart);
            }
        });

        saveInfo = findViewById(R.id.saveSportSection);
        saveInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveData();
                showChart(pieChart);
            }
        });

        setLayoutData();

    }

    private void showChart(PieChart pieChart){
        //pieChart.setVisibility(View.VISIBLE);

        Utils.setPieChartLayout(pieChart);

        setChartData(pieChart);
    }

    private void setChartData(PieChart pieChart) {
        List<EntityPhysicalActivity> ElementChart = DBinstance.entityPhysicalActivityDao().getAllEntityPhysicalActivity();
        int size = ElementChart.size();

        if (size == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }

        int corsa = 0, corpoLibero = 0, camminata = 0, yoga = 0, pesi = 0, sport = 0;

        for (int j = 0; j < size; j++) {
            EntityPhysicalActivity entityPhysicalActivity = ElementChart.get(j);
            if (entityPhysicalActivity.corpoLibero) corpoLibero = corpoLibero + 1;
            if (entityPhysicalActivity.pesi) pesi = pesi + 1;
            if (entityPhysicalActivity.corsa) corsa = corsa + 1;
            if (entityPhysicalActivity.camminata) camminata = camminata + 1;
            if (entityPhysicalActivity.sport) sport = sport + 1;
            if (entityPhysicalActivity.yoga) yoga = yoga + 1;
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        pieEntries.add(new PieEntry(corpoLibero, 0));
        pieEntries.add(new PieEntry(sport, 1));
        pieEntries.add(new PieEntry(corsa, 2));
        pieEntries.add(new PieEntry(camminata, 3));
        pieEntries.add(new PieEntry(yoga, 4));
        pieEntries.add(new PieEntry(pesi, 5));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(getColor(R.color.seaGreenRipple), Color.parseColor("#ff6767"), getColor(R.color.orangeYellow), Color.parseColor("#92b4f2"), getColor(R.color.backgroundBigSection), Color.parseColor("#99e699"));
        pieDataSet.setSliceSpace(1.5f);
        pieDataSet.setValueTextColor(getColor(R.color.textColor));
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setSliceSpace(10f);

        PieData pieData = new PieData(pieDataSet);

        Legend legend = pieChart.getLegend();
        legend.getEntries();
        legend.setYEntrySpace(10f);
        legend.setWordWrapEnabled(true);
        LegendEntry l1 = new LegendEntry("corpoLibero",Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.seaGreenRipple));
        LegendEntry l2 = new LegendEntry("pesi", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#ff6767"));
        LegendEntry l3 = new LegendEntry("corsa", Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.orangeYellow));
        LegendEntry l4 = new LegendEntry("camminata", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#92b4f2"));
        LegendEntry l5 = new LegendEntry("sport", Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.backgroundBigSection));
        LegendEntry l6 = new LegendEntry("yoga", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#99e699"));

        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4, l5, l6});

        legend.setEnabled(true);

        pieChart.setData(pieData);
    }


    private void setLayoutData(){
        EntityPhysicalActivity entityPhysicalActivity = DBinstance.entityPhysicalActivityDao().findEntityPhysicalActivityByDate(date);
        if (entityPhysicalActivity != null) {
            if (entityPhysicalActivity.corpoLibero) {
                coproLibero.setChecked(true);
            }
            if (entityPhysicalActivity.pesi) {
                pesi.setChecked(true);
            }
            if (entityPhysicalActivity.corsa) {
                corsa.setChecked(true);
            }
            if (entityPhysicalActivity.camminata) {
                camminata.setChecked(true);
            }
            if (entityPhysicalActivity.sport) {
                sport.setChecked(true);
            }
            if (entityPhysicalActivity.yoga) {
                yoga.setChecked(true);
            }
            if (entityPhysicalActivity.time != null){
                time.setText(entityPhysicalActivity.time.toString());
            }
        }
    }

    public void saveData () {
        EntityPhysicalActivity entityPhysicalActivity = DBinstance.entityPhysicalActivityDao().findEntityPhysicalActivityByDate(date);
        //caso1: non esiste la tabella, la setto e la inserisco
        if (entityPhysicalActivity == null) {
            entityPhysicalActivity = new EntityPhysicalActivity();
            entityPhysicalActivity.date = date;
            DBinstance.entityPhysicalActivityDao().insert(entityPhysicalActivity);
        }
        if (coproLibero.isChecked()) {
            entityPhysicalActivity.corpoLibero = true;
        }else {
            entityPhysicalActivity.corpoLibero = false;
        }
        if (pesi.isChecked()) {
            entityPhysicalActivity.pesi = true;
        }else {
            entityPhysicalActivity.pesi = false;
        }
        if (corsa.isChecked()) {
            entityPhysicalActivity.corsa = true;
        }else {
            entityPhysicalActivity.corsa = false;
        }
        if (camminata.isChecked()) {
            entityPhysicalActivity.camminata = true;
        }else {
            entityPhysicalActivity.camminata = false;
        }
        if (sport.isChecked()) {
            entityPhysicalActivity.sport = true;
        }else {
            entityPhysicalActivity.sport = false;
        }
        if (yoga.isChecked()) {
            entityPhysicalActivity.yoga = true;
        }else {
            entityPhysicalActivity.yoga = false;
        }
        if (!TextUtils.isEmpty(time.getText())) {
            entityPhysicalActivity.time = (time.getText().toString());
        }else {
            entityPhysicalActivity.time = "";
        }
        DBinstance.entityPhysicalActivityDao().update(entityPhysicalActivity);
        Utils.generateToast("DATI SALVATI", context);
    }

    private void showChartDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhysicalActivitySection.this);
        alertDialog.setTitle("Salvare il grafico?");

        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pieChart.saveToGallery("Attività fisica" + System.currentTimeMillis()/1000, 50);
                        Utils.generateToast("Grafico salvato", PhysicalActivitySection.this);
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


