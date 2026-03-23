package com.example.madlassignment_1;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ViewRemindersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);
        
        // Removed potential action bar setup or null checks to keep it simple and robust
        
        recyclerView = findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        reminderList = new ArrayList<>();

        loadReminders();

        adapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(adapter);
    }

    private void loadReminders() {
        Cursor cursor = databaseHelper.getAllReminders();
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
            int timeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME);
            int locIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION);
            int prioIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRIORITY);

            do {
                int id = idIndex != -1 ? cursor.getInt(idIndex) : 0;
                String title = titleIndex != -1 ? cursor.getString(titleIndex) : "";
                String desc = descIndex != -1 ? cursor.getString(descIndex) : "";
                String time = timeIndex != -1 ? cursor.getString(timeIndex) : "";
                String loc = locIndex != -1 ? cursor.getString(locIndex) : "";
                String prio = prioIndex != -1 ? cursor.getString(prioIndex) : "";

                reminderList.add(new Reminder(id, title, desc, time, loc, prio));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
