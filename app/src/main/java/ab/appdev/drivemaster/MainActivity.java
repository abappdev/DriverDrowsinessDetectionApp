package ab.appdev.drivemaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import ab.appdev.drivemaster.facedetection.DriverFaceDetection;

public class MainActivity extends AppCompatActivity {


    private SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference("/abhishek/").child("INFO").setValue("STARTED");

        Intent i = new Intent(getApplicationContext(), DriverFaceDetection.class);

        if(sharedpreferences.getString(Configurable.SENSITIVITY, "").equals(""))
            sharedpreferences.edit().putString(Configurable.SENSITIVITY, "5").apply();

        i.putExtra(Configurable.SENSITIVITY, sharedpreferences.getString(Configurable.SENSITIVITY, "0"));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
        finish();

    }
}