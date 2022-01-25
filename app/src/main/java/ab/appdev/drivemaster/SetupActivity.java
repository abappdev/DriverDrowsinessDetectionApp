package ab.appdev.drivemaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private SeekBar sensitivityBar;
    private TextView sensitivityShowingLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityShowingLabel = findViewById(R.id.sensitivity);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        sensitivityShowingLabel.setText(sharedpreferences.getString(Configurable.SENSITIVITY, "5"));


        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedpreferences.edit().putString(Configurable.SENSITIVITY, progress + "").apply();

                if (0 <= progress & progress <= 3) {
                    //sensitivityBar.color
                    sensitivityShowingLabel.setText("LOWER IS MORE SAFE");
                    sensitivityShowingLabel.setBackgroundColor(Color.GREEN);
                    sensitivityShowingLabel.setTextColor(Color.BLACK);
                } else if (4 <= progress && progress <= 8) {
                    sensitivityShowingLabel.setText("LOOKING SAFE FOR YOU");
                    sensitivityShowingLabel.setBackgroundColor(Color.YELLOW);
                    sensitivityShowingLabel.setTextColor(Color.BLACK);
                } else {
                    sensitivityShowingLabel.setText("NO SAFETY AT ALL");
                    sensitivityShowingLabel.setBackgroundColor(Color.RED);
                    sensitivityShowingLabel.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Set Sensitivity", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sensitivityBar.setProgress(Integer.parseInt(sharedpreferences.getString(Configurable.SENSITIVITY, "5")));

    }
}