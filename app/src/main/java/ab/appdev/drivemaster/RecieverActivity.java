package ab.appdev.drivemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ab.appdev.drivemaster.facedetection.DriverFaceDetection;

public class RecieverActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    static Information information = new Information();

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;
    TextView info;
     SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase.getReference("/" + information.getBroadcastId() + "/");
        info = findViewById(R.id.info);
        info.setText("Hello");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // after getting the value we are setting
                // our value to our text view in below line.
                info.setText(Objects.requireNonNull(snapshot.child("INFO").getValue()).toString());

                if(Objects.requireNonNull(snapshot.child("INFO").getValue()).toString().equals("00000000000000")){
                    info.setText("WARNING");
                alert_box();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(RecieverActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
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
            dig = new AlertDialog.Builder(RecieverActivity.this)
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


}