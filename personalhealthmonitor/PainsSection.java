package personalhealthmonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import personalhealthmonitor.Connect;
import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityPains;
import personalhealthmonitor.R;
import personalhealthmonitor.Utils;

public class PainsSection extends AppCompatActivity {

    private PieChart pieChart;
    public Context context;
    public DB DBinstance;
    private String date;
    private Button saveInfo;
    private CheckBox head, back, eyes, neck, arts, belly;
    private EditText otherPains;
    private ImageView chartButton;
    private ImageView saveIMG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pains_section);

        context = getApplicationContext();

        DBinstance = DB.getInstance(context);

        date = getIntent().getStringExtra("date");

        head = findViewById(R.id.headPains);
        back = findViewById(R.id.backPains);
        eyes = findViewById(R.id.eyesPains);
        neck = findViewById(R.id.neckPains);
        arts = findViewById(R.id.artsPains);
        belly = findViewById(R.id.bellyPains);
        otherPains = findViewById(R.id.otherPains);

        pieChart = findViewById(R.id.painsChart);
        pieChart.setNoDataText("");
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setBackgroundColor(getResources().getColor(R.color.ghostwhite));
        pieChart.setClickable(true);

        saveIMG = findViewById(R.id.saveIMGpains);
        saveIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheckWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PainsSection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    showChartDialog();
                }
            }
        });

        chartButton = findViewById(R.id.painsChartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                showChart(pieChart);
            }
        });

        saveInfo = findViewById(R.id.savePainsSection);
        saveInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveData();
                showChart(pieChart);
            }
        });

        setLayoutData();

    }

    private void showChart(PieChart pieChart){
        Utils.setPieChartLayout(pieChart);

        setChartData(pieChart);
    }

    private void setChartData(PieChart pieChart) {
        List<EntityPains> painsElementChart = DBinstance.entityPainsDao().getAllEntityPains();
        int size = painsElementChart.size();

        if (size == 0){
            Utils.generateToast("Non ci sono dati da mostrare", context);
            return;
        }

        int head = 0, arts = 0, belly = 0, eyes = 0, back = 0, neck = 0;

        for (int j = 0; j < size; j++) {
            EntityPains entityPains = painsElementChart.get(j);
            if (entityPains.arts) arts = arts + 1;
            if (entityPains.neck) neck = neck + 1;
            if (entityPains.head) head = head + 1;
            if (entityPains.belly) belly = belly + 1;
            if (entityPains.eyes) eyes = eyes + 1;
            if (entityPains.back) back = back + 1;
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        pieEntries.add(new PieEntry(arts, 0));
        pieEntries.add(new PieEntry(neck, 1));
        pieEntries.add(new PieEntry(head, 2));
        pieEntries.add(new PieEntry(belly, 3));
        pieEntries.add(new PieEntry(eyes, 4));
        pieEntries.add(new PieEntry(back, 5));

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
        LegendEntry l1 = new LegendEntry("Arti",Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.seaGreenRipple));
        LegendEntry l2 = new LegendEntry("Collo", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#ff6767"));
        LegendEntry l3 = new LegendEntry("Testa", Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.orangeYellow));
        LegendEntry l4 = new LegendEntry("Pancia", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#92b4f2"));
        LegendEntry l5 = new LegendEntry("Occhi", Legend.LegendForm.CIRCLE,10f,2f,null, getColor(R.color.backgroundBigSection));
        LegendEntry l6 = new LegendEntry("Schiena", Legend.LegendForm.CIRCLE,10f,2f,null, Color.parseColor("#99e699"));

        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4, l5, l6});

        legend.setEnabled(true);

        pieChart.setData(pieData);
    }


    private void setLayoutData(){
        EntityPains entityPains = DBinstance.entityPainsDao().findEntityPainsByDate(date);
        if (entityPains != null) {
            if (entityPains.head) {
                head.setChecked(true);
            }
            if (entityPains.eyes) {
                eyes.setChecked(true);
            }
            if (entityPains.back) {
                back.setChecked(true);
            }
            if (entityPains.belly) {
                belly.setChecked(true);
            }
            if (entityPains.arts) {
                arts.setChecked(true);
            }
            if (entityPains.neck) {
                neck.setChecked(true);
            }
            if (entityPains.otherPains != null){
                otherPains.setText(entityPains.otherPains.toString());
            }
        }
    }

        public void saveData () {
            EntityPains entityPains = DBinstance.entityPainsDao().findEntityPainsByDate(date);
            //caso1: non esiste la tabella, la setto e la inserisco
            if (entityPains == null) {
                entityPains = new EntityPains();
                entityPains.date = date;
                DBinstance.entityPainsDao().insert(entityPains);
            }
            if (head.isChecked()) {
                entityPains.head = true;
            }else {
                entityPains.head = false;
            }
            if (eyes.isChecked()) {
                entityPains.eyes = true;
            }else {
                entityPains.eyes = false;
            }
            if (arts.isChecked()) {
                entityPains.arts = true;
            }else {
                entityPains.arts = false;
            }
            if (neck.isChecked()) {
                entityPains.neck = true;
            }else {
                entityPains.neck = false;
            }
            if (belly.isChecked()) {
                entityPains.belly = true;
            }else {
                entityPains.belly = false;
            }
            if (back.isChecked()) {
                entityPains.back = true;
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("painsMonitoring", false))Utils.addNotificationNote("Attenzione!", "Ho riscontrato alcuni valori sballati in data " + date + ". Se percepisci dolore alle ossa meglio contattare il medico di base per ulteriori accertamenti. Per informationi consulta la sezione anti-virus.", "doctor", context);
                Connect.setMyBoolean(true);
            }else {
                entityPains.back = false;
            }
            if (!TextUtils.isEmpty(otherPains.getText())) {
                entityPains.otherPains = (otherPains.getText().toString());
            }else {
                entityPains.otherPains = "";
            }
            DBinstance.entityPainsDao().update(entityPains);
            Utils.generateToast("DATI SALVATI", context);
        }

    private void showChartDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PainsSection.this);
        alertDialog.setTitle("Salvare il grafico?");

        alertDialog.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pieChart.saveToGallery("Dolori" + System.currentTimeMillis()/1000, 50);
                        Utils.generateToast("Grafico salvato", PainsSection.this);
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


