package personalhealthmonitor;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityNotificationNote;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.model.ModelTodayReport;

public class Utils {

    public static void generateToast(String s, Context c){
        Toast toast = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.toast);
        toast.show();
    }

//    public static void generateToastBold(String s, Context c){
//        Toast toast = Toast.makeText(c, s, Toast.LENGTH_SHORT);
//        View toastView = toast.getView();
//        toastView.setBackgroundResource(R.drawable.toast);
//        toast.
//        toast.show();
//    }


    public static void setBarChartLayout(BarChart barChart){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        LimitLine line = new LimitLine(37f);
        barChart.getAxisLeft().addLimitLine(line);
        barChart.animateXY(2500, 2200);
        barChart.getDescription().setText("");
        barChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        barChart.getAxisLeft().setAxisMinimum(35);
        barChart.getAxisLeft().setAxisMaximum(40);
        barChart.getXAxis().setEnabled(false);
    }


    public static void setLineChartLayout(LineChart lineChart){
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateXY(2200, 1900);
    }

    public static void setPieChartLayout(PieChart pieChart){
        pieChart.getLegend().setEnabled(true);
        pieChart.animateXY(2200, 1900);
        pieChart.getDescription().setText("");
    }

    public static boolean deleteSectionFromDB(DB DBinstance, ModelTodayReport modelTodayReport, String date, Context context) {
        boolean bool = false;
        //eliminare sezione specifica con data, settare creata nel report a false
        String section = modelTodayReport.getTitle();
        EntityReports entityReports = DBinstance.entityReportsDao().findReportByDate(date);
        if (section.equals("Temperatura Corporea")) {
            DBinstance.entityTemperatureDao().deleteTemperatureByDate(date);
            entityReports.temperatureClickable = false;
            DBinstance.entityReportsDao().update(entityReports);
        }

        if (section.equals("Informazioni Generali")) {
            DBinstance.entityInfoDao().deleteInfoByDate(date);
            entityReports.infoClickable = false;
            DBinstance.entityReportsDao().update(entityReports);
        }

        if (section.equals("Dolori")) {
            DBinstance.entityPainsDao().deletePainsByDate(date);
            entityReports.painsClickable = false;
            DBinstance.entityReportsDao().update(entityReports);
        }

        if (section.equals("Attività Fisica")) {
            DBinstance.entityPhysicalActivityDao().deletePhysicalActivityByDate(date);
            entityReports.physicalActivityClickable = false;
            DBinstance.entityReportsDao().update(entityReports);
        }

        if (section.equals("Valori Del Sangue")) {
            DBinstance.entityBloodDao().deleteBloodByDate(date);
            entityReports.pressureClickable = false;
            DBinstance.entityReportsDao().update(entityReports);
        }

        //controllo se tutte le sezioni sono state eliminate
        if (!entityReports.infoClickable && !entityReports.temperatureClickable && !entityReports.painsClickable  && !entityReports.pressureClickable && !entityReports.glycemicIndexClickable && !entityReports.drugClickable && !entityReports.physicalActivityClickable){
            DBinstance.entityReportsDao().deleteReportByDate(date);
            bool = true;
        }

        Utils.generateToast("Sezione eliminata", context);
        return bool;
    }

    public static void addNotificationNote(String title, String text, String type, Context context){
        EntityNotificationNote entityNotificationNote = new EntityNotificationNote();
        entityNotificationNote.title = title;
        entityNotificationNote.text = text;
        entityNotificationNote.type = type;
        DB.getInstance(context).entityNotificationNoteDao().insert(entityNotificationNote);
        Connect.setMyBoolean(true);
    }

    public static String buildDate(int day, int month, int year){
        String Day, Month;
        if (day < 10){
            Day = "0".concat(day + "/");
        }else {
            Day = day + "/";
        }
        if (month < 10){
            Month = "0".concat(month + "/");
        }else {
            Month = month + "/";
        }
        return Day + Month + year;
    }

    public static String buildHour(int hour, int minute){
        String Hour, Minute;
        if (hour < 10){
            Hour = "0".concat(hour + ":");
        }else {
            Hour = hour + ":";
        }
        if (minute < 10){
            Minute = "0".concat(minute + "");
        }else {
            Minute = minute + "";
        }
        return Hour + Minute;
    }

    public static void runAnimationView(View view){
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .repeat(1)
                .playOn(view);
    }

}
