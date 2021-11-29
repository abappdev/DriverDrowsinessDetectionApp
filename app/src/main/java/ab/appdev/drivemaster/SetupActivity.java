package ab.appdev.drivemaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SetupActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        sharedpreferences.edit().putString(Configurable.SENSITIVITY, "50").apply();
    }
}