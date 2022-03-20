package ab.appdev.drivemaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SecondaryDeviceActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;
    TextView info;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        info = findViewById(R.id.info);

        sharedpreferences = getSharedPreferences(Configurable.SHAREDNAME, Context.MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase.getReference("/" + Information.getBroadcastId() + "/");
        info.setText("Hello");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                try {

                    switch (Objects.requireNonNull(snapshot.child("INFO").getValue()).toString()) {
                        case "00000000000000":
                            info.setText("WARNING");
                            alert_box();
                            break;
                        case "STARTED":
                            info.setText("STARTED");
                            break;
                        default:
                            info.setText("UNDER DETECTION MODE");

                    }
                } catch (Exception E) {
                    Toast.makeText(SecondaryDeviceActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();

                }

                // after getting the value we are setting
                // our value to our text view in below line.


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(SecondaryDeviceActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void play_media() {
        stop_playing();
        mp = MediaPlayer.create(this, R.raw.alarm);
        mp.start();
    }

    public void stop_playing() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private MediaPlayer mp;

    public void alert_box() {
        play_media();
        runOnUiThread(() -> {
            play_media();

            AlertDialog dig;
            dig = new AlertDialog.Builder(SecondaryDeviceActivity.this)
                    .setTitle("Drowsy Alert !!!")
                    .setMessage("Tracker suspects that the driver is experiencing Drowsiness")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        stop_playing();
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            dig.setOnDismissListener(dialog -> {
                stop_playing();
            });
        });


    }

    public void deRegister(View view) {
        //  sharedpreferences.edit().putString("BroadcastID", AESUtils.encrypt("trusttext" + AESUtils.sizedString(30))).apply();
        sharedpreferences.edit().putString(Configurable.APPMODE, "").apply();
        Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
        finish();
    }

}