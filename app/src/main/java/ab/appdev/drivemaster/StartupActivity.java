package ab.appdev.drivemaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import ab.appdev.drivemaster.facedetection.DriverFaceDetection;

public class StartupActivity extends AppCompatActivity {


    static Information information;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        information = new Information();

        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        if (sharedpreferences.getString("BroadcastID", "").equals(""))
            sharedpreferences.edit().putString("BroadcastID", AESUtils.encrypt("trusttext" + AESUtils.sizedString(30))).apply();


        if (sharedpreferences.getString(Configurable.SENSITIVITY, "").equals(""))
            sharedpreferences.edit().putString(Configurable.SENSITIVITY, "3").apply();


        Information.setBroadcastId(AESUtils.decrypt(sharedpreferences.getString("BroadcastID", "")));
        FirebaseDatabase.getInstance().getReference("/" + Information.getBroadcastId() + "/").child("INFO").setValue("STARTED");

        Intent intent;

        switch (sharedpreferences.getString("RGR", "")) {
            case "RECEIVER":
                intent = new Intent(getApplicationContext(), SecondaryDeviceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case "SENDER":
                intent = new Intent(getApplicationContext(), DriverFaceDetection.class);
                intent.putExtra(Configurable.SENSITIVITY, sharedpreferences.getString(Configurable.SENSITIVITY, "0"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            default:
                intent = new Intent(getApplicationContext(), ModePickerActivity.class);
                break;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            startActivity(intent);
            finish();        //Do something after 100ms
        }, 1000);

    }
}