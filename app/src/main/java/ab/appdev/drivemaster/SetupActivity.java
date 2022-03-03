package ab.appdev.drivemaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import java.util.Objects;

public class SetupActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private SeekBar sensitivityBar;
    private TextView sensitivityShowingLabel;

    private ImageView qrCodeIV;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private ScannerLiveView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        qrCodeIV = findViewById(R.id.idIVQrcode);


        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityShowingLabel = findViewById(R.id.sensitivity);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        sensitivityShowingLabel.setText(sharedpreferences.getString(Configurable.SENSITIVITY, "5"));

        camera = (ScannerLiveView) findViewById(R.id.camview);


        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;
        qrgEncoder = new QRGEncoder(sharedpreferences.getString("BroadcastID", ""), null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("Tag", e.toString());
        }


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


        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                Toast.makeText(SetupActivity.this, "Multicast Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                Toast.makeText(SetupActivity.this, "Multicast Scanner Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                Toast.makeText(SetupActivity.this, "Multicast Scanner Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCodeScanned(String data) {

                try {

                    if (Objects.requireNonNull(AESUtils.decrypt(data)).toLowerCase().contains("trusttext") && !sharedpreferences.getString("BroadcastID", "").equals(data)) {

                        //if (Objects.requireNonNull(AESUtils.decrypt(data)).toLowerCase().contains("trusttext")) {
                        goDialog(data);
                    } else
                        Toast.makeText(SetupActivity.this, "Invalid QR", Toast.LENGTH_SHORT).show();

                } catch (Exception E) {
                    Toast.makeText(SetupActivity.this, E.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        decoder.setScanAreaPercent(0.8);
        camera.setDecoder(decoder);
        camera.startScanner();
    }

    @Override
    protected void onPause() {
        camera.stopScanner();
        super.onPause();
    }

    private boolean checkPermission() {
        int camera_permission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int vibrate_permission = ContextCompat.checkSelfPermission(getApplicationContext(), VIBRATE);
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, VIBRATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (!cameraaccepted || !vibrateaccepted) {
                Toast.makeText(this, "Permission Denined \n You cannot use app without providing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static Information information = new Information();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Do you want current device registered as SenseReceiver");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            sharedpreferences.edit().putString("RGR", "RCV").apply();
            sharedpreferences.edit().putString("BroadcastID", data).apply();
            information.setBroadcastId(AESUtils.decrypt(sharedpreferences.getString("BroadcastID", "")));
            dialog.dismiss();


        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}