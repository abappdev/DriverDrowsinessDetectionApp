package ab.appdev.drivemaster.facedetection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import ab.appdev.drivemaster.Configurable;
import ab.appdev.drivemaster.R;
import ab.appdev.drivemaster.SetupActivity;


public final class DriverFaceDetection extends AppCompatActivity {

    private CameraSource mCameraSource = null;

    static int count = 0, count1 = 0;

    private MediaPlayer mp;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private int detectionDelay;

    private static final int RC_HANDLE_GMS = 9001;

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public int flag = 0;
    private SharedPreferences sharedpreferences;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_driver_face_detection);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);

        mPreview.setVisibility(View.VISIBLE);


        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (c == 0) {
            Toast.makeText(getApplicationContext(), "Volume is MUTE", Toast.LENGTH_LONG).show();
        }


        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);


        View decorview = getWindow().getDecorView(); //hide navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorview.setSystemUiVisibility(uiOptions);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    public void onOpenSetup(View v) {
        startActivity(new Intent(getApplicationContext(), SetupActivity.class));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        detectionDelay = Configurable.getDetectionDelayInMilliseconds(
                Integer.parseInt(
                        sharedpreferences.getString(Configurable.SENSITIVITY, "0")));
    }

    private void requestCameraPermission() {
        Log.w(Configurable.TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM);

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction("R.string.ok", listener)
                .show();

    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Toast.makeText(getApplicationContext(), "Dependencies are not yet available. ", Toast.LENGTH_LONG).show();
            Log.w(Configurable.TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(45.0f)
                .build();

    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        stop_playing();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(Configurable.TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(Configurable.TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(Configurable.TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT")
                .setMessage("R.string.no_camera_permission")
                .setPositiveButton("R.string.ok", listener)
                .show();
    }

    private void startCameraSource() {


        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(Configurable.TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public static int incrementer() {
        count++;
        return (count);
    }

    public static int incrementer_1() {
        count1++;
        return (count1);
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

    public void alert_box() {
        play_media();
        runOnUiThread(() -> {
            play_media();

            FirebaseDatabase.getInstance().getReference("/abhishek/").child("INFO").setValue("DETECTED");


            AlertDialog dig;
            dig = new AlertDialog.Builder(DriverFaceDetection.this)
                    .setTitle("Drowsy Alert !!!")
                    .setMessage("Tracker suspects that the driver is experiencing Drowsiness, Touch OK to Stop the Alarm\nSENSITIVITY: " + detectionDelay/1000 + " seconds")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        stop_playing();
                        flag = 0;
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            dig.setOnDismissListener(dialog -> {
                stop_playing();
                flag = 0;
            });
        });


    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private final GraphicOverlay mOverlay;
        private final FaceMonitor mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceMonitor(overlay);
        }


        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        int state_i, state_f = -1;
        long start, end = System.currentTimeMillis();
        long begin, stop;
        int c;

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            if (flag == 0) {
                eye_tracking(face);
            }
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }

        private void eye_tracking(Face face) {
            float l = face.getIsLeftEyeOpenProbability();
            float r = face.getIsRightEyeOpenProbability();
            if (l < 0.50 && r < 0.50) {
                state_i = 0;
            } else {
                state_i = 1;
            }
            if (state_i != state_f) {
                start = System.currentTimeMillis();
                if (state_f == 0) {
                    c = incrementer_1();

                }
                end = start;
                stop = System.currentTimeMillis();
            } else if (state_i == 0 && state_f == 0) {
                begin = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference("/abhishek/").child("INFO").setValue(""+begin);

                if (begin - stop > detectionDelay) {
                    c = incrementer();
                    alert_box();
                    flag = 1;
                }
                begin = stop;
            }
            state_f = state_i;
        }


    }

    @Override
    public void onBackPressed() {

        finish();
    }


}



