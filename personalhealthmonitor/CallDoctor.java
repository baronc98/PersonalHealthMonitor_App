package personalhealthmonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.skyfishjy.library.RippleBackground;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CallDoctor extends AppCompatActivity {

    private RippleBackground rippleBackground;
    private boolean animationRunning = false;
    private Intent callIntent;
    Timer timer = new Timer();
    private ImageView modify;
    private static final int REQUEST_PHONE_CALL = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.call_doctor);

        modify = findViewById(R.id.modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showNumberDialog();
            }

        });

        rippleBackground = (RippleBackground)findViewById(R.id.content);
        ImageView imageView = (ImageView)findViewById(R.id.callImage);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                if (!animationRunning) {
                    if (ContextCompat.checkSelfPermission(CallDoctor.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CallDoctor.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                        rippleBackground.stopRippleAnimation();
                    } else {
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                calling();
                            }
                        };
                        long delay = TimeUnit.SECONDS.toMillis(2);
                        timer.schedule(task, delay);
                    }
                } else {
                    rippleBackground.stopRippleAnimation();
                }
                animationRunning = !animationRunning;
            }
        });

    }

    private void showNumberDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CallDoctor.this);
        alertDialog.setTitle("Nuovo numero di telefono");
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        alertDialog.setMessage("Numero attuale: " + sharedPreferences.getString("doctorNumber", ""));

        final EditText input = new EditText(CallDoctor.this);
        input.setHint("Nuovo numero...");
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.requestLayout();
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.modify);

        alertDialog.setPositiveButton("Salva",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = input.getText().toString();
                        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("doctorNumber", number).apply();
                        Utils.generateToast("Numero modificato", CallDoctor.this);
                    }
                });

        alertDialog.setNegativeButton("Chiudi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialogCreate = alertDialog.create();
        alertDialogCreate.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialogCreate.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.seaGreenRipple));
                alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.seaGreenPrimary));
            }
        });

        alertDialogCreate.show();

    }

    private void calling(){

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String number =  sharedPreferences.getString("doctorNumber", "");
        callIntent =  new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+ number));
        startActivity(callIntent);
    }

}
