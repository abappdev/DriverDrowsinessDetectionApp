package ab.appdev.drivemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RecieverActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    static Information information = new Information();

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;
    TextView info;
    private SharedPreferences sharedpreferences;

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(RecieverActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}