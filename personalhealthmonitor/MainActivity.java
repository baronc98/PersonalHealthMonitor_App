package personalhealthmonitor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityReports;
import personalhealthmonitor.SimpleGestureFilter.SimpleGestureListener;
import personalhealthmonitor.adapter.MyAdapterReportCollection;
import personalhealthmonitor.notification.NotificationHelper;

public class MainActivity extends AppCompatActivity implements SimpleGestureListener {

    private static BottomNavigationView bottomNavigationView;
    private static TodayReport todayReportFragment;
    private ReportCollection reportCollection;
    private static Fragment selectedFragment = null;
    private SimpleGestureFilter detector;
    private DB DBinstance;
    private Context context;
    private NotificationHelper notificationHelper;
    public ImageView notificationBell;
    public ImageView filters;
    private ImageView settings;
    private static ImageView calendarAdd;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Boolean menuOpen;
    private PopupMenu popupMenu;

    private static final String SHARED_PREFS = "sharedPrefs";
    private Boolean firstAccess;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        context = getApplication().getApplicationContext();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstAccess = sharedPreferences.getBoolean("firstAccess", true);
        if (firstAccess) welcomeDialog(context);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        todayReportFragment = new TodayReport();
        selectedFragment = todayReportFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        reportCollection = new ReportCollection();

        detector = new SimpleGestureFilter(MainActivity.this, this);

        DBinstance = DB.getInstance(this);

        notificationHelper = new NotificationHelper(context);

        menuOpen = false;


        filters = findViewById(R.id.filters);
        filters.setVisibility(View.INVISIBLE);


        resetPopupmenu();

        filters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMenu();
            }
        });

        notificationBell = findViewById(R.id.notificationBell);
        if (Connect.getMyBoolean()){
            notificationBell.setImageDrawable(getDrawable(R.drawable.notificationbell2));
            notificationBell.setColorFilter(getColor(R.color.redChart));
        }else {
            notificationBell.setImageDrawable(getDrawable(R.drawable.notificationbell));
            notificationBell.setColorFilter(getColor(R.color.seaGreenPrimary));
        }
            notificationBell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Connect.getMyBoolean()) Connect.setMyBoolean(false);

                Intent activityChangeIntent = new Intent(MainActivity.this, NotificationNote.class);

                // currentContext.startActivity(activityChangeIntent);

                startActivity(activityChangeIntent);
            }
        });

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(MainActivity.this, Settings.class);

                // currentContext.startActivity(activityChangeIntent);

                startActivity(activityChangeIntent);
            }
        });

        calendar = Calendar.getInstance();

        calendarAdd = findViewById(R.id.calendarAdd);
        calendarAdd.setVisibility(View.INVISIBLE);
        calendarAdd.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   showCalendar();
               }
           });

        Connect.addMyBooleanListener(new ConnectionBooleanChangedListener() {
            @Override
            public void OnMyBooleanChanged() {
                if (Connect.getMyBoolean()){
                    notificationBell.setImageDrawable(getDrawable(R.drawable.notificationbell2));
                    notificationBell.setColorFilter(getColor(R.color.redChart));
                }else {
                    notificationBell.setImageDrawable(getDrawable(R.drawable.notificationbell));
                    notificationBell.setColorFilter(getColor(R.color.seaGreenPrimary));
                }
            }
        });

    }

    private void showCalendar(){
        final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint("SimpleDateFormat")
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            DatePicker datePicker = datePickerDialog.getDatePicker();
                            String stringDate = Utils.buildDate(datePicker.getDayOfMonth(), (datePicker.getMonth() + 1), datePicker.getYear());
                            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date todayDate = calendar.getTime();
                            Date date = null;
                            try {
                                date = dateFormat.parse(stringDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (date.after(todayDate)) {
                                Utils.generateToast("Non prevedi il futuro... per ora", MainActivity.this);
                            } else {
                                addReport(stringDate);
                                Utils.generateToast("Report aggiunto con successo", MainActivity.this);
                            }
                        }
                    }
                });

        datePickerDialog.show();
        datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.seaGreenPrimary));
        datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.seaGreenRipple));
    }


    private void welcomeDialog(Context c){
        Intent newPage = new Intent(c, WelcomePage.class);
//        newPage.putExtra("date", date);
        newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(newPage);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.bottom1:
                            selectedFragment = todayReportFragment;
                            calendarAdd.setVisibility(View.INVISIBLE);
                            filters.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.bottom2:
                            selectedFragment = reportCollection;
                            calendarAdd.setVisibility(View.VISIBLE);
                            filters.setVisibility(View.VISIBLE);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;

                }
            };


    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        //Detect the swipe gestures
        String showToastMessage = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                selectedFragment = todayReportFragment;
                bottomNavigationView.setSelectedItemId(R.id.bottom1);
                calendarAdd.setVisibility(View.INVISIBLE);
                filters.setVisibility(View.INVISIBLE);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                selectedFragment = reportCollection;
                bottomNavigationView.setSelectedItemId(R.id.bottom2);
                calendarAdd.setVisibility(View.VISIBLE);
                filters.setVisibility(View.VISIBLE);
                break;
//            case SimpleGestureFilter.SWIPE_DOWN:
//                showToastMessage = "You have Swiped Down.";
//                break;
//            case SimpleGestureFilter.SWIPE_UP:
//                showToastMessage = "You have Swiped Up.";
//                break;

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//        switch (selectedFragment){
//            case "todayReportFragment":
//                break;
//            case "reportCollection":
//                break;
//        }
    }

    @Override
    public void onDoubleTap() {

    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == reportCollection){
            selectedFragment = todayReportFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom1);
            calendarAdd.setVisibility(View.INVISIBLE);
            filters.setVisibility(View.INVISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {}
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addReport(String date){
        if (DBinstance.entityReportsDao().findReportByDate(date) == null){
            EntityReports entityReports = new EntityReports(date);
            DBinstance.entityReportsDao().insert(entityReports);
            ReportCollection.recyclerView.setLayoutManager(new LinearLayoutManager(ReportCollection.mContext));
            ReportCollection.adapter = new MyAdapterReportCollection(ReportCollection.mContext, ReportCollection.createReportList(DBinstance.entityReportsDao().getReportsList()));
            ReportCollection.recyclerView.setAdapter(ReportCollection.adapter);

            resetPopupmenu();
        }else {
            Utils.generateToast("Report gia' creato, seleziona un'altra data", context);
        }
    }

    private void showMenu(){

        if (!menuOpen) {
            popupMenu.show();
            menuOpen = true;
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());

                MenuItem info = popupMenu.getMenu().findItem(R.id.infoFilter);
                MenuItem temp = popupMenu.getMenu().findItem(R.id.tempFilter);
                MenuItem blood = popupMenu.getMenu().findItem(R.id.bloodFilter);
                MenuItem pains = popupMenu.getMenu().findItem(R.id.painsFilter);
                MenuItem activity = popupMenu.getMenu().findItem(R.id.activityFilter);

                ReportCollection.filterReport(info.isChecked(), temp.isChecked(), blood.isChecked(), pains.isChecked(), activity.isChecked());
                return true;
            }
        });


        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                menuOpen = false;
            }
        });
    }

    private void resetPopupmenu(){
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        popupMenu = new PopupMenu(wrapper, filters);
        popupMenu.inflate(R.menu.menu_filters);

        ReportCollection.infoGlob = false;
        ReportCollection.tempGlob = false;
        ReportCollection.bloodGlob = false;
        ReportCollection.painsGlob = false;
        ReportCollection.activityGlob = false;

    }

}

