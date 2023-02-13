package com.example.hsebase;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeacherActivity extends AppCompatActivity {
    private TextView teacher;
    private TextView status;
    private TextView subject;
    private TextView corp;
    private TextView cabinet;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        final Spinner spinner = findViewById(R.id.groupList);
        List<StudentActivity.Group> groups = new ArrayList<>();
        initGroupList(groups);

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedID) {
                Object item = adapter.getItem(selectedItemPosition);
                Log.d(TAG, "selectedItem" + item);
            }


            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView time = findViewById(R.id.time);
        time.setText(GetTime.initTime());

        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        cabinet = findViewById(R.id.cabinet);
        corp = findViewById(R.id.corp);
        teacher = findViewById(R.id.teacher);

        initData();
    }

    private void initGroupList(List<StudentActivity.Group> groups) {
        groups.add(new StudentActivity.Group(1, "Преподаватель 1"));
        groups.add(new StudentActivity.Group(2, "Преподаватель 2"));
    }

    private void initData() {
        status.setText(R.string.timetableStatusDefault);
        subject.setText(R.string.timetableSubjectDefault);
        cabinet.setText(R.string.timetableCabinetDefault);
        corp.setText(R.string.timetableCorpDefault);
        teacher.setText(R.string.timetableTeacherDefault);
    }

}