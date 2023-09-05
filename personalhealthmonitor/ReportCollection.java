package personalhealthmonitor;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.adapter.MyAdapterReportCollection;
import personalhealthmonitor.model.ModelReportCollection;
import pl.droidsonroids.gif.GifImageView;


public class ReportCollection extends Fragment {

    public static RecyclerView recyclerView;
    public static MyAdapterReportCollection adapter;
    public static Context mContext;
    public static DB DBinstance;
    private static GifImageView noReportCalendar;
    private static List<EntityReports> elementReport;
    public static boolean infoGlob = false;
    public static boolean tempGlob = false;
    public static boolean bloodGlob = false;
    public static boolean painsGlob = false;
    public static boolean activityGlob = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {

        View v = inflater.inflate(R.layout.report_collection, container, false);

        noReportCalendar = v.findViewById(R.id.noReportCalendar);

        mContext = getContext();
        DBinstance = DB.getInstance(mContext);

        recyclerView = v.findViewById(R.id.reportRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        elementReport = DBinstance.entityReportsDao().getReportsList();
        adapter = new MyAdapterReportCollection(mContext, createReportList(elementReport));
        recyclerView.setAdapter(adapter);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        filterReport(infoGlob, tempGlob, bloodGlob, painsGlob, activityGlob);
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ModelReportCollection> createReportList(List<EntityReports> listEntityReport){
        ArrayList<ModelReportCollection> models = new ArrayList<>();

        if (listEntityReport.size() != 0) {
            List<String> dateString = sortReport(listEntityReport);
            for (int i = listEntityReport.size() - 1; i >= 0; i--) {
                ModelReportCollection m = new ModelReportCollection();
                m.setTitle(dateString.get(i));
                models.add(m);
            }
        }

        if (listEntityReport.size() == 0){
            noReportCalendar.setVisibility(View.VISIBLE);
        }else {
            noReportCalendar.setVisibility(View.GONE);
        }

        return models;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static List<String> sortReport(List<EntityReports> entityReportsList){
        int size = entityReportsList.size();
        ArrayList<String> dateString = new ArrayList<String>();
        ArrayList<LocalDate> localDateList = new ArrayList<LocalDate>();
        for (int i = 0; i < size; i++){
            String string = entityReportsList.get(i).date;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void filterReport(boolean info, boolean temp, boolean blood, boolean pains, boolean activity){
        infoGlob = info;
        tempGlob = temp;
        bloodGlob = blood;
        painsGlob = pains;
        activityGlob = activity;

        //riprendo istanza per avere aggiornamento anche in caso di modifiche sul reportVisualization
        elementReport = DBinstance.entityReportsDao().getReportsList();

        List<EntityReports> filtersElementReport = new ArrayList<>(elementReport);

        List<EntityReports> filtersElementReportUpdate = new ArrayList<>();
        for (EntityReports entityReports: filtersElementReport){
            boolean good = true;
            if (info && !entityReports.infoClickable) good = false;
            if (temp && !entityReports.temperatureClickable) good = false;
            if (blood && !entityReports.pressureClickable) good = false;
            if (pains && !entityReports.painsClickable) good = false;
            if (activity && !entityReports.physicalActivityClickable) good = false;

            if (good) filtersElementReportUpdate.add(entityReports);
        }

        adapter = new MyAdapterReportCollection(mContext, createReportList(filtersElementReportUpdate));
        recyclerView.setAdapter(adapter);    }

}