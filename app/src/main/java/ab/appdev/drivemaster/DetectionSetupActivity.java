package ab.appdev.drivemaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DetectionSetupActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private SeekBar sensitivityBar;
    private TextView sensitivityShowingLabel;

    private ImageView qrCodeIV;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

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



        qrCodeIV = findViewById(R.id.idIVQrcode);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(sharedpreferences.getString("BroadcastID", ""), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }

    }


}