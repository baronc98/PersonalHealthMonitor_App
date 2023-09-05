package personalhealthmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cdflynn.android.library.crossview.CrossView;
import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.DB.UtilsDB;
import personalhealthmonitor.adapter.MyAdapterTodayReport;
import personalhealthmonitor.model.ModelTodayReport;

public class TodayReport extends Fragment {

    private CrossView mCrossView;
    private boolean menuOpen = false;
    private RecyclerView recyclerView;
    private MyAdapterTodayReport adapter;
    private LayoutAnimationController layoutAnimationController;
    private Context mContext;
    private boolean firstCreation = true;
    private ArrayList<ModelTodayReport> cards = new ArrayList<>();
    //private Menu menu;
    private CardView cardCall;
    private CardView virusInfo;
    public DB DBinstance;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    public String date;
    private CardView ytLink;
    private ImageView info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {

        View v = inflater.inflate(R.layout.today_report, container, false);

        mContext = getContext();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());

        mCrossView = v.findViewById(R.id.sample_cross_view);
        mCrossView.setOnClickListener(mCrossViewClickListener);

        recyclerView = v.findViewById(R.id.cardRecycleview);

        initRecycleView();

        deleteAnimation(layoutAnimationController);

        //menu = v.findViewById(R.id.menu_today_report);

        cardCall = (CardView) v.findViewById(R.id.cardCall);

        cardCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callDoctorPage = new Intent(getContext(), CallDoctor.class);
                startActivity(callDoctorPage);
            }
        });

        virusInfo = (CardView) v.findViewById(R.id.virusInfo);

        virusInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent virusInfoPage = new Intent(getContext(), VirusInfo.class);
                startActivity(virusInfoPage);
            }
        });

        DBinstance = DB.getInstance(getContext());

        if (firstCreation) {
            createFirstModel();
            firstCreation = false;
        }

        ytLink = v.findViewById(R.id.ytLink);
        ytLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                watchYoutubeVideo("results?search_query=allenamento+casasalingo");
            }
        });

        info = v.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
                dialog.setTitle(getString(R.string.titleDialog));
                dialog.setMessage(getString(R.string.introPhysicalActivity));
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                dialog.show();
            }
        });

        return v;
    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new MyAdapterTodayReport(mContext, cards, date);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void deleteAnimation(LayoutAnimationController layoutAnimationController){
        if (!firstCreation){
            layoutAnimationController = null;
            recyclerView.setLayoutAnimation(layoutAnimationController);
        }
    }

    private void runLayoutAnimation(RecyclerView recyclerView){
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    private final View.OnClickListener mCrossViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCrossView.toggle();
            Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.inflate(R.menu.menu_today_report);


            if (!menuOpen) {
                popupMenu.show();
                menuOpen = true;
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.info) {
                        controlSectionCreated(1);
                    }
                    if (id == R.id.temperature) {
                        controlSectionCreated(2);
                    }
                    if (id == R.id.blood) {
                        controlSectionCreated(3);
                    }
//                    if (id == R.id.glycemicIndex) {
//                        controlSectionCreated(4);
//                    }
                    if (id == R.id.pains) {
                        controlSectionCreated(5);
                    }
//                    if (id == R.id.drug) {
//                        controlSectionCreated(6);
//                    }
                    if (id == R.id.physicalActivity) {
                        controlSectionCreated(7);
                    }

                    return true;
                }
            });

            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    mCrossView.toggle();
                    menuOpen = false;
                }
            });
        }
    };

    //salvo la creazione dell'entita' corrispondente, se non esiste ancora il report corrispondente
    //lo creo a catena richiamando CreateReport()
    private void controlSectionCreated(int n){
        EntityReports currentEntityReports;
        //controllo se non esiste il report lo creo
        if (!UtilsDB.checkIfExistReport(date, DBinstance)){
            UtilsDB.createReport(date, DBinstance);
        }
        //prendo il report giusto
        currentEntityReports = UtilsDB.getReportByDate(date, DBinstance);
        switch (n){
            case 1:
                if (!currentEntityReports.infoClickable) {
                    currentEntityReports.infoClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione informazioni generali", mContext);
                }
                break;
            case 2:
                if (!currentEntityReports.temperatureClickable) {
                    currentEntityReports.temperatureClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione temperatura corporea", mContext);
                }
                break;
            case 3:
                if (!currentEntityReports.pressureClickable) {
                    currentEntityReports.pressureClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione pressione", mContext);
                }
                break;
            case 4:
                if (!currentEntityReports.glycemicIndexClickable) {
                    currentEntityReports.glycemicIndexClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione indice glicemico", mContext);
                }
                break;
            case 5:
                if (!currentEntityReports.painsClickable) {
                    currentEntityReports.painsClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione dolori", mContext);
                }
                break;
            case 6:
                if (!currentEntityReports.drugClickable) {
                    currentEntityReports.drugClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione medicine", mContext);
                }
                break;
            case 7:
                if (!currentEntityReports.physicalActivityClickable) {
                    currentEntityReports.physicalActivityClickable = true;
                    DBinstance.entityReportsDao().update(currentEntityReports);
                    emptyRecycleView();
                    createFirstModel();
                    runLayoutAnimation(recyclerView);
                }else {
                    Utils.generateToast("Hai gia' creato la sezione attività fisica", mContext);
                }
                break;
        }
    }


    public void emptyRecycleView(){
        cards.clear();
//        adapter = new MyAdapterTodayReport(mContext, cards, date);
//        recyclerView.setAdapter(adapter);
    }

    private void createFirstModel(){
        EntityReports entityReports = DBinstance.entityReportsDao().findReportByDate(date);
        if(entityReports != null) {
            if (entityReports.infoClickable) createModel(1);
            if (entityReports.temperatureClickable) createModel(2);
            if (entityReports.pressureClickable) createModel(3);
            if (entityReports.glycemicIndexClickable) createModel(4);
            if (entityReports.painsClickable) createModel(5);
            if (entityReports.drugClickable) createModel(6);
            if (entityReports.physicalActivityClickable) createModel(7);
        }
    }

    public void createModel(int n){
        ModelTodayReport m = new ModelTodayReport();

        String title = "";
        switch (n){
            case 1:
                title = getString(R.string.info);
                break;
            case 2:
                title = getString(R.string.temperature);
                break;
            case 3:
                title = getString(R.string.blood);
                break;
            case 4:
                title = getString(R.string.glycemicIndex);
                break;
            case 5:
                title = getString(R.string.pains);
                break;
            case 6:
                title = getString(R.string.drug);
                break;
            case 7:
                title = getString(R.string.physicalActivity);
                break;
        }
        m.setTitle(title);

        switch (n){
            case 1:
                m.setImg(R.drawable.info);
                break;
            case 2:
                m.setImg(R.drawable.thermometer);
                break;
            case 3:
                m.setImg(R.drawable.blood);
                break;
            case 4:
                m.setImg(R.drawable.glycemicindex);
                break;
            case 5:
                m.setImg(R.drawable.pain);
                break;
            case 6:
                m.setImg(R.drawable.drug);
                break;
            case 7:
                m.setImg(R.drawable.physicalactivity);
                break;
        }

        cards.add(m);
        adapter = new MyAdapterTodayReport(mContext, cards, date);
        recyclerView.setAdapter(adapter);
    }

     ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
         @Override
         public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
             return false;
         }

         @Override
         public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
             int item = viewHolder.getAdapterPosition();
             ModelTodayReport modelTodayReport = cards.get(item);
             cards.remove(item);
             Utils.deleteSectionFromDB(DBinstance, modelTodayReport, date, mContext);
             adapter.notifyDataSetChanged();
         }
     };

    @Override
    public void onResume() {
        if (DBinstance.entityReportsDao().findReportByDate(date) == null) emptyRecycleView();
        super.onResume();
    }
}