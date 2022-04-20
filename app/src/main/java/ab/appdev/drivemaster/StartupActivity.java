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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedpreferences = getSharedPreferences(Configurable.SHAREDNAME, Context.MODE_PRIVATE);

        if (sharedpreferences.getString(Configurable.BRODCASTID, "").equals(""))
            sharedpreferences.edit().putString(Configurable.BRODCASTID, AESUtils.encrypt("trusttext" + AESUtils.sizedString(30))).apply();


        if (sharedpreferences.getString(Configurable.SENSITIVITY, "").equals(""))
            sharedpreferences.edit().putString(Configurable.SENSITIVITY, Configurable.DEFAULT_SENSITIVITY).apply();


        Information.setBroadcastId(AESUtils.decrypt(sharedpreferences.getString(Configurable.BRODCASTID, "")));
        FirebaseDatabase.getInstance().getReference().child("").setValue(null);

        FirebaseDatabase.getInstance().getReference("/" + Information.getBroadcastId() + "/").child("INFO").setValue("STARTED");

        Intent intent;

        switch (sharedpreferences.getString(Configurable.APPMODE, "")) {
            case Configurable.RECEIVER:
                intent = new Intent(getApplicationContext(), SecondaryDeviceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case Configurable.SENDER:
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