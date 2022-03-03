package ab.appdev.drivemaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import ab.appdev.drivemaster.facedetection.DriverFaceDetection;

public class MainActivity extends AppCompatActivity {


    private SharedPreferences sharedpreferences;
    static Information information = new Information();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        if (sharedpreferences.getString("BroadcastID", "").equals("")) {
            sharedpreferences.edit().putString("BroadcastID", AESUtils.encrypt(AESUtils.sizedString(30))).apply();
        }

        if (sharedpreferences.getString(Configurable.SENSITIVITY, "").equals(""))
            sharedpreferences.edit().putString(Configurable.SENSITIVITY, "5").apply();


        information.setBroadcastId(AESUtils.decrypt(sharedpreferences.getString("BroadcastID", "")));


        FirebaseDatabase.getInstance().getReference("/" + information.getBroadcastId() + "/").child("INFO").setValue("STARTED");

        Intent i = new Intent(getApplicationContext(), DriverFaceDetection.class);


        i.putExtra(Configurable.SENSITIVITY, sharedpreferences.getString(Configurable.SENSITIVITY, "0"));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            startActivity(i);
            finish();        //Do something after 100ms
        }, 1000);


    }
}