package com.example.madlassignment_1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etTime, etPriority;
    private TextView tvLocation;
    private Button btnGetLocation, btnAddReminder, btnViewReminders;

    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private String currentLocationString = "Location: Not set";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI Components
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTime = findViewById(R.id.etTime);
        etPriority = findViewById(R.id.etPriority);
        tvLocation = findViewById(R.id.tvLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnAddReminder = findViewById(R.id.btnAddReminder);
        btnViewReminders = findViewById(R.id.btnViewReminders);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestNotificationPermission();

        btnGetLocation.setOnClickListener(v -> getLastLocation());

        btnAddReminder.setOnClickListener(v -> saveReminder());

        btnViewReminders.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewRemindersActivity.class);
            startActivity(intent);
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocationString = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                    tvLocation.setText("Location: " + currentLocationString);
                } else {
                    currentLocationString = "Lat: 0.0, Lng: 0.0";
                    tvLocation.setText("Location: Not available (Please enable GPS)");
                }
            });
        }
    }

    private void saveReminder() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();
        String priority = etPriority.getText().toString().trim();

        if (title.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "Title and Time are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseHelper.insertReminder(title, description, timeStr, currentLocationString, priority);
        if (result != -1) {
            Toast.makeText(this, "Reminder Added Successfully", Toast.LENGTH_SHORT).show();
            scheduleReminderAlarm((int) result, title, timeStr);

            etTitle.setText("");
            etDescription.setText("");
            etTime.setText("");
            etPriority.setText("");
            tvLocation.setText("Location: Not set");
            currentLocationString = "Location: Not set";
        } else {
            Toast.makeText(this, "Failed to Add Reminder", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleReminderAlarm(int reminderId, String title, String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                // If the time is already passed today, schedule for tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("title", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                // Trigger exact alarm for initial run, repeating every 5 minutes
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        5 * 60 * 1000,
                        pendingIntent
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}