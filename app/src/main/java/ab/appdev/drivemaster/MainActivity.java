package ab.appdev.drivemaster;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ab.appdev.drivemaster.facedetection.DriverFaceDetection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(getApplicationContext(), DriverFaceDetection.class);

        i.putExtra(Configurable.SENSITIVITY, "" + 2);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
        finish();

    }
}