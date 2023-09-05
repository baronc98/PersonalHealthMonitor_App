package personalhealthmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifImageView;

public class WelcomePage extends AppCompatActivity {

    private EditText name;
    private EditText age;
//    private EditText weightBody;
//    private EditText heightBody;
    private EditText doctorNumber;
    private GifImageView next;
    private TextView nextTextView;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_page);
        context = getApplicationContext();

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
//        weightBody = findViewById(R.id.weightBody);
//        heightBody = findViewById(R.id.heightBody);
        doctorNumber = findViewById(R.id.doctorNumber);

        next = findViewById(R.id.next);
        nextTextView = findViewById(R.id.nextTextview);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveData();
            }
        });
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveData();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("temperatureMonitoring", false).apply();
        editor.putBoolean("painsMonitoring", false).apply();
        editor.putBoolean("pressureMonitoring", false).apply();

        editor.putString("temperatureMonitoringValue", "37.5").apply();
//        editor.putString("painsMonitoringValue", "").apply();
        editor.putString("pressureMonitoringValue", "150").apply();


        String Name, Age, WeightBody, HeightBody, DoctorNumber;
        Name = name.getText().toString();
        Age = age.getText().toString();
        DoctorNumber = doctorNumber.getText().toString();
//        WeightBody = weightBody.getText().toString();
//        HeightBody = heightBody.getText().toString();

        if (controlEmptyData(Name, Age, DoctorNumber)) {
            editor.putString("name", Name).apply();
            editor.putString("age", Age).apply();
//            editor.putString("weightBody", WeightBody).apply();
//            editor.putString("heightBody", HeightBody).apply();
            editor.putString("doctorNumber", DoctorNumber).apply();
            //setto la fine del primo accesso
            editor.putBoolean("firstAccess", false).apply();
            boolean firstAccess = sharedPreferences.getBoolean("firstAccess", true);
            finish();
        }

    }

    private boolean controlEmptyData(String Name, String Age, String doctorNumber){
        boolean good = true;
        if (Name.equals("") || Age.equals("") || doctorNumber.equals("")){
            good = false;
            Utils.runAnimationView(name);
            Utils.runAnimationView(age);
            Utils.runAnimationView(this.doctorNumber);
            Utils.generateToast("Compila tutti i campi per continuare", context);
        }
        return good;
    }

}
