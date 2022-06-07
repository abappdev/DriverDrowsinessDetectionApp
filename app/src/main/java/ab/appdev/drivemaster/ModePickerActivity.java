package ab.appdev.drivemaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ModePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_picker);
    }

    public void qrShowOpen(View view) { //car
        Intent intent = new Intent(getApplicationContext(), QRShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void qrScanOpen(View view) { //personal
        Intent intent = new Intent(getApplicationContext(), QRScanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void onOpenInfo(View view) {
        startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));

    }

}