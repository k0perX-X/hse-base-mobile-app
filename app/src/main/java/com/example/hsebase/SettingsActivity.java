package com.example.hsebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsActivity extends AppCompatActivity implements SensorEventListener {
    private static final String PERMISSION = Manifest.permission.CAMERA;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private ImageView imageView;
    private Button saveButton;
    private Button takePhotoButton;

    private String saveFolder;
    private final String imageName = "avatar.jpg";
    private final String userNameSettingName = "userName";
    private EditText nameTextBox;

    private SensorManager sensorManager;
    private Sensor light;
    private TextView sensorLight;
    private Sensor gyroscope;
    private TextView sensorGyroscope;
    private Sensor accelerometer;
    private TextView sensorAccelerometer;
    private Sensor motion;
    private TextView sensorMotion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorLight = findViewById(R.id.sensorLightText);

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorGyroscope = findViewById(R.id.sensorGyroscopeText);

        motion = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        sensorMotion = findViewById(R.id.sensorMotionText);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorAccelerometer = findViewById(R.id.sensorAccelerometerText);

        TextView allSensors = findViewById(R.id.allSensorsTextView);
        allSensors.setText(String.join("\n",
                (List<String>) sensorManager.getSensorList(Sensor.TYPE_ALL).stream()
                        .map(Sensor::getName).collect(Collectors.toList())));

        imageView = findViewById(R.id.imageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(this::takePhotoClick);

        saveFolder = this.getCacheDir().toString();

        setImageViewPicture();

        nameTextBox = findViewById(R.id.nameTextBox);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this::saveButtonClick);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nameTextBox.setText(preferences.getString(userNameSettingName, nameTextBox.getText().toString()));
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private String[] getStrings(float[] values) {
        String[] s = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            s[i] = String.valueOf(values[i]);
        }
        return s;
    }


    @Override
    public final void onSensorChanged(SensorEvent event) {
        String[] l = getStrings(event.values);
        if (event.sensor == light) {
            sensorLight.setText(String.join(":", l));
        } else if (event.sensor == gyroscope) {
            sensorGyroscope.setText(String.join(":", l));
        } else if (event.sensor == accelerometer) {
            sensorAccelerometer.setText(String.join(":", l));
        } else if (event.sensor == motion) {
            sensorMotion.setText(String.join(":", l));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, motion, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void takePhotoClick(View v) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                showDialogNotPermittedCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
            }
        } else {
            createImageFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Arrays.stream(grantResults).anyMatch(x -> x == REQUEST_PERMISSION_CODE) && requestCode == REQUEST_PERMISSION_CODE) {
            createImageFile();
        }
    }

    private void showDialogNotPermittedCamera() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(R.string.alertSettingsDialogMessage);
        dlgAlert.setTitle(R.string.alertSettingsDialogTitle);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton(R.string.alertSettingsDialogOK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });
        dlgAlert.create().show();
    }

    private void createImageFile() {
        try {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, GetUriFromPath(saveFolder, imageName));
            openSomeActivityForResult(takePhotoIntent);
        } catch (IOException e) {
            Log.e(TAG, "Create file", e);
        }
    }

    private Uri GetUriFromPath(String dir, String fileName) throws IOException {
        File file = new File(dir, fileName);
        return FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider", file);
    }

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setImageViewPicture();
                    }
                }
            });

    private void setImageViewPicture() {
        try {
            imageView.setImageURI(GetUriFromPath(saveFolder, imageName));
        } catch (IOException ex) {
            Log.e(TAG, "Create file", ex);
        }
    }

    public void saveButtonClick(View v) {
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(userNameSettingName, nameTextBox.getText().toString()).apply();
    }
}